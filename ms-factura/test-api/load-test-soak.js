import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Soak test: Carga constante prolongada para detectar degradación
export const options = {
  stages: [
    { duration: '2m', target: 50 },  // Ramp-up
    { duration: '60m', target: 50 }, // Mantener por 1 hora
    { duration: '2m', target: 0 },   // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.05'],
  },
};

const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

function generateFactura() {
  return {
    numero: `FACT-SOAK-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
    fechaEmision: '2026-04-08',
    total: Math.random() * 5000 + 100,
    clienteId: Math.floor(Math.random() * 5) + 1,
    descripcion: 'Factura soak test'
  };
}

export default function () {
  const scenario = Math.random();

  // 40% - Obtener todas
  if (scenario < 0.4) {
    const response = http.get(`${BASE_URL}/v1/facturas`, { headers });
    check(response, {
      'Get all status OK': (r) => r.status === 200,
    });
  }
  // 30% - Crear factura
  else if (scenario < 0.7) {
    const factura = generateFactura();
    const response = http.post(
      `${BASE_URL}/v1/facturas`,
      JSON.stringify(factura),
      { headers }
    );
    check(response, {
      'Create status OK': (r) => r.status === 200 || r.status === 201,
    });
  }
  // 20% - Obtener por cliente
  else if (scenario < 0.9) {
    const clienteId = Math.floor(Math.random() * 5) + 1;
    const response = http.get(`${BASE_URL}/v1/facturas/cliente/${clienteId}`, { headers });
    check(response, {
      'Get by cliente status OK': (r) => r.status === 200,
    });
  }
  // 10% - CRUD completo
  else {
    // Crear
    const factura = generateFactura();
    let response = http.post(
      `${BASE_URL}/v1/facturas`,
      JSON.stringify(factura),
      { headers }
    );

    if (response.status === 200 || response.status === 201) {
      try {
        const created = JSON.parse(response.body);

        // Actualizar
        const updated = { ...factura, total: factura.total + 100 };
        response = http.put(
          `${BASE_URL}/v1/facturas/${created.id}`,
          JSON.stringify(updated),
          { headers }
        );

        // Eliminar
        http.del(`${BASE_URL}/v1/facturas/${created.id}`, null, { headers });
      } catch (e) {
        console.error('CRUD error:', e);
      }
    }
  }

  sleep(Math.random() * 3 + 1); // 1-4 segundos
}
