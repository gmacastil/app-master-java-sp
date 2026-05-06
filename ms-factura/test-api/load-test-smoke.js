import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Smoke test: Prueba rápida con 10 usuarios durante 1 minuto
export const options = {
  vus: 10,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(99)<1500'],
    http_req_failed: ['rate<0.01'],
  },
};

const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

export default function () {
  // 1. Health check
  let response = http.get(`${BASE_URL}/actuator/health`, { headers });
  check(response, {
    'Health check is status 200': (r) => r.status === 200,
  });

  // 2. Obtener todas las facturas
  response = http.get(`${BASE_URL}/v1/facturas`, { headers });
  check(response, {
    'Get all facturas is status 200': (r) => r.status === 200,
  });

  // 3. Crear una factura
  const factura = {
    numero: `FACT-SMOKE-${Date.now()}`,
    fechaEmision: '2026-04-08',
    total: 1500.50,
    clienteId: 1,
    descripcion: 'Factura smoke test'
  };

  response = http.post(
    `${BASE_URL}/v1/facturas`,
    JSON.stringify(factura),
    { headers }
  );

  check(response, {
    'Create factura is status 200 or 201': (r) => r.status === 200 || r.status === 201,
  });

  // 4. Obtener factura por cliente
  response = http.get(`${BASE_URL}/v1/facturas/cliente/1`, { headers });
  check(response, {
    'Get facturas by cliente is status 200': (r) => r.status === 200,
  });

  sleep(1);
}
