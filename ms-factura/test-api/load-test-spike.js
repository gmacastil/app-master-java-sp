import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Spike test: Pico repentino de carga
export const options = {
  stages: [
    { duration: '10s', target: 10 },   // Carga normal
    { duration: '1m', target: 10 },    // Mantener carga normal
    { duration: '10s', target: 500 },  // Pico repentino a 500 usuarios
    { duration: '1m', target: 500 },   // Mantener pico
    { duration: '10s', target: 10 },   // Bajar a carga normal
    { duration: '1m', target: 10 },    // Mantener carga normal
    { duration: '10s', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<3000'], // Más tolerante durante picos
    http_req_failed: ['rate<0.2'],     // Hasta 20% de errores aceptable en picos
  },
};

const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

export default function () {
  // Durante el spike, principalmente operaciones de lectura
  const response = http.get(`${BASE_URL}/v1/facturas`, { headers });

  check(response, {
    'Get facturas status OK': (r) => r.status === 200 || r.status === 429, // 429 = Too Many Requests
  });

  sleep(0.1); // Pausa mínima
}
