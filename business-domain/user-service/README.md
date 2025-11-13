# 🌐 ForoHub - [User Service]
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)
![Kafka](https://img.shields.io/badge/Event%20Streaming-Apache%20Kafka-orange)
![Feign](https://img.shields.io/badge/Client-Feign-yellowgreen)
![Swagger](https://img.shields.io/badge/API%20Docs-Swagger-green)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-lightblue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📚 Índice

1. [📝 Descripción](#-descripción)
2. [✨ Características principales](#-características-principales)
3. [💻 Tecnologías principales](#-tecnologías-principales)
4. [📦 Dependencias](#-dependencias)
5. [🔧 Requisitos del proyecto](#-requisitos-del-proyecto)
6. [🧩 Variables de Entorno](#-variables-de-entorno)
7. [🧱 Arquitectura y Comunicación](#-arquitectura-y-comunicación)
8. [🗄️ Base de Datos](#-base-de-datos)
9. [🔗 Endpoints Expuestos](#-endpoints-expuestos)
10. [🔒 Endpoints Internos](#-endpoints-internos)
11. [📘 Documentación del microservicio](#-documentación-del-microservicio)
12. [👨‍💻 Autor](#-autor)


## 📝 Descripción

El **User Service** es el microservicio encargado de la **gestión de usuarios** en la plataforma **ForoHub**. Incluye funcionalidades de **registro, actualización de perfil, recuperación de contraseña y gestión de roles**.

Se integra con otros microservicios mediante **Feign Clients**, y publica eventos en **Kafka** para mantener sincronización con sistemas de notificación, correo y lectura optimizada (`topic-read-service`).

Su objetivo es proporcionar una gestión de usuarios **segura, escalable y confiable**, en un entorno de microservicios desacoplado.


## ✨ Características principales

- 🔑 **Registro de usuarios**
    - Creación de cuentas y confirmación por correo electrónico.
- 🛠️ **Actualización de perfil**
    - Modificación de nombre de usuario y contraseña.
    - Generación de eventos que actualizan vistas en `topic-read-service`.
- 🔄 **Recuperación de contraseña**
    - Solicitud de restablecimiento mediante correo electrónico.
    - Validación de token temporal para cambio seguro de contraseña.
- 👥 **Gestión de roles y permisos**
    - Asignación de roles: usuario, moderador, instructor, administrador.
- 📡 **Eventos Kafka**
    - Publica eventos al crear, actualizar o eliminar usuarios para mantener consistencia en otros microservicios.
- 👀 **Estadísticas de usuario**
    - Obtiene información resumida sobre actividad del usuario, tópicos creados, respuestas y seguimiento.


## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.
- **MySQL**: Base de datos para los microservicios transaccionales.
- **API REST & Feign Clients**: Comunicación entre microservicios.
- **Spring WebFlux**: Para flujos reactivos, como el análisis de contenido.
- **Apache Kafka**: Emisión de eventos.
- **Springdoc OpenAPI / Swagger UI**: Documentación y prueba interactiva de endpoints.


## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Data JPA**: Facilita la persistencia de datos en bases de datos relacionales (MySQL).
- **Spring Boot Starter Web / WebFlux**: Para exponer APIs REST y flujos reactivos.
- **Spring Boot Starter Validation**: Validación de objetos y parámetros de entrada.
- **Flyway**: Migraciones y versionamiento de bases de datos.
- **MySQL Connector**: Conector JDBC para interactuar con MySQL.
- **Lombok**: Reduce código repetitivo con anotaciones (getters, setters, constructores).
- **SpringDoc OpenAPI / Swagger UI**: Documentación automática de la API.
- **Apache Kafka (Spring Cloud Stream Kafka)**: Comunicación basada en eventos entre microservicios.
- **Spring Cloud Netflix Eureka**: Registro y descubrimiento de microservicios.
- **Spring Cloud Config**: Configuración centralizada para los microservicios.
- **Spring Cloud OpenFeign**: Clientes HTTP declarativos para comunicación entre microservicios.
- **Spring Boot Starter Actuator**: Monitorización y métricas de los microservicios.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **MySQL** para los microservicios transaccionales.
- **Kafka** para la mensajería basada en eventos.
- **IntelliJ IDEA** o cualquier IDE compatible con Java.


## 🧩 Variables de Entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 📊 Base de Datos MySQL
MYSQL_HOST=your_mysql_host
MYSQL_PORT=your_mysql_port
MYSQL_USER_SERVICE=your_user_username
MYSQL_USER_PASSWORD=your_user_password

# 🏗️ Infraestructura y servicios
KAFKA_SERVERS=your_kafka_bootstrap_servers
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.


## 🧱 Arquitectura y Comunicación

`user-service` forma parte del **Business Domain** y se comunica con otros servicios mediante **REST**, **Feign Clients** y **Kafka**:

- **REST / Feign:** consulta de datos y validaciones en `topic-service` y `reply-service`.
- **WebFlux:** comunicación reactiva con `content-analysis-service` para validar nombres de usuario.
- **Kafka:** publicación de eventos que notifican cambios a otros microservicios.  
  Los consumidores principales son: `notification-service`, `email-service` y `topic-read-service`.

### Publicadores de eventos Kafka

- **`UserEventPublisher`** → Publica eventos relacionados con usuarios (`user-events`):
    - Tipos: `CREATED_ACCOUNT`, `RESET_PASSWORD`, `REQUEST_CONFIRMATION_CODE`, `UPDATED_USER`.


## 🗄️ Base de Datos

El microservicio utiliza **MySQL** para almacenar usuarios. Las migraciones se gestionan con **Flyway**.

**Tablas principales:**

- `users` → Información principal de cada usuario.
- `profiles` → Perfiles asignados a cada usuario.


## 🔗 Endpoints Expuestos

Estos endpoints son accesibles a través del **API Gateway**.

| Endpoint | Método | Descripción |
|--------|---------|-------------|
| `/api/auth/create-account` | `POST` | Crea una nueva cuenta de usuario. |
| `/api/auth/confirm-account/{token}` | `GET` | Confirma la cuenta mediante token de correo. |
| `/api/auth/request-code` | `POST` | Solicita un nuevo código de confirmación. |
| `/api/auth/forgot-password` | `POST` | Genera un token de restablecimiento de contraseña. |
| `/api/auth/update-password/{token}` | `POST` | Actualiza la contraseña usando token temporal. |
| `/api/auth/update-password` | `PATCH` | Cambia la contraseña del usuario autenticado. |
| `/api/auth/update-username` | `PATCH` | Cambia el nombre de usuario del usuario autenticado. |
| `/api/auth/me` | `GET` | Obtiene información del usuario autenticado. |
| `/api/auth/stats` | `GET` | Obtiene estadísticas del usuario. |


## 🔒 Endpoints Internos

Estos endpoints **no están expuestos al API Gateway** y son usados exclusivamente para la comunicación entre microservicios dentro del ecosistema ForoHub.

| Endpoint | Método | Descripción |
|--------|---------|-------------|
| `/internal/auth/{userId}` | `GET` | Obtiene un usuario específico por su ID. |
| `/internal/auth/batch?ids={ids}` | `GET` | Obtiene múltiples usuarios enviando una lista de IDs separados por coma. |
| `/internal/auth/validate-credentials` | `POST` | Valida las credenciales de un usuario (`email` y `password`) y devuelve la información del usuario. |

> 🧩 Documentados con **OpenAPI/Swagger**, pero ocultos con `@Hidden` para evitar su exposición pública.


## 📘 Documentación del microservicio

La documentación completa está disponible mediante **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8081/swagger-ui/index.html)**

O puedes acceder directamente mediante la URL:

```
http://localhost:8081/swagger-ui/index.html
```

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [User Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

