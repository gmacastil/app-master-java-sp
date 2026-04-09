# Guía de Pruebas de API - MS Factura

Esta guía proporciona información sobre cómo probar los endpoints del microservicio de Facturas.

## Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/v1/facturas` | Crear una nueva factura |
| GET | `/v1/facturas` | Obtener todas las facturas |
| GET | `/v1/facturas/{id}` | Obtener factura por ID |
| PUT | `/v1/facturas/{id}` | Actualizar una factura |
| DELETE | `/v1/facturas/{id}` | Eliminar una factura |
| GET | `/v1/facturas/cliente/{clienteId}` | Obtener facturas por cliente |

## Estructura de FacturaDTO

```json
{
  "id": 1,
  "numero": "FACT-2026-001",
  "fechaEmision": "2026-04-08",
  "total": 1500.50,
  "clienteId": 1,
  "descripcion": "Descripción de la factura"
}
```

### Validaciones

- **numero**: No puede estar vacío
- **fechaEmision**: Es obligatoria (formato: YYYY-MM-DD)
- **total**: Es obligatorio y debe ser un valor positivo
- **clienteId**: Es obligatorio
- **descripcion**: Opcional

## Métodos de Prueba

### Opción 1: Archivo HTTP (IntelliJ IDEA / VSCode)

Usa el archivo `factura-api.http` incluido en el proyecto.

#### En IntelliJ IDEA:
1. Abre el archivo `factura-api.http`
2. Haz clic en el botón verde de "play" junto a cada request
3. Los resultados se mostrarán en el panel inferior

#### En VSCode:
1. Instala la extensión "REST Client" de Huachao Mao
2. Abre el archivo `factura-api.http`
3. Haz clic en "Send Request" sobre cada petición
4. Los resultados se mostrarán en una nueva pestaña

### Opción 2: Postman

Importa el archivo `factura-postman-collection.json` en Postman:

1. Abre Postman
2. Haz clic en "Import"
3. Selecciona el archivo `factura-postman-collection.json`
4. La colección se importará con todos los endpoints configurados
5. Asegúrate de que la variable `{{baseUrl}}` esté configurada como `http://localhost:8080`

### Opción 3: cURL

#### Crear Factura
```bash
curl -X POST http://localhost:8080/v1/facturas \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "FACT-2026-001",
    "fechaEmision": "2026-04-08",
    "total": 1500.50,
    "clienteId": 1,
    "descripcion": "Factura por servicios de consultoría"
  }'
```

#### Obtener todas las Facturas
```bash
curl -X GET http://localhost:8080/v1/facturas \
  -H "Accept: application/json"
```

#### Obtener Factura por ID
```bash
curl -X GET http://localhost:8080/v1/facturas/1 \
  -H "Accept: application/json"
```

#### Actualizar Factura
```bash
curl -X PUT http://localhost:8080/v1/facturas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "FACT-2026-001-MOD",
    "fechaEmision": "2026-04-08",
    "total": 1750.00,
    "clienteId": 1,
    "descripcion": "Factura actualizada"
  }'
```

#### Obtener Facturas por Cliente
```bash
curl -X GET http://localhost:8080/v1/facturas/cliente/1 \
  -H "Accept: application/json"
```

#### Eliminar Factura
```bash
curl -X DELETE http://localhost:8080/v1/facturas/1
```

## Escenarios de Prueba

### 1. Flujo Completo (Happy Path)
1. Crear varias facturas para diferentes clientes
2. Listar todas las facturas
3. Obtener una factura específica por ID
4. Obtener facturas de un cliente específico
5. Actualizar una factura
6. Eliminar una factura

### 2. Casos de Error
- Intentar obtener una factura con ID inexistente (debería retornar 404)
- Intentar actualizar una factura inexistente (debería retornar 404)
- Intentar eliminar una factura inexistente (debería retornar 404)

### 3. Validaciones
- Crear factura sin número (debería retornar 400)
- Crear factura sin fecha de emisión (debería retornar 400)
- Crear factura con total negativo (debería retornar 400)
- Crear factura sin clienteId (debería retornar 400)

## Respuestas HTTP Esperadas

| Operación | Código de Éxito | Código de Error |
|-----------|-----------------|-----------------|
| POST /v1/facturas | 201 Created | 400 Bad Request |
| GET /v1/facturas | 200 OK | - |
| GET /v1/facturas/{id} | 200 OK | 404 Not Found |
| PUT /v1/facturas/{id} | 200 OK | 400, 404 |
| DELETE /v1/facturas/{id} | 200 OK | 404 Not Found |
| GET /v1/facturas/cliente/{clienteId} | 200 OK | - |

## Prerequisitos

Asegúrate de que:
1. La aplicación esté corriendo: `./gradlew bootRun`
2. El puerto 8080 esté disponible
3. La base de datos H2 esté funcionando correctamente

## Notas

- El microservicio usa H2 en memoria, por lo que los datos se perderán al reiniciar
- Todas las fechas deben estar en formato ISO (YYYY-MM-DD)
- Los totales usan BigDecimal para precisión decimal
- Los IDs se generan automáticamente al crear facturas
