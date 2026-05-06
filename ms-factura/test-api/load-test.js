import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Counter, Rate, Trend } from 'k6/metrics';

// Configuración de variables
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const CONTENT_TYPE = 'application/json';

// Métricas personalizadas
const facturaCreatedCounter = new Counter('facturas_created');
const facturaErrorRate = new Rate('factura_errors');
const facturaReadDuration = new Trend('factura_read_duration');

// Opciones de la prueba de carga
export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp-up a 10 usuarios en 30s
    { duration: '1m', target: 50 },   // Escalar a 50 usuarios en 1 minuto
    { duration: '2m', target: 50 },   // Mantener 50 usuarios por 2 minutos
    { duration: '30s', target: 100 }, // Pico de 100 usuarios en 30s
    { duration: '1m', target: 100 },  // Mantener 100 usuarios por 1 minuto
    { duration: '30s', target: 0 },   // Ramp-down a 0 usuarios
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'], // 95% de requests < 500ms, 99% < 1000ms
    http_req_failed: ['rate<0.05'],                  // Tasa de error < 5%
    factura_errors: ['rate<0.05'],                   // Tasa de error específica < 5%
    facturas_created: ['count>100'],                 // Al menos 100 facturas creadas
  },
};

// Datos de prueba para facturas
const facturaData = new SharedArray('facturas', function () {
  return [
    {
      numero: `FACT-${Date.now()}-001`,
      fechaEmision: '2026-04-08',
      total: 1500.50,
      clienteId: 1,
      descripcion: 'Factura por servicios de consultoría'
    },
    {
      numero: `FACT-${Date.now()}-002`,
      fechaEmision: '2026-04-07',
      total: 2300.75,
      clienteId: 2,
      descripcion: 'Factura por desarrollo de software'
    },
    {
      numero: `FACT-${Date.now()}-003`,
      fechaEmision: '2026-04-06',
      total: 850.00,
      clienteId: 1,
      descripcion: 'Factura por mantenimiento'
    },
    {
      numero: `FACT-${Date.now()}-004`,
      fechaEmision: '2026-04-05',
      total: 3200.00,
      clienteId: 3,
      descripcion: 'Factura por servicios cloud'
    },
  ];
});

// Función para generar un número de factura único
function generateFacturaNumero() {
  return `FACT-${Date.now()}-${Math.floor(Math.random() * 10000)}`;
}

// Función para generar una factura aleatoria
function generateRandomFactura() {
  const clienteIds = [1, 2, 3, 4, 5];
  const descripciones = [
    'Servicios de consultoría',
    'Desarrollo de software',
    'Mantenimiento sistemas',
    'Infraestructura cloud',
    'Soporte técnico'
  ];

  return {
    numero: generateFacturaNumero(),
    fechaEmision: '2026-04-08',
    total: Math.random() * 5000 + 100,
    clienteId: clienteIds[Math.floor(Math.random() * clienteIds.length)],
    descripcion: descripciones[Math.floor(Math.random() * descripciones.length)]
  };
}

// Headers comunes
const headers = {
  'Content-Type': CONTENT_TYPE,
  'Accept': CONTENT_TYPE,
};

// Escenario principal de prueba de carga
export default function () {
  const scenario = Math.random();

  // 40% - Crear y obtener factura
  if (scenario < 0.4) {
    createAndGetFactura();
  }
  // 30% - Obtener todas las facturas
  else if (scenario < 0.7) {
    getAllFacturas();
  }
  // 15% - Obtener facturas por cliente
  else if (scenario < 0.85) {
    getFacturasByCliente();
  }
  // 10% - Actualizar factura
  else if (scenario < 0.95) {
    updateFactura();
  }
  // 5% - Eliminar factura
  else {
    deleteFactura();
  }

  sleep(Math.random() * 2 + 1); // Pausa aleatoria entre 1-3 segundos
}

// Función para crear y obtener una factura
function createAndGetFactura() {
  const factura = generateRandomFactura();

  // Crear factura
  const createResponse = http.post(
    `${BASE_URL}/v1/facturas`,
    JSON.stringify(factura),
    { headers, tags: { name: 'CreateFactura' } }
  );

  const createSuccess = check(createResponse, {
    'Create factura status 200 or 201': (r) => r.status === 200 || r.status === 201,
    'Create factura has id': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.id !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  if (createSuccess) {
    facturaCreatedCounter.add(1);

    try {
      const createdFactura = JSON.parse(createResponse.body);

      // Obtener la factura recién creada
      const getResponse = http.get(
        `${BASE_URL}/v1/facturas/${createdFactura.id}`,
        { headers, tags: { name: 'GetFacturaById' } }
      );

      check(getResponse, {
        'Get factura by id status 200': (r) => r.status === 200,
        'Get factura returns correct id': (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.id === createdFactura.id;
          } catch (e) {
            return false;
          }
        },
      });

      facturaReadDuration.add(getResponse.timings.duration);
    } catch (e) {
      facturaErrorRate.add(1);
      console.error('Error parsing create response:', e);
    }
  } else {
    facturaErrorRate.add(1);
  }
}

// Función para obtener todas las facturas
function getAllFacturas() {
  const response = http.get(
    `${BASE_URL}/v1/facturas`,
    { headers, tags: { name: 'GetAllFacturas' } }
  );

  const success = check(response, {
    'Get all facturas status 200': (r) => r.status === 200,
    'Get all facturas returns array': (r) => {
      try {
        const body = JSON.parse(r.body);
        return Array.isArray(body);
      } catch (e) {
        return false;
      }
    },
  });

  if (!success) {
    facturaErrorRate.add(1);
  }

  facturaReadDuration.add(response.timings.duration);
}

// Función para obtener facturas por cliente
function getFacturasByCliente() {
  const clienteId = Math.floor(Math.random() * 5) + 1;

  const response = http.get(
    `${BASE_URL}/v1/facturas/cliente/${clienteId}`,
    { headers, tags: { name: 'GetFacturasByCliente' } }
  );

  const success = check(response, {
    'Get facturas by cliente status 200': (r) => r.status === 200,
    'Get facturas by cliente returns array': (r) => {
      try {
        const body = JSON.parse(r.body);
        return Array.isArray(body);
      } catch (e) {
        return false;
      }
    },
  });

  if (!success) {
    facturaErrorRate.add(1);
  }

  facturaReadDuration.add(response.timings.duration);
}

// Función para actualizar una factura
function updateFactura() {
  // Primero crear una factura para actualizar
  const factura = generateRandomFactura();

  const createResponse = http.post(
    `${BASE_URL}/v1/facturas`,
    JSON.stringify(factura),
    { headers, tags: { name: 'CreateFactura' } }
  );

  if (createResponse.status === 200 || createResponse.status === 201) {
    try {
      const createdFactura = JSON.parse(createResponse.body);

      // Actualizar la factura
      const updatedFactura = {
        ...factura,
        total: factura.total + 100,
        descripcion: factura.descripcion + ' - Actualizada'
      };

      const updateResponse = http.put(
        `${BASE_URL}/v1/facturas/${createdFactura.id}`,
        JSON.stringify(updatedFactura),
        { headers, tags: { name: 'UpdateFactura' } }
      );

      const success = check(updateResponse, {
        'Update factura status 200': (r) => r.status === 200,
        'Update factura has updated data': (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.total === updatedFactura.total;
          } catch (e) {
            return false;
          }
        },
      });

      if (!success) {
        facturaErrorRate.add(1);
      }
    } catch (e) {
      facturaErrorRate.add(1);
      console.error('Error updating factura:', e);
    }
  }
}

// Función para eliminar una factura
function deleteFactura() {
  // Primero crear una factura para eliminar
  const factura = generateRandomFactura();

  const createResponse = http.post(
    `${BASE_URL}/v1/facturas`,
    JSON.stringify(factura),
    { headers, tags: { name: 'CreateFactura' } }
  );

  if (createResponse.status === 200 || createResponse.status === 201) {
    try {
      const createdFactura = JSON.parse(createResponse.body);

      // Eliminar la factura
      const deleteResponse = http.del(
        `${BASE_URL}/v1/facturas/${createdFactura.id}`,
        null,
        { headers, tags: { name: 'DeleteFactura' } }
      );

      const success = check(deleteResponse, {
        'Delete factura status 200 or 204': (r) => r.status === 200 || r.status === 204,
      });

      if (!success) {
        facturaErrorRate.add(1);
      }
    } catch (e) {
      facturaErrorRate.add(1);
      console.error('Error deleting factura:', e);
    }
  }
}

// Escenario de smoke test (prueba rápida)
export function smokeTest() {
  const factura = generateRandomFactura();

  const response = http.post(
    `${BASE_URL}/v1/facturas`,
    JSON.stringify(factura),
    { headers }
  );

  check(response, {
    'Smoke test - status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });
}

// Escenario de spike test (pico de carga)
export function spikeTest() {
  for (let i = 0; i < 10; i++) {
    getAllFacturas();
  }
}
