# 🌐 ForoHub - [Reply Service]
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

El **Reply Service** es el microservicio encargado de la **gestión de respuestas** a los tópicos en la plataforma **ForoHub**.

Permite **crear, editar y eliminar respuestas**, además de publicar **eventos Kafka** para mantener sincronización con otros microservicios, como `notification-service`, `email-service` y `reply-read-service`.

Su objetivo es ofrecer una gestión de respuestas **consistente, escalable y confiable**, respetando la arquitectura desacoplada de microservicios.

## ✨ Características principales

- 📝 **Crear una respuesta:** Permite a los usuarios responder a un tópico específico.
- ✏️ **Editar respuesta:** Modifica el contenido de una respuesta, generando eventos de actualización.
- ❌ **Eliminar respuesta (lógicamente):** Se marca como inactiva sin perder el historial.
- ✅ **Marcar como solución:** Solo usuarios con rol de **moderador, instructor o administrador** pueden marcar una respuesta como solución de un tópico.
- 📡 **Publicación de eventos Kafka:** Notifica a otros servicios sobre nuevas respuestas, actualizaciones, eliminaciones o soluciones.

## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.
- **MySQL**: Base de datos para los microservicios transaccionales.
- **API REST & Feign Clients**: Comunicación entre microservicios.
- **Spring WebFlux**: Para flujos reactivos, como el análisis de contenido.
- **Apache Kafka**: Emisión de eventos.
- **Springdoc OpenAPI / Swagger UI**: Documentación y prueba interactiva de endpoints.

## 📦 Dependencias

- **Spring Boot**: Framework principal para microservicios.
- **Spring Data JPA**: Persistencia de datos en MySQL.
- **Spring Boot Starter Web / WebFlux**: APIs REST y flujos reactivos.
- **Spring Boot Starter Validation**: Validación de entradas.
- **Flyway**: Migraciones de bases de datos.
- **MySQL Connector**: Conector JDBC para MySQL.
- **Lombok**: Reducción de código repetitivo.
- **SpringDoc OpenAPI / Swagger UI**: Documentación automática.
- **Apache Kafka (Spring Cloud Stream Kafka)**: Comunicación basada en eventos.
- **Spring Cloud Netflix Eureka**: Registro y descubrimiento de servicios.
- **Spring Cloud Config**: Configuración centralizada.
- **Spring Cloud OpenFeign**: Clientes HTTP declarativos.
- **Spring Boot Starter Actuator**: Monitorización y métricas.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para gestión de dependencias.
- **MySQL** para la base de datos transaccional.
- **Kafka** para mensajería basada en eventos.
- **IDE**: IntelliJ IDEA o equivalente.

## 🧩 Variables de Entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.


```dotenv
# 📊 Base de Datos MySQL
MYSQL_HOST=your_mysql_host
MYSQL_PORT=your_mysql_port
MYSQL_REPLY_SERVICE=your_reply_username
MYSQL_REPLY_PASSWORD=your_reply_password

# 🏗️ Infraestructura y servicios
KAFKA_SERVERS=your_kafka_bootstrap_servers
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

## 🧱 Arquitectura y Comunicación

`reply-service` forma parte del **Business Domain** y se comunica con otros microservicios mediante **REST, Feign Clients y Kafka**:

- **REST / Feign:** consulta de datos y validaciones en `user-service`, `topic-service` y `course-service`.
- **WebFlux:** comunicación reactiva con `content-analysis-service` para validar contenido de las respuestas antes de ser publicadas.
- **Kafka:** publicación de eventos que notifican cambios a otros microservicios (`notification-service`, `email-service` y `reply-read-service`).

### Publicadores de eventos Kafka

El servicio cuenta con un **publisher principal** para mantener los eventos desacoplados:

- **`ReplyEventPublisher`** → Publica eventos relacionados con respuestas (`reply-events`):
    - Tipos: `CREATED`, `UPDATED`, `DELETED`, `SOLUTION_CHANGED`.

> ⚠️ La acción de marcar una respuesta como **solución** solo puede ser realizada por usuarios con rol de **moderador, instructor o administrador**.

## 🗄️ Base de Datos

El microservicio utiliza **MySQL** como base de datos relacional. Las migraciones se gestionan mediante **Flyway**.

**Tablas principales:**

- `replies` → Contiene la información principal de cada respuesta.

## 🔗 Endpoints Expuestos

Estos endpoints son accesibles a través del **API Gateway**:

| Endpoint              | Método      | Descripción                                                                                                                                                                                                                                                                                             |
|-----------------------|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/api/reply`              | `POST`      | Crea una respuesta para un tópico. Si se agrega una respuesta, el creador del tópico y los usuarios que lo siguen recibirán notificaciones y emails informándoles.                                                                                                                                      |
| `/api/reply/user/replies` | `GET`       | Obtiene todas las respuestas del usuario autenticado con paginación.                                                                                                                                                                                                                                    |
| `/api/reply/{replyId}`    | `GET`       | Obtiene una respuesta específica utilizando su ID.                                                                                                                                                                                                                                                      |
| `/api/reply/{replyId}` | `PUT`       | Actualiza una respuesta. Si la actualización la hace un moderador, instructor o administrador, solo se notifica al creador de la respuesta.                                                                                                                                                             |
| `/api/reply/{replyId}` | `PATCH`     | Alterna el estado de una respuesta como solución o la quita si ya estaba marcada como solución. Además, actualiza el estado del tópico, indicándole si está activo o cerrado. Al hacerlo, Se notificará al creador de la respuesta, al creador del tópico, y a todos los usuarios que siguen el tópico. |
| `/api/reply/{replyId}` | `DELETE`    | Elimina una respuesta de manera lógica. Si un moderador, instructor o administrador la elimina, solo se notifica al creador de la respuesta.                                                                                                                                                            |

## 🔒 Endpoints Internos

Estos endpoints **no están expuestos al API Gateway** y son usados exclusivamente para la comunicación entre microservicios:

| Endpoint | Método | Descripción |
|---------|--------|-------------|
| `/internal/reply/topic/{topicId}` | `GET` | Obtiene todas las respuestas asociadas a un tópico específico. |
| `/internal/reply/topic/{topicId}/count` | `GET` | Devuelve la cantidad de respuestas activas de un tópico. |
| `/internal/reply/count/batch?ids={ids}` | `GET` | Devuelve la cantidad de respuestas asociadas a una lista de IDs de tópicos. |
| `/internal/reply/user/{userId}/count` | `GET` | Retorna la cantidad de respuestas activas realizadas por un usuario. |

> 🧩 Documentados con **OpenAPI/Swagger**, pero ocultos con `@Hidden` para evitar exposición pública.

## 📘 Documentación del microservicio

La documentación completa está disponible mediante **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8083/swagger-ui/index.html)**

O puedes acceder directamente mediante la URL:

```
http://localhost:8083/swagger-ui/index.html
```

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Reply Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)



