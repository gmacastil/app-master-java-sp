# Servicio de Notificaciones con Kafka

Este microservicio implementa un sistema de mensajería con Kafka que incluye:
- **Productor**: Envía notificaciones al topic de Kafka
- **Consumidor**: Escucha y procesa las notificaciones del topic

## Configuración

### Kafka Broker
- **URL**: `localhost:9092`
- **Sin autenticación**
- **Topic**: `notificaciones`
- **Particiones**: 3
- **Réplicas**: 1

### Grupo de Consumidor
- **Group ID**: `ms-notifica-group`

## Estructura del Proyecto

```
src/main/java/com/lite/ms_notifica/
├── model/
│   └── Notificacion.java          # Modelo de datos
├── config/
│   └── KafkaTopicConfig.java      # Configuración del topic
├── service/
│   ├── KafkaProducerService.java  # Servicio productor
│   └── KafkaConsumerService.java  # Servicio consumidor
└── controller/
    └── NotificacionController.java # API REST
```

## Endpoints Disponibles

### 1. Health Check
```bash
GET http://localhost:8080/api/notificaciones/health
```

### 2. Enviar Notificación Completa
```bash
POST http://localhost:8080/api/notificaciones
Content-Type: application/json

{
  "destinatario": "usuario@example.com",
  "asunto": "Bienvenido",
  "mensaje": "Gracias por registrarte",
  "tipo": "EMAIL"
}
```

### 3. Enviar Mensaje Simple
```bash
POST http://localhost:8080/api/notificaciones/simple?key=test-1&mensaje=Hola%20Kafka
```

## Tipos de Notificación Soportados

El consumidor procesa tres tipos de notificaciones:
- **EMAIL**: Envío de correos electrónicos
- **SMS**: Envío de mensajes de texto
- **PUSH**: Notificaciones push

## Pruebas con cURL

### Enviar notificación EMAIL
```bash
curl -X POST http://localhost:8080/api/notificaciones \
  -H "Content-Type: application/json" \
  -d '{
    "destinatario": "test@example.com",
    "asunto": "Prueba de notificación",
    "mensaje": "Este es un mensaje de prueba",
    "tipo": "EMAIL"
  }'
```

### Enviar notificación SMS
```bash
curl -X POST http://localhost:8080/api/notificaciones \
  -H "Content-Type: application/json" \
  -d '{
    "destinatario": "+34123456789",
    "asunto": "Alerta",
    "mensaje": "Código de verificación: 123456",
    "tipo": "SMS"
  }'
```

### Enviar notificación PUSH
```bash
curl -X POST http://localhost:8080/api/notificaciones \
  -H "Content-Type: application/json" \
  -d '{
    "destinatario": "user-device-token",
    "asunto": "Nueva actualización",
    "mensaje": "Hay nuevas funciones disponibles",
    "tipo": "PUSH"
  }'
```

### Enviar mensaje simple
```bash
curl -X POST "http://localhost:8080/api/notificaciones/simple?key=msg-001&mensaje=Mensaje%20de%20prueba"
```

## Cómo Ejecutar

### 1. Iniciar Kafka (si no está ejecutándose)
```bash
# Iniciar Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Iniciar Kafka Broker
bin/kafka-server-start.sh config/server.properties
```

### 2. Ejecutar el Microservicio
```bash
cd ms-notifica
./gradlew bootRun
```

### 3. Ver Logs
El servicio mostrará en los logs:
- Mensajes enviados por el productor
- Mensajes recibidos por el consumidor
- Procesamiento de las notificaciones

Ejemplo de log:
```
INFO  KafkaProducerService - Mensaje enviado exitosamente: [abc-123] con offset: [42]
INFO  KafkaConsumerService - Mensaje recibido - Key: [abc-123], Partition: [1], Offset: [42]
INFO  KafkaConsumerService - Notificación procesada: Notificacion{id='abc-123', ...}
INFO  KafkaConsumerService - Enviando EMAIL a: test@example.com - Asunto: Prueba
```

## Comandos Útiles de Kafka

### Listar topics
```bash
kafka-topics.sh --list --bootstrap-server localhost:9092
```

### Ver mensajes del topic
```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic notificaciones \
  --from-beginning
```

### Describir el topic
```bash
kafka-topics.sh --describe --topic notificaciones --bootstrap-server localhost:9092
```

## Características Implementadas

✅ Productor de Kafka con serialización JSON  
✅ Consumidor de Kafka con deserialización JSON  
✅ Creación automática del topic con 3 particiones  
✅ Manejo de diferentes tipos de notificaciones  
✅ API REST para enviar notificaciones  
✅ Logging detallado de eventos  
✅ Manejo de errores y reintentos  
✅ Confirmación de escritura (acks=all)  

## Flujo de Funcionamiento

1. Cliente envía una solicitud POST al endpoint `/api/notificaciones`
2. El controlador recibe la notificación y genera un ID único si no existe
3. El `KafkaProducerService` serializa la notificación a JSON y la envía al topic
4. Kafka confirma la recepción y devuelve el offset
5. El `KafkaConsumerService` escucha el topic y recibe el mensaje
6. El consumidor deserializa el JSON y procesa la notificación según su tipo
7. Se registran logs en cada paso del proceso

## Configuración Adicional

Para personalizar la configuración, edita el archivo `application.properties`:

```properties
# Cambiar el puerto del servidor
server.port=8080

# Cambiar el broker de Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Cambiar el nombre del topic
kafka.topic.notifications=notificaciones

# Cambiar el group ID del consumidor
spring.kafka.consumer.group-id=ms-notifica-group
```
