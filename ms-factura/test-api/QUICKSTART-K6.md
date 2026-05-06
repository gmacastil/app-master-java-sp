# Quick Start - K6 Load Testing

## Instalación Rápida

```bash
# Linux
sudo apt-get install k6

# macOS
brew install k6

# Windows
choco install k6
```

## Uso Básico

### 1. Ejecutar Smoke Test (Verificación Rápida)
```bash
cd test-api
./run-load-tests.sh smoke
```

### 2. Ejecutar Prueba de Carga Completa
```bash
./run-load-tests.sh load
```

### 3. Ejecutar Suite Básica
```bash
./run-load-tests.sh suite
```

## Scripts Disponibles

| Script | Descripción | Duración | Usuarios |
|--------|-------------|----------|----------|
| `smoke` | Verificación básica | 1 min | 1 |
| `load` | Carga completa | ~5 min | 10-100 |
| `stress` | Encuentra límites | ~30 min | 50-300 |
| `spike` | Picos de tráfico | ~3.5 min | 10-500 |
| `soak` | Resistencia prolongada | 1 hora | 50 |

## Comandos Directos

```bash
# Ejecutar cualquier script directamente
k6 run load-test-smoke.js

# Con URL personalizada
k6 run --env BASE_URL=http://localhost:9090 load-test.js

# Generar reporte JSON
k6 run --out json=results.json load-test.js
```

## Verificar Servidor

```bash
# Antes de ejecutar, verifica que el servidor esté corriendo
curl http://localhost:8080/actuator/health
```

## Analizar Resultados

```bash
# Usar el script de análisis
./analyze-results.sh results/load_20260422_193045.json
```

## Documentación Completa

Ver [README-K6.md](README-K6.md) para documentación detallada.
