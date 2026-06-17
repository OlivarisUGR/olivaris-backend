# Olivaris Backend

## 🌟 Introducción
Este repositorio contiene el **código del lado del servidor (backend)** para la plataforma **Olivaris**. El sistema actúa como el motor central de la aplicación, exponiendo una API principal para la gestión de datos, autenticación de usuarios y notificaciones.

El backend está construido principalmente en **Java**, utilizando el framework **Spring Boot** para una arquitectura robusta y escalable.

## 🚀 Tecnologías Principales
El proyecto utiliza una pila tecnológica moderna centrada en la seguridad y el manejo de datos geoespaciales:
*   **Lenguaje**: Java 21.
*   **Framework**: Spring Boot con Spring Security (JWT) para autenticación.
*   **Base de Datos**: PostgreSQL con la extensión **PostGIS** para soporte geográfico.
*   **Gestión de Migraciones**: Flyway para el control de versiones de la base de datos.
*   **Infraestructura**: Docker, Docker Compose y Nginx como proxy inverso.
*   **Servicios**: Spring Mail para confirmaciones y notificaciones por correo.

## 📁 Estructura del Repositorio
El repositorio se organiza en tres directorios principales que separan la lógica de la aplicación, las pruebas de rendimiento y la automatización:

### 1. `/olivaris-app`
Es el núcleo del sistema. Contiene la **aplicación Spring Boot** completa y su configuración de despliegue.
*   **`src/`**: Contiene el código fuente de la lógica de negocio, controladores de la API y modelos de datos.
*   **`docker/initdb/`**: Scripts para la inicialización de la base de datos en entornos Docker.
*   **`nginx/`**: Archivos de configuración (como `default.conf`) para el proxy inverso que gestiona las peticiones.
*   **`src/main/resources/db/migration`**: Archivos de Flyway para la evolución del esquema de la base de datos.
*   **`pom.xml`**: Archivo de configuración de Maven con todas las dependencias del proyecto.
*   **`.env.example`**: Plantilla de variables de entorno necesarias (claves secretas, credenciales de DB y correo).

### 2. `/olivaris-load-tests`
Directorio dedicado a las **pruebas de carga y rendimiento** para asegurar que el backend soporte múltiples usuarios.
*   **`locust_login.py`**: Script de Locust para probar la capacidad del sistema en el inicio de sesión.
*   **`locust_plots.py`**: Script para probar la carga en los endpoints de visualización de gráficos (plots).
*   **`requirements.txt`**: Dependencias de Python necesarias para ejecutar las pruebas.

### 3. `/.github/workflows`
Contiene las configuraciones de **CI/CD** (Integración y Despliegue Continuo) para automatizar tareas cada vez que se sube código al repositorio. En este caso, va a compilar el programa y va a ejecutar
todos los tests para comprobar que funciona correctamente.

## 🛠️ Configuración Inicial Rápida
Para levantar el entorno completo de desarrollo (Base de datos + App + Nginx):

1.  Accede a la carpeta de la aplicación: `cd olivaris-app`.
2.  Configura tus variables de entorno creando un archivo `.env` basado en `.env.example`.
3.  Ejecuta el sistema con Docker: 
    ```bash
    docker compose up --build
    ```
    *Nota: Esto iniciará PostgreSQL con PostGIS, la aplicación Spring Boot y Nginx como puerta de entrada*.
