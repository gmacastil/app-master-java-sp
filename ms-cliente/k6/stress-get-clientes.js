import http from 'k6/http';
import { check, sleep } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://api.lite.com';
const endpoint = '/v1/clientes';

export const options = {
  scenarios: {
    stress_clientes: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '30s', target: 20 },
        { duration: '1m', target: 80 },
        { duration: '1m', target: 150 },
        { duration: '30s', target: 0 }
      ],
      gracefulRampDown: '20s'
    }
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1200'],
    checks: ['rate>0.95']
  }
};

export default function () {
  const res = http.get(`${baseUrl}${endpoint}`, {
    headers: {
      Accept: 'application/json'
    },
    tags: {
      name: 'GET /v1/clientes'
    }
  });

  check(res, {
    'status es 200': (r) => r.status === 200,
    'tiempo menor a 2s': (r) => r.timings.duration < 2000
  });

  sleep(0.2);
}
