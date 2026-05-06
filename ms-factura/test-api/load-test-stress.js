import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Stress test: Incrementa gradualmente hasta encontrar el punto de quiebre
export const options = {
  stages: [
    { duration: '2m', target: 50 },   // Ramp-up a 50 usuarios
    { duration: '5m', target: 50 },   // Mantener 50 usuarios
    { duration: '2m', target: 100 },  // Escalar a 100 usuarios
    { duration: '5m', target: 100 },  // Mantener 100 usuarios
    { duration: '2m', target: 200 },  // Escalar a 200 usuarios
    { duration: '5m', target: 200 },  // Mantener 200 usuarios
    { duration: '2m', target: 300 },  // Escalar a 300 usuarios
    { duration: '5m', target: 300 },  // Mantener 300 usuarios
    { duration: '2m', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% de requests < 2s (más tolerante)
    http_req_failed: ['rate<0.1'],     // Tasa de error < 10%
  },
};

const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

function generateFactura() {
  return {
    numero: `FACT-STRESS-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
    fechaEmision: '2026-04-08',
    total: Math.random() * 5000 + 100,
    clienteId: Math.floor(Math.random() * 10) + 1,
    descripcion: 'Factura stress test'
  };
}

export default function () {
  const scenario = Math.random();

  // 50% - Lecturas (GET)
  if (scenario < 0.5) {
    const response = http.get(`${BASE_URL}/v1/facturas`, { headers });
    check(response, {
      'Get all facturas status OK': (r) => r.status === 200,
    });
  }
  // 30% - Crear factura
  else if (scenario < 0.8) {
    const factura = generateFactura();
    const response = http.post(
      `${BASE_URL}/v1/facturas`,
      JSON.stringify(factura),
      { headers }
    );
    check(response, {
      'Create factura status OK': (r) => r.status === 200 || r.status === 201,
    });
  }
  // 20% - Obtener por cliente
  else {
    const clienteId = Math.floor(Math.random() * 10) + 1;
    const response = http.get(`${BASE_URL}/v1/facturas/cliente/${clienteId}`, { headers });
    check(response, {
      'Get by cliente status OK': (r) => r.status === 200,
    });
  }

  sleep(Math.random() * 0.5); // Pausa muy corta para stress
}
