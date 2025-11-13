# 🌐 ForoHub - [Topic Service]
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
7. [⚙️ Sincronización entre Servicios de Lectura y Escritura](#-sincronización-entre-servicios-de-lectura-y-escritura)
8. [🧱 Arquitectura y Comunicación](#-arquitectura-y-comunicación)
9. [🗄️ Base de Datos](#-base-de-datos)
10. [🔗 Endpoints Expuestos](#-endpoints-expuestos)
11. [🔒 Endpoints Internos](#-endpoints-internos)
12. [📘 Documentación del microservicio](#-documentación-del-microservicio)
13. [👨‍💻 Autor](#-autor)

## 📝 Descripción

El **Topic Service** es el microservicio responsable de gestionar los **tópicos** creados por los usuarios dentro de la plataforma **ForoHub**.

Permite **crear, editar, eliminar y seguir tópicos**, además de emitir **eventos Kafka** cuando ocurren acciones relevantes (creación, actualización o eliminación).

Su propósito es mantener la información de los tópicos sincronizada con otros microservicios —como `notification-service`, `email-service` y `topic-read-service`— garantizando una **arquitectura desacoplada, consistente y escalable**.

## ✨ Características principales

- 📝 **Crear un tópico:** Permite iniciar una conversación asociada a un curso.
- ✏️ **Editar un tópico:** Modifica título o contenido, generando eventos de actualización.
- ❌ **Eliminar un tópico (lógicamente):** Se marca como inactivo sin perder el historial.
- 👀 **Seguir/Dejar de seguir:** Controla la suscripción de usuarios a tópicos.
- 📡 **Publicación de eventos Kafka:** Informa a otros servicios sobre cambios o nuevas interacciones.

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


## 🧩 Variables de entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 📊 Base de Datos MySQL
MYSQL_HOST=your_mysql_host
MYSQL_PORT=your_mysql_port
MYSQL_TOPIC_SERVICE=your_topic_username
MYSQL_TOPIC_PASSWORD=your_topic_password

# 🏗️ Infraestructura y servicios
KAFKA_SERVERS=your_kafka_bootstrap_servers
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

## ⚙️ Sincronización entre Servicios de Lectura y Escritura

El **Topic Read Service** mantiene una **vista optimizada y de solo lectura** de los tópicos en **MongoDB**, basada en los eventos emitidos por `topic-service`. De esta forma, las operaciones de lectura y escritura se mantienen completamente separadas.

**Flujo general:**
1. `topic-service` publica eventos en Kafka (`topic.created`, `topic.updated`, `topic.deleted`, etc.).
2. `topic-read-service` consume estos eventos y actualiza su base de datos MongoDB.
3. Las consultas públicas de solo lectura (listado, búsqueda, detalles, etc.) se ejecutan sobre `topic-read-service`.

> ⚙️ Este patrón implementa el modelo **CQRS (Command Query Responsibility Segregation)**, optimizando el rendimiento en consultas y reduciendo la carga sobre la base de datos transaccional.

### ⚙️ Enrutamiento de Endpoints entre `topic-service` y `topic-read-service`

El **API Gateway** gestiona dinámicamente todas las solicitudes al prefijo común `/api/topic` para mantener la separación entre lectura y escritura:

- 🔍 **GET →** redirigidos automáticamente a `topic-read-service` (operaciones de lectura).
- ✏️ **POST / PUT / DELETE →** manejados por `topic-service` (operaciones de escritura).

Esto permite escalar cada servicio de forma independiente y mantener una arquitectura desacoplada y eficiente.

#### 🔁 Mapeo de enrutamiento

| Método HTTP | Endpoint base | Servicio destino | Descripción breve |
|--------------|----------------|------------------|-------------------|
| `GET` | `/api/topic/**` | `topic-read-service` | Consultas y visualización de tópicos |
| `POST` | `/api/topic/**` | `topic-service` | Creación o seguimiento de tópicos |
| `PUT` | `/api/topic/**` | `topic-service` | Actualización de tópicos existentes |
| `DELETE` | `/api/topic/**` | `topic-service` | Eliminación lógica de tópicos |

> ⚠️ Aunque ambos microservicios comparten el mismo prefijo `/api/topic`, el **API Gateway intercepta los métodos GET** y los redirige a `topic-read-service`, manteniendo la lógica CQRS.

#### 🧩 Ejemplo de comportamiento

- `GET /api/topic` → manejado por **topic-read-service**
- `GET /api/topic/{id}` → manejado por **topic-read-service**
- `POST /api/topic` → manejado por **topic-service**
- `PUT /api/topic/{id}` → manejado por **topic-service**
- `DELETE /api/topic/{id}` → manejado por **topic-service**


## 🧱 Arquitectura y Comunicación

El `topic-service` pertenece al **Business Domain** y se comunica con otros servicios mediante **REST** y **Kafka**.

**Flujos principales:**
- **REST:** obtención de datos desde `user-service`, `reply-service` y `course-service` mediante **Feign Clients**.
- **WebFlux:** comunicación **reactiva** con `content-analysis-service` para el **análisis automático del contenido** de los tópicos antes de su publicación o actualización.
- **Kafka:** publicación de eventos que notifican cambios a otros microservicios.  
  Los consumidores principales son: `notification-service`, `email-service` y `topic-read-service`.
### Publicadores de eventos Kafka

El servicio cuenta con **dos publishers independientes** para mantener los eventos desacoplados:

- **`TopicEventPublisher`** → emite eventos relacionados con los tópicos (`topic-events`).
    - Tipos: `CREATED`, `UPDATED`, `STATUS_CHANGED`, `DELETED`.

- **`TopicFollowEventPublisher`** → emite eventos de seguimiento a tópicos (`topic-follow-events`).
    - Tipos: `FOLLOW`, `UNFOLLOW`.

## 🗄️ Base de Datos

El microservicio utiliza **MySQL** como base de datos relacional para almacenar y gestionar la información de los tópicos. Las **migraciones** se administran mediante **Flyway**, garantizando la consistencia del esquema entre entornos.

**Tablas principales:**

- **`topics`** → Contiene la información principal de cada tópico creado por los usuarios.
- **`topic_followers`** → Registra las relaciones entre usuarios y los tópicos que siguen.


## 🔗 Endpoints Expuestos

Estos endpoints son accesibles a través del **API Gateway**.

| Endpoint               | Método | Descripción |
|------------------------|--------|-------------|
| `/api/topic`           | `POST` | Crea un nuevo tópico (analizado por IA antes de guardarse). |
| `/api/topic/{topicId}` |  `PUT` | Actualiza un tópico existente. |
| `/api/topic/{topicId}` | `DELETE` | Elimina un tópico de manera lógica. |
| `/api/topic/follow/{topicId}` | `POST`  | Permite seguir o dejar de seguir un tópico. |


## 🔒 Endpoints Internos

Estos endpoints **no están expuestos al API Gateway** y son usados exclusivamente para la comunicación entre microservicios dentro del ecosistema ForoHub.

| Endpoint | Método | Descripción |
|---------|-----------|-------------|
| `/internal/topic/{topicId}/status` | `POST` | Cambia el estado (`ACTIVE` o `CLOSED`) de un tópico existente. |
| `/internal/topic/summary/{topicId}` | `GET` | Obtiene información resumida de un tópico. |
| `/internal/topic/user/{userId}/count` | `GET` | Devuelve la cantidad de tópicos creados por un usuario. |
| `/internal/topic/user/{userId}/followed/count` | `GET` | Retorna la cantidad de tópicos que un usuario sigue. |
| `/internal/topic/export` | `GET` | Exporta todos los tópicos con detalles para sincronización inicial con `topic-read-service`. |

> 🧩 Documentados con **OpenAPI/Swagger**, pero ocultos con `@Hidden` para evitar su exposición pública.

## 📘 Documentación del microservicio

La documentación completa y en tiempo real está disponible a través de **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8082/swagger-ui/index.html)**

O puedes acceder directamente mediante la URL:

```
http://localhost:8082/swagger-ui/index.html
```

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Topic Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

