#!/bin/bash

# Script para ejecutar pruebas de carga con K6
# Uso: ./run-load-tests.sh [test-type] [options]

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración por defecto
BASE_URL="${BASE_URL:-http://localhost:8080}"
RESULTS_DIR="./results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Crear directorio de resultados si no existe
mkdir -p "$RESULTS_DIR"

# Función para imprimir mensajes
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Función para verificar que k6 está instalado
check_k6() {
    if ! command -v k6 &> /dev/null; then
        print_error "k6 no está instalado. Instálalo desde https://k6.io/docs/get-started/installation/"
        exit 1
    fi
    print_success "k6 está instalado: $(k6 version)"
}

# Función para verificar que el servidor está corriendo
check_server() {
    print_info "Verificando servidor en $BASE_URL..."

    if curl -s -f "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        print_success "Servidor está disponible"
        return 0
    else
        print_error "Servidor no está disponible en $BASE_URL"
        print_warning "Asegúrate de que la aplicación está corriendo"
        exit 1
    fi
}

# Función para ejecutar un test
run_test() {
    local test_name=$1
    local test_file=$2
    local output_file="${RESULTS_DIR}/${test_name}_${TIMESTAMP}.json"

    print_info "=========================================="
    print_info "Ejecutando: $test_name"
    print_info "=========================================="

    if k6 run --env BASE_URL="$BASE_URL" --out json="$output_file" "$test_file"; then
        print_success "✓ $test_name completado exitosamente"
        print_info "Resultados guardados en: $output_file"
        return 0
    else
        print_error "✗ $test_name falló"
        return 1
    fi
}

# Función para mostrar uso
show_usage() {
    cat << EOF
Uso: $0 [OPCION]

Opciones:
    smoke       Ejecutar smoke test (1 usuario, 1 minuto)
    load        Ejecutar load test completo (10-100 usuarios, ~5 minutos)
    stress      Ejecutar stress test (50-300 usuarios, ~30 minutos)
    spike       Ejecutar spike test (10-500 usuarios, ~3.5 minutos)
    soak        Ejecutar soak test (50 usuarios, 1 hora)
    all         Ejecutar todos los tests en secuencia
    suite       Ejecutar suite básica (smoke + load + spike)
    help        Mostrar esta ayuda

Variables de entorno:
    BASE_URL    URL base del servicio (default: http://localhost:8080)

Ejemplos:
    $0 smoke
    BASE_URL=http://localhost:9090 $0 load
    $0 suite

EOF
}

# Función para ejecutar suite completa
run_full_suite() {
    print_info "=========================================="
    print_info "EJECUTANDO SUITE COMPLETA DE PRUEBAS"
    print_info "=========================================="

    local failed=0

    run_test "smoke" "load-test-smoke.js" || ((failed++))

    if [ $failed -eq 0 ]; then
        run_test "load" "load-test.js" || ((failed++))
        run_test "stress" "load-test-stress.js" || ((failed++))
        run_test "spike" "load-test-spike.js" || ((failed++))
        run_test "soak" "load-test-soak.js" || ((failed++))
    else
        print_error "Smoke test falló. Abortando suite completa."
        return 1
    fi

    if [ $failed -eq 0 ]; then
        print_success "=========================================="
        print_success "TODOS LOS TESTS COMPLETADOS EXITOSAMENTE"
        print_success "=========================================="
    else
        print_error "=========================================="
        print_error "$failed TEST(S) FALLARON"
        print_error "=========================================="
        return 1
    fi
}

# Función para ejecutar suite básica
run_basic_suite() {
    print_info "=========================================="
    print_info "EJECUTANDO SUITE BÁSICA"
    print_info "=========================================="

    local failed=0

    run_test "smoke" "load-test-smoke.js" || ((failed++))

    if [ $failed -eq 0 ]; then
        run_test "load" "load-test.js" || ((failed++))
        run_test "spike" "load-test-spike.js" || ((failed++))
    else
        print_error "Smoke test falló. Abortando suite."
        return 1
    fi

    if [ $failed -eq 0 ]; then
        print_success "=========================================="
        print_success "SUITE BÁSICA COMPLETADA EXITOSAMENTE"
        print_success "=========================================="
    else
        print_error "=========================================="
        print_error "$failed TEST(S) FALLARON"
        print_error "=========================================="
        return 1
    fi
}

# Main script
main() {
    print_info "K6 Load Testing Script"
    print_info "Base URL: $BASE_URL"
    print_info "Results Directory: $RESULTS_DIR"
    echo ""

    # Verificar instalación de k6
    check_k6

    # Verificar servidor
    check_server

    echo ""

    # Procesar comando
    case "${1:-help}" in
        smoke)
            run_test "smoke" "load-test-smoke.js"
            ;;
        load)
            run_test "load" "load-test.js"
            ;;
        stress)
            run_test "stress" "load-test-stress.js"
            ;;
        spike)
            run_test "spike" "load-test-spike.js"
            ;;
        soak)
            print_warning "Soak test durará 1 hora. ¿Continuar? (y/n)"
            read -r confirm
            if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
                run_test "soak" "load-test-soak.js"
            else
                print_info "Soak test cancelado"
            fi
            ;;
        all)
            run_full_suite
            ;;
        suite)
            run_basic_suite
            ;;
        help|--help|-h)
            show_usage
            ;;
        *)
            print_error "Opción desconocida: $1"
            echo ""
            show_usage
            exit 1
            ;;
    esac
}

# Ejecutar script
main "$@"
