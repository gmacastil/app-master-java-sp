# Integración con Microservicio de Facturas

## Descripción
Este documento describe la integración del microservicio `ms-cliente` con el microservicio de facturas, permitiendo consultar las facturas asociadas a un cliente mediante **Spring Cloud OpenFeign** siguiendo los principios de **Arquitectura Hexagonal**.

## Cambios Realizados

### 1. Dependencias Agregadas
- **spring-cloud-starter-openfeign**: Cliente HTTP declarativo para comunicación entre microservicios

```gradle
ext {
    springCloudVersion = '2024.0.0'
}

dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

### 2. Habilitación de Feign

#### MsClienteApplication.java
```java
@SpringBootApplication
@EnableFeignClients
public class MsClienteApplication {
    // ...
}
```

### 3. Configuración

#### application.properties
```properties
# URL del servicio de facturas (configurable vía variable de entorno)
facturas.service.url=${FACTURAS_SERVICE_URL:http://localhost:8080}
```

### 4. Nuevos Componentes

#### FacturaDTO
- **Ubicación**: `com.lite.ms_cliente.domain.dto.FacturaDTO`
- **Capa**: Domain
- **Propósito**: Representa la estructura de una factura del servicio externo

```java
public record FacturaDTO(
    Long id,
    String numero,
    LocalDate fechaEmision,
    BigDecimal total,
    Long clienteId,
    String descripcion
)
```

#### FacturasFeignClient
- **Ubicación**: `com.lite.ms_cliente.infrastructure.client.FacturasFeignClient`
- **Capa**: Infrastructure (Adaptador de salida)
- **Propósito**: Cliente Feign declarativo para comunicarse con el servicio de facturas
- **Configuración**: 
  - `name`: "facturas-service"
  - `url`: Configurable vía `${facturas.service.url}`

```java
@FeignClient(name = "facturas-service", url = "${facturas.service.url}")
public interface FacturasFeignClient {
    @GetMapping("/v1/facturas/cliente/{clienteId}")
    List<FacturaDTO> getFacturasByClienteId(@PathVariable("clienteId") Long clienteId);
}
```

#### ClienteUseCase (Actualizado)
- **Ubicación**: `com.lite.ms_cliente.application.service.ClienteUseCase`
- **Capa**: Application (Puerto)
- **Método agregado**: `List<FacturaDTO> getFacturasByClienteId(String clienteId)`

#### ClienteService (Actualizado)
- **Ubicación**: `com.lite.ms_cliente.application.service.ClienteService`
- **Capa**: Application (Servicio)
- **Propósito**: Implementa la lógica de negocio, orquestando la consulta al cliente y la obtención de facturas
- **Manejo de errores**:
  - Convierte el ID de String a Long
  - Maneja `FeignException.NotFound` retornando lista vacía
  - Maneja otros `FeignException` lanzando `RuntimeException`

### 5. Nuevo Endpoint

#### GET /v1/clientes/{id}/facturas
- **Descripción**: Obtiene todas las facturas asociadas a un cliente
- **Parámetros**: 
  - `id` (path): ID del cliente (debe ser numérico)
- **Respuesta**: Lista de facturas (`FacturaDTO[]`)
- **Códigos de respuesta**:
  - `200 OK`: Facturas encontradas
  - `404 NOT FOUND`: Cliente no existe
  - `400 BAD REQUEST`: ID de cliente no numérico

**Ejemplo de uso:**
```http
GET http://localhost:8081/v1/clientes/1/facturas
Accept: application/json
```

## Arquitectura Hexagonal

La integración sigue los principios de arquitectura hexagonal (puertos y adaptadores):

```
┌──────────────────────────────────────────────────────────────┐
│                        ms-cliente                             │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  Infrastructure Layer (Adaptadores)                 │    │
│  │                                                      │    │
│  │  ┌──────────────────┐      ┌────────────────────┐  │    │
│  │  │ ClienteController│      │FacturasFeignClient │  │    │
│  │  │   (Entrada)      │      │    (Salida)        │  │    │
│  │  └────────┬─────────┘      └──────────┬─────────┘  │    │
│  └───────────┼────────────────────────────┼────────────┘    │
│              │                            │                  │
│  ┌───────────▼────────────────────────────▼────────────┐    │
│  │  Application Layer (Lógica de Negocio)             │    │
│  │                                                      │    │
│  │  ┌──────────────┐      ┌──────────────────────┐    │    │
│  │  │ClienteUseCase│◀─────│  ClienteService      │    │    │
│  │  │  (Puerto)    │      │  (Implementación)    │    │    │
│  │  └──────────────┘      └──────────────────────┘    │    │
│  └──────────────────────────────────────────────────────┘    │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐    │
│  │  Domain Layer                                        │    │
│  │                                                       │    │
│  │  ┌────────┐  ┌────────────┐  ┌────────────┐        │    │
│  │  │Cliente │  │ClienteDTO  │  │FacturaDTO  │        │    │
│  │  └────────┘  └────────────┘  └────────────┘        │    │
│  └──────────────────────────────────────────────────────┘    │
└───────────────────────────────┬───────────────────────────────┘
                                │
                                │ HTTP (Feign)
                                ▼
                     ┌────────────────────┐
                     │   ms-facturas      │
                     │   (port 8080)      │
                     └────────────────────┘
```

## Flujo de Integración

1. **Cliente HTTP** → `ClienteController.getFacturasByClienteId()`
2. **Controller** verifica que el cliente existe
3. **Controller** → `ClienteUseCase.getFacturasByClienteId()`
4. **ClienteService** convierte el ID y maneja errores
5. **ClienteService** → `FacturasFeignClient.getFacturasByClienteId()`
6. **Feign Client** realiza llamada HTTP a ms-facturas
7. **Respuesta** se propaga de vuelta por las capas

## Consideraciones Importantes

### 1. Conversión de IDs
- **MongoDB (ms-cliente)**: Usa IDs tipo `String` (ObjectId)
- **Facturas**: Usa IDs tipo `Long` (numérico secuencial)
- La conversión se realiza en el controlador con `Long.parseLong(id)`
- Si necesitas una estrategia diferente, modifica el método en `ClienteController:113-129`

### 2. Manejo de Errores
El servicio implementa manejo robusto de errores:
- **NumberFormatException**: ID de cliente no numérico → `IllegalArgumentException`
- **FeignException.NotFound (404)**: Cliente sin facturas → Retorna lista vacía
- **Otros FeignException**: Problemas de conectividad → `RuntimeException` con detalles

### 3. Logging
Se registran logs estructurados para:
- Consultas realizadas al servicio de facturas
- Número de facturas encontradas
- Errores en la comunicación

### 4. Configuración del Servicio
Para cambiar la URL del servicio de facturas:

**Opción 1 - Variable de entorno:**
```bash
export FACTURAS_SERVICE_URL=http://facturas-service:8080
```

**Opción 2 - application.properties:**
```properties
facturas.service.url=http://facturas-service:8080
```

## Testing

### Prueba Manual
1. Iniciar el servicio de facturas (puerto 8080)
2. Iniciar ms-cliente (puerto 8081)
3. Crear un cliente y anotar su ID
4. Crear facturas para ese cliente en el servicio de facturas
5. Consultar: `GET /v1/clientes/{id}/facturas`

### Archivos HTTP
- **clientes.http**: Endpoints del microservicio de clientes
- **facturas-service-reference.http**: Referencia de endpoints del servicio de facturas

## Ventajas de OpenFeign

1. **Código Declarativo**: Define clientes HTTP como interfaces simples
2. **Menos Boilerplate**: No necesitas escribir código de serialización/deserialización
3. **Integración con Spring**: Inyección de dependencias automática
4. **Configuración Externa**: URLs y timeouts configurables vía properties
5. **Logging Built-in**: Logs de peticiones/respuestas configurables
6. **Compatible con Resilience4j**: Fácil integración con circuit breakers

## Ventajas de Arquitectura Hexagonal

1. **Separación de Responsabilidades**: 
   - **Domain**: Lógica de negocio pura
   - **Application**: Casos de uso y orquestación
   - **Infrastructure**: Detalles técnicos (HTTP, BD, etc.)

2. **Testabilidad**: Los casos de uso son independientes de la infraestructura
3. **Mantenibilidad**: Cambios en adaptadores no afectan la lógica de negocio
4. **Flexibilidad**: Fácil reemplazar Feign por otro cliente HTTP sin tocar el dominio

## Próximos Pasos Sugeridos

1. **Circuit Breaker**: Implementar Resilience4j con `@CircuitBreaker` en el Feign Client
2. **Cache**: Agregar `@Cacheable` en el método del service
3. **Tests**: 
   - Tests unitarios de `ClienteService` mockeando `FacturasFeignClient`
   - Tests de integración con WireMock o Spring Cloud Contract
4. **Configuración Feign**:
   ```yaml
   feign:
     client:
       config:
         facturas-service:
           connectTimeout: 5000
           readTimeout: 5000
           loggerLevel: basic
   ```
5. **Retry**: Configurar política de reintentos con Feign Retryer
6. **Métricas**: Agregar métricas con Micrometer para monitoreo
7. **Fallback**: Implementar métodos fallback con `@FeignClient(fallback = ...)`

## Documentación API

La documentación OpenAPI/Swagger está disponible en:
```
http://localhost:8081/swagger-ui.html
```
