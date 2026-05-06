# Pruebas de Carga con K6 - Microservicio Facturas

Este directorio contiene scripts de pruebas de carga usando K6 para el microservicio de facturas.

## Requisitos Previos

### Instalación de K6

**Linux:**
```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

**macOS:**
```bash
brew install k6
```

**Windows:**
```bash
choco install k6
```

O descarga desde: https://k6.io/docs/get-started/installation/

## Scripts Disponibles

### 1. `load-test.js` - Prueba de Carga Completa
Prueba de carga realista con múltiples escenarios y distribución de operaciones.

**Características:**
- Ramp-up progresivo de 10 a 100 usuarios
- Duración total: ~5 minutos
- Distribución de operaciones:
  - 40% Crear y obtener facturas
  - 30% Obtener todas las facturas
  - 15% Obtener facturas por cliente
  - 10% Actualizar facturas
  - 5% Eliminar facturas
- Métricas personalizadas y thresholds

**Ejecutar:**
```bash
k6 run load-test.js

# Con URL personalizada
k6 run --env BASE_URL=http://localhost:8080 load-test.js

# Generar reporte HTML
k6 run --out json=results.json load-test.js
```

### 2. `load-test-smoke.js` - Smoke Test
Prueba rápida de verificación básica con 1 usuario durante 1 minuto.

**Propósito:**
- Verificar que el sistema funciona correctamente
- Validar endpoints principales
- Ejecutar antes de pruebas más pesadas

**Ejecutar:**
```bash
k6 run load-test-smoke.js
```

### 3. `load-test-stress.js` - Stress Test
Prueba de estrés que incrementa gradualmente la carga hasta encontrar el punto de quiebre.

**Características:**
- Incremento progresivo: 50 → 100 → 200 → 300 usuarios
- Duración total: ~30 minutos
- Identifica límites del sistema

**Ejecutar:**
```bash
k6 run load-test-stress.js
```

### 4. `load-test-spike.js` - Spike Test
Prueba de picos repentinos de tráfico.

**Características:**
- Pico repentino de 10 a 500 usuarios
- Evalúa recuperación del sistema
- Duración total: ~3.5 minutos

**Ejecutar:**
```bash
k6 run load-test-spike.js
```

### 5. `load-test-soak.js` - Soak Test
Prueba de resistencia prolongada para detectar memory leaks y degradación.

**Características:**
- Carga constante de 50 usuarios
- Duración: 1 hora
- Detecta problemas de memoria y estabilidad

**Ejecutar:**
```bash
k6 run load-test-soak.js
```

## Opciones Avanzadas

### Configurar Variables de Entorno

```bash
# URL base personalizada
export BASE_URL=http://localhost:8080
k6 run load-test.js

# O directamente en el comando
k6 run --env BASE_URL=http://production-url.com load-test.js
```

### Generar Reportes

**JSON Output:**
```bash
k6 run --out json=results.json load-test.js
```

**InfluxDB:**
```bash
k6 run --out influxdb=http://localhost:8086/k6 load-test.js
```

**Grafana Cloud:**
```bash
k6 run --out cloud load-test.js
```

**Múltiples salidas:**
```bash
k6 run --out json=results.json --out influxdb=http://localhost:8086/k6 load-test.js
```

### Configuración de Usuarios y Duración

```bash
# Usuarios virtuales específicos
k6 run --vus 50 --duration 5m load-test.js

# Usar stages personalizados (esto sobrescribe las opciones del script)
k6 run --stage 30s:10,1m:50,2m:100,30s:0 load-test.js
```

## Métricas y Thresholds

### Métricas Estándar de K6

- **http_req_duration**: Tiempo de respuesta de las peticiones
- **http_req_failed**: Tasa de errores HTTP
- **http_reqs**: Total de peticiones realizadas
- **iterations**: Número de iteraciones completadas
- **vus**: Usuarios virtuales activos

### Métricas Personalizadas

- **facturas_created**: Contador de facturas creadas exitosamente
- **factura_errors**: Tasa de errores específicos de facturas
- **factura_read_duration**: Tiempo de lectura de facturas

### Thresholds Configurados

```javascript
thresholds: {
  http_req_duration: ['p(95)<500', 'p(99)<1000'], // Percentiles
  http_req_failed: ['rate<0.05'],                  // Tasa de error < 5%
  facturas_created: ['count>100'],                 // Mínimo de facturas creadas
}
```

## Análisis de Resultados

### Salida en Consola

K6 muestra un resumen al finalizar:

```
checks.........................: 95.50% ✓ 1910      ✗ 90
data_received..................: 1.2 MB 23 kB/s
data_sent......................: 534 kB 10 kB/s
http_req_blocked...............: avg=1.23ms   min=0µs     med=0µs      max=123.4ms
http_req_connecting............: avg=0.67ms   min=0µs     med=0µs      max=67.8ms
http_req_duration..............: avg=234.56ms min=12.3ms  med=198.7ms  max=1.2s
http_req_failed................: 4.50%  ✓ 90       ✗ 1910
http_req_receiving.............: avg=0.12ms   min=0µs     med=0.08ms   max=12.3ms
http_reqs......................: 2000   37.735849/s
iterations.....................: 2000   37.735849/s
```

### Interpretación

✅ **Prueba Exitosa** si:
- Todos los checks están por encima del 95%
- http_req_failed < 5%
- Percentiles p95 y p99 dentro de thresholds
- No hay timeouts ni errores de conexión

⚠️ **Atención** si:
- Checks entre 85-95%
- Errores entre 5-10%
- Tiempos de respuesta aumentan significativamente

❌ **Prueba Fallida** si:
- Checks < 85%
- Errores > 10%
- Timeouts frecuentes
- Thresholds no cumplidos

## Escenarios de Uso

### 1. Antes de Deployment
```bash
# Smoke test rápido
k6 run load-test-smoke.js

# Si pasa, ejecutar carga completa
k6 run load-test.js
```

### 2. Prueba de Capacidad
```bash
# Stress test para encontrar límites
k6 run load-test-stress.js
```

### 3. Validación de Estabilidad
```bash
# Soak test durante la noche
k6 run load-test-soak.js
```

### 4. Prueba de Elasticidad
```bash
# Spike test para validar auto-scaling
k6 run load-test-spike.js
```

## Troubleshooting

### El servidor no responde
```bash
# Verificar que la aplicación está corriendo
curl http://localhost:8080/actuator/health

# Verificar puerto
netstat -tulpn | grep 8080
```

### Errores de conexión
```bash
# Aumentar límites del sistema
ulimit -n 10000

# Verificar conectividad
ping localhost
```

### Métricas de sistema
```bash
# Monitorear CPU y memoria durante las pruebas
htop

# Ver logs de la aplicación
tail -f ../logs/application.log
```

## Mejores Prácticas

1. **Comenzar con Smoke Test**: Siempre ejecuta primero el smoke test
2. **Incremento Gradual**: No saltes directamente a cargas altas
3. **Monitorear el Sistema**: Observa CPU, memoria y conexiones de BD
4. **Limpiar Base de Datos**: Entre pruebas, limpia datos de test
5. **Documentar Resultados**: Guarda resultados para comparaciones futuras
6. **Ambiente Dedicado**: Pruebas de carga en ambiente separado de desarrollo

## Scripts de Utilidad

### Ejecutar Suite Completa
```bash
#!/bin/bash
echo "=== Smoke Test ==="
k6 run load-test-smoke.js

if [ $? -eq 0 ]; then
  echo "=== Load Test ==="
  k6 run load-test.js
  
  echo "=== Spike Test ==="
  k6 run load-test-spike.js
else
  echo "Smoke test falló. Abortando."
  exit 1
fi
```

### Generar Reporte HTML (requiere xk6-reporter)
```bash
# Instalar xk6 con reporter
xk6 build --with github.com/benc-uk/k6-reporter@latest

# Ejecutar con reporte HTML
./k6 run --out json=results.json load-test.js
node generate-report.js results.json
```

## Referencias

- [K6 Documentation](https://k6.io/docs/)
- [K6 Cloud](https://app.k6.io/)
- [K6 Examples](https://k6.io/docs/examples/)
- [Test Types](https://k6.io/docs/test-types/introduction/)

## Contacto y Soporte

Para dudas o problemas con las pruebas de carga, contacta al equipo de QA o DevOps.
