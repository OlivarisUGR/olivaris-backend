# Olivaris Backend

Backend de la plataforma Olivaris construido con Spring Boot. Expone la API principal, persiste datos en PostgreSQL/PostGIS, usa Flyway para migraciones, JWT para autenticación y soporte de correo para confirmaciones y notificaciones.

## Tecnologías principales

- Java 21
- Spring Boot
- Spring Security con JWT
- Spring Data JPA
- Flyway
- PostgreSQL + PostGIS
- Spring Mail
- Docker, Docker Compose y Nginx

## Requisitos

- Java 21
- Maven 3.9+ o el wrapper incluido
- Docker y Docker Compose si vas a levantar el entorno completo

## Configuración necesaria

Antes de ejecutar la aplicación, asegúrate de tener estos archivos correctamente configurados:

1. `src/main/resources/application.properties`
   - Debe contener los datos adecuados para tu entorno local o de despliegue.
   - Ajusta la URL de la base de datos, usuario, contraseña, correo saliente, URL base de la app y secreto JWT según corresponda.
   - El proyecto ya trae valores por defecto para desarrollo local, pero conviene revisarlos antes de correr en otro entorno.

2. `.env`
   - Crea este archivo a partir de `.env.example`.
   - Guarda ahí los valores sensibles y específicos de tu entorno.
   - No lo subas al repositorio con credenciales reales.

Ejemplo de `application.properties` para copiar, pegar y adaptar:

```properties
spring.application.name=olivaris-app

# PostgreSQL configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/olivaris
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASENA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Flyway configuration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0

# Confirmation token configuration
confirmation-token.expiration-hours=24

# Email configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=TU_CORREO
spring.mail.password=TU_APP_PASSWORD
app.base-url=http://localhost:8080
spring.mail.urlConfirm=${MAIL_CONFIRM_URL:${app.base-url}/api/auth/confirm?token=}
spring.mail.urlConfirmAdmin=${MAIL_CONFIRM_ADMIN_URL:${app.base-url}/api/auth/confirmEntityAdmin?token=}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT configuration
security.jwt.token.expiration=3600000
security.jwt.refresh-token.expiration=604800000
security.jwt.secret-key=TU_SECRETO_JWT_LARGO_Y_ALEATORIO

# Allow Spring Boot to accept the request from Nginx
server.forward-headers-strategy=framework
```

Variables habituales en `.env`:

- `DOCKER_POSTGRES_DB`
- `DOCKER_POSTGRES_USER`
- `DOCKER_POSTGRES_PASSWORD`
- `POSTGRES_HOST_PORT`
- `APP_BASE_URL`
- `SECURITY_JWT_SECRET_KEY`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`

## Ejecución con Docker

1. Copia el ejemplo de variables de entorno:

```bash
cp .env.example .env
```

2. Edita `.env` con los valores correctos para tu entorno.

3. Levanta los servicios:

```bash
docker compose up --build
```

Esto arranca:

- PostgreSQL con PostGIS
- La aplicación Spring Boot
- Nginx como proxy frontal

## Ejecución local

Si quieres correr la aplicación fuera de Docker, necesitas una instancia de PostgreSQL disponible y un `application.properties` ajustado a esa conexión.

```bash
./mvnw spring-boot:run
```

## Base de datos

- La base usa migraciones Flyway ubicadas en `src/main/resources/db/migration`.
- En Docker, la base de datos se inicializa con los scripts de `docker/initdb/`.

## Estructura relevante

- `src/main/resources/application.properties`: configuración principal de Spring.
- `.env.example`: plantilla de variables de entorno.
- `docker-compose.yml`: levanta base de datos, app y Nginx.
- `docker/initdb/`: scripts de inicialización de la base.
- `nginx/default.conf`: configuración del proxy inverso.

## Notas

- No uses `localhost` dentro del contenedor de la aplicación para conectar con PostgreSQL; en Docker el host correcto es `db`.
- Si cambias credenciales, recuerda mantener consistencia entre `application.properties`, `.env` y `docker-compose.yml`.
