#!/bin/bash

# Script para analizar resultados de K6
# Uso: ./analyze-results.sh [archivo-resultados.json]

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_metric() {
    echo -e "${GREEN}$1:${NC} $2"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Verificar archivo
if [ -z "$1" ]; then
    echo "Uso: $0 <archivo-resultados.json>"
    echo ""
    echo "Archivos disponibles:"
    ls -lh results/*.json 2>/dev/null || echo "No hay archivos de resultados"
    exit 1
fi

RESULT_FILE=$1

if [ ! -f "$RESULT_FILE" ]; then
    echo "Error: Archivo no encontrado: $RESULT_FILE"
    exit 1
fi

print_header "ANÁLISIS DE RESULTADOS K6"
print_metric "Archivo" "$RESULT_FILE"
print_metric "Tamaño" "$(du -h "$RESULT_FILE" | cut -f1)"
echo ""

# Extraer métricas principales usando jq
if command -v jq &> /dev/null; then
    print_header "MÉTRICAS PRINCIPALES"

    # Contar checks
    total_checks=$(jq -r 'select(.type=="Point" and .metric=="checks") | .data.value' "$RESULT_FILE" | awk '{s+=$1} END {print s}')
    echo -e "${GREEN}Total Checks:${NC} $total_checks"

    # Contar requests
    total_requests=$(jq -r 'select(.type=="Point" and .metric=="http_reqs") | .data.value' "$RESULT_FILE" | awk '{s+=$1} END {print s}')
    echo -e "${GREEN}Total HTTP Requests:${NC} $total_requests"

    # Duraciones
    echo ""
    print_header "TIEMPOS DE RESPUESTA"
    jq -r 'select(.type=="Point" and .metric=="http_req_duration") | "\(.data.tags.name // "general"): \(.data.value)ms"' "$RESULT_FILE" | head -20

    # Errores
    echo ""
    print_header "ERRORES"
    failed_requests=$(jq -r 'select(.type=="Point" and .metric=="http_req_failed" and .data.value==1)' "$RESULT_FILE" | wc -l)

    if [ "$failed_requests" -gt 0 ]; then
        print_error "Requests fallidos: $failed_requests"

        # Mostrar algunos errores
        echo ""
        echo "Ejemplos de errores:"
        jq -r 'select(.type=="Point" and .metric=="http_req_failed" and .data.value==1) | "\(.data.tags.name // "unknown"): Status \(.data.tags.status // "unknown")"' "$RESULT_FILE" | head -10
    else
        echo -e "${GREEN}✓ No se encontraron errores HTTP${NC}"
    fi

    # Resumen de thresholds
    echo ""
    print_header "THRESHOLDS"
    jq -r 'select(.type=="Metric" and .data.thresholds!=null) | "\(.metric): \(.data.thresholds | to_entries[] | "\(.key): \(.value.ok)")"' "$RESULT_FILE" | head -20

else
    echo -e "${YELLOW}jq no está instalado. Análisis básico limitado.${NC}"
    echo "Instala jq para análisis detallado: sudo apt-get install jq"
    echo ""

    # Análisis básico sin jq
    print_header "RESUMEN BÁSICO"
    echo "Total de líneas: $(wc -l < "$RESULT_FILE")"
    echo "Métricas encontradas: $(grep -c '"type":"Point"' "$RESULT_FILE")"
fi

# Estadísticas del archivo
echo ""
print_header "INFORMACIÓN DEL ARCHIVO"
print_metric "Fecha creación" "$(stat -c %y "$RESULT_FILE" 2>/dev/null || stat -f "%Sm" "$RESULT_FILE")"
print_metric "Ubicación" "$RESULT_FILE"

echo ""
echo -e "${BLUE}Para análisis más detallado, considera usar:${NC}"
echo "  - K6 Cloud: https://app.k6.io/"
echo "  - Grafana + InfluxDB"
echo "  - k6-reporter: https://github.com/benc-uk/k6-reporter"
