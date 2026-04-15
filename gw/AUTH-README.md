# API Gateway con Autenticación Básica

## 🔐 Configuración de Seguridad

Este API Gateway implementa **autenticación básica (Basic Auth)** para proteger todos los endpoints excepto el health check.

### Usuarios Configurados

| Usuario | Contraseña | Rol   | Propósito |
|---------|-----------|-------|-----------|
| `admin` | `admin123` | ADMIN | Administrador con acceso completo |
| `user`  | `user123`  | USER  | Usuario estándar con acceso completo |

## 🚀 Iniciar el Gateway

```bash
cd gw
mvn spring-boot:run
```

El gateway se iniciará en: **http://localhost:9090**

## 📡 Endpoints Disponibles

### Con Autenticación Requerida

Todos estos endpoints requieren autenticación básica:

- `/v1/facturas/**` → Microservicio de Facturas (puerto 8080)
- `/v1/clientes/**` → Microservicio de Clientes (puerto 8081)
- `/v3/api-docs/**` → Documentación OpenAPI
- `/swagger-ui/**` → Interfaz Swagger UI

### Sin Autenticación (Público)

- `/actuator/health` → Health check del gateway

## 🧪 Ejemplos de Uso

### Con cURL

#### Sin autenticación (401 Unauthorized)
```bash
curl http://localhost:9090/v1/facturas
```

#### Con autenticación (admin)
```bash
curl -u admin:admin123 http://localhost:9090/v1/facturas
```

#### Con autenticación (user)
```bash
curl -u user:user123 http://localhost:9090/v1/facturas
```

#### Health check (sin autenticación)
```bash
curl http://localhost:9090/actuator/health
```

### Con HTTP Client (.http file)

Abre el archivo `gateway-test.http` en VS Code con la extensión "REST Client" instalada y haz clic en "Send Request" sobre cualquier petición.

El archivo ya incluye las credenciales configuradas en variables:

```http
@gatewayUrl = http://localhost:9090
@adminUser = admin
@adminPassword = admin123
@normalUser = user
@normalPassword = user123
```

### Desde el Navegador

Para acceder a Swagger UI:

1. Abre: http://localhost:9090/swagger-ui/index.html
2. El navegador te pedirá las credenciales
3. Ingresa: `admin` / `admin123` o `user` / `user123`

## 🔧 Configuración Técnica

### Dependencias (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Clase de Configuración

La configuración de seguridad se encuentra en:
- `src/main/java/com/lite/gw/config/SecurityConfig.java`

Características:
- ✅ Autenticación básica HTTP habilitada
- ✅ Usuarios en memoria (InMemoryUserDetailsManager)
- ✅ Contraseñas encriptadas con BCrypt
- ✅ CSRF deshabilitado (para simplificar pruebas con APIs REST)
- ✅ Health check público sin autenticación

### Personalización de Usuarios

Para cambiar o agregar usuarios, edita el método `userDetailsService()` en `SecurityConfig.java`:

```java
UserDetails nuevoUsuario = User.builder()
    .username("miusuario")
    .password(passwordEncoder().encode("mipassword"))
    .roles("USER")
    .build();
```

## 📊 Monitoreo

### Health Check
```bash
curl http://localhost:9090/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

## 🛡️ Seguridad

### ⚠️ Importante para Producción

Esta configuración es para **desarrollo y aprendizaje**. Para producción considera:

1. **No uses usuarios en memoria**: Integra con LDAP, OAuth2, o una base de datos
2. **Habilita HTTPS**: La autenticación básica envía credenciales en base64 (fácil de decodificar)
3. **Habilita CSRF**: Si tienes formularios web
4. **Usa variables de entorno**: Para las contraseñas, no las dejes hardcodeadas
5. **Implementa rate limiting**: Para prevenir ataques de fuerza bruta
6. **Considera JWT**: Para arquitecturas de microservicios distribuidas

### Ejemplo de mejora para producción

```yaml
# application.yaml
spring:
  security:
    user:
      name: ${GATEWAY_USER}
      password: ${GATEWAY_PASSWORD}
```

## 🔄 Flujo de Autenticación

```
Cliente → Gateway (9090) → [Autenticación Básica] → Microservicio (8080/8081)
          ↓
     ✅ Autorizado → Proxy request
     ❌ No autorizado → 401 Unauthorized
```

## 📝 Logs de Seguridad

Los logs de seguridad están habilitados en modo DEBUG:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
```

Para ver los logs en tiempo real:
```bash
tail -f gateway-auth.log
```

## 🧪 Pruebas

Ejecuta todas las pruebas del archivo `gateway-test.http`:

1. Pruebas sin autenticación (deben fallar con 401)
2. Pruebas con usuario admin
3. Pruebas con usuario normal
4. Health check sin autenticación (debe funcionar)

## 📚 Referencias

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Basic Authentication RFC 7617](https://tools.ietf.org/html/rfc7617)
