# 🌐 ForoHub - [Topic Read Service]
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![MongoDB](https://img.shields.io/badge/Database-MongoDB-green)
![Kafka](https://img.shields.io/badge/Event%20Streaming-Apache%20Kafka-orange)
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
7. [⚙️ Sincronización con Topic Service](#-sincronización-con-topic-service)
8. [🧱 Arquitectura y Comunicación](#-arquitectura-y-comunicación)
9. [🗄️ Base de Datos](#-base-de-datos)
10. [🔗 Endpoints Expuestos](#-endpoints-expuestos)
11. [📘 Documentación del microservicio](#-documentación-del-microservicio)
12. [👨‍💻 Autor](#-autor)

---

## 📝 Descripción

El **Topic Read Service** es un microservicio de **solo lectura** dentro del ecosistema **ForoHub**, encargado de mantener una **vista optimizada en MongoDB** con la información de los tópicos y sus relaciones.

Su función principal es permitir la **consulta eficiente** de los tópicos, sin afectar el rendimiento del microservicio de escritura (`topic-service`).

Se actualiza en tiempo real mediante los **eventos Kafka** generados por:
- `topic-service`
- `reply-service`
- `user-service`
- `topic-follow-events`

De esta forma, implementa el patrón **CQRS (Command Query Responsibility Segregation)**, separando completamente las responsabilidades de lectura y escritura.


## ✨ Características principales

- 📖 **Lectura optimizada:** Consultas rápidas y escalables gracias a **MongoDB**.
- 🔁 **Sincronización por eventos:** Actualización automática basada en los eventos Kafka publicados por otros microservicios.
- 👥 **Listado de tópicos seguidos y propios:** Permite consultar los tópicos de un usuario o los que sigue.
- 🔍 **Filtrado y búsqueda:** Soporte para búsqueda por palabra clave, curso o estado.
- ⚙️ **Modelo desacoplado:** No depende directamente de las bases de datos relacionales de otros servicios.


## 💻 Tecnologías principales

- **Spring Boot** – Framework principal para la construcción del microservicio.
- **Spring Data MongoDB** – Persistencia en base de datos NoSQL.
- **Spring Cloud Stream (Kafka)** – Consumo de eventos en tiempo real.
- **Swagger / OpenAPI** – Documentación interactiva de la API.
- **Eureka Client** – Descubrimiento de servicios dentro del ecosistema.
- **Spring Cloud Config** – Configuración centralizada.

## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Boot Starter Web**: Para exponer APIs REST.
- **Lombok**: Reduce código repetitivo con anotaciones (getters, setters, constructores).
- **SpringDoc OpenAPI / Swagger UI**: Documentación automática de la API.
- **Apache Kafka (Spring Cloud Stream Kafka)**: Comunicación basada en eventos entre microservicios.
- **Spring Cloud Netflix Eureka**: Registro y descubrimiento de microservicios.
- **Spring Cloud Config**: Configuración centralizada para los microservicios.
- **Spring Boot Starter Actuator**: Monitorización y métricas de los microservicios.
- **Spring Boot Starter Data MongoDB**: Almacenamiento y consultas rápidas en MongoDB.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **MongoDB** para `topic-read-service`.
- **Kafka** para la mensajería basada en eventos.
- **IntelliJ IDEA** o cualquier IDE compatible con Java.

## 🧩 Variables de Entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv   
# 📊 Base de Datos MongoDB
MONGO_TOPIC_READ_SERVICE=your_topic_read_username
MONGO_TOPIC_READ_PASSWORD=your_topic_read_password

# 🏗️ Infraestructura y servicios
CONFIG_SERVER_HOST=your_config_server_url
EUREKA_URL=your_eureka_server_url
KAFKA_SERVERS=your_kafka_bootstrap_servers
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.


## ⚙️ Sincronización con Topic Service

El `topic-read-service` mantiene su base de datos sincronizada en tiempo real con los eventos generados por otros microservicios del ecosistema **ForoHub**, utilizando **Apache Kafka** como sistema de mensajería.

### 🔄 Flujo general de sincronización

1. **`topic-service`** publica eventos relacionados con la creación, edición, eliminación o cambio de estado de los tópicos (`CREATED`, `UPDATED`, `STATUS_CHANGED`, `DELETED`).
2. **`reply-service`** envía eventos cuando se crean, editan, eliminan o marcan respuestas como solución (`CREATED`, `EDITED`, `DELETED`, `SOLVED`).
3. **`user-service`** emite eventos al crear o actualizar usuarios, reflejando los cambios de nombre o username.
4. **`topic-follow-events`** actualiza la lista de seguidores de un tópico.
5. **`topic-read-service`** consume estos eventos y actualiza su vista optimizada en **MongoDB**, garantizando coherencia eventual y consultas rápidas.

> De esta manera, el servicio implementa el patrón **CQRS (Command Query Responsibility Segregation)**, donde las operaciones de lectura y escritura están completamente separadas para mejorar el rendimiento y la escalabilidad del sistema.

---

## 🧱 Arquitectura y Comunicación

El `topic-read-service` pertenece al **Business Domain** dentro de la arquitectura de microservicios de ForoHub.

Toda su comunicación con otros servicios se realiza de manera **asíncrona**, a través de **Kafka**, sin llamadas HTTP directas ni dependencias circulares.

### 🔹 Fuentes de eventos consumidos

| Origen | Tipo de evento | Descripción general |
|---------|----------------|---------------------|
| `user-events` | `CREATED`, `UPDATED` | Sincroniza datos del usuario, como username. |
| `topic-events` | `CREATED`, `UPDATED`, `STATUS_CHANGED`, `DELETED` | Actualiza o elimina tópicos. |
| `reply-events` | `CREATED`, `EDITED`, `DELETED`, `SOLVED` | Sincroniza las respuestas asociadas a los tópicos. |
| `topic-follow-events` | `FOLLOWED`, `UNFOLLOWED` | Actualiza la lista de usuarios que siguen cada tópico. |

### ⚙️ Procesamiento de mensajes

> Los mensajes se procesan usando **Spring Cloud Stream**, con modo `manual_immediate` para confirmar la recepción y persistencia de cada evento de forma controlada.

Esto garantiza:
- ✅ **Idempotencia**: cada evento se procesa una sola vez.
- 🔒 **Consistencia eventual**: los datos reflejan el estado actualizado del ecosistema.
- ⚡ **Alta disponibilidad**: el servicio puede procesar mensajes en paralelo y escalar horizontalmente.

---

## 🗄️ Base de Datos

El servicio utiliza **MongoDB** como base de datos principal, optimizada para operaciones de lectura y agregación.

### 🧩 Colección principal

- **`topics_read`** → Contiene la información consolidada de los tópicos, incluyendo detalles del autor, curso, estado, seguidores y respuestas.


## 🔗 Endpoints Expuestos

El servicio expone endpoints **solo de lectura**, disponibles a través del **API Gateway**.

| Endpoint | Método | Descripción |
|-----------|--------|-------------|
| `/api/topic` | `GET` | Obtiene todos los tópicos con filtros opcionales (curso, palabra clave, estado). |
| `/api/topic/{topicId}` | `GET` | Devuelve los detalles completos de un tópico específico. |
| `/api/topic/user/topics` | `GET` | Lista los tópicos creados por el usuario autenticado. |
| `/api/topic/user/followed-topics` | `GET` | Devuelve los tópicos seguidos por el usuario. |

> Todos los endpoints son **públicos en modo lectura** y no modifican información en la base de datos.

## 📘 Documentación del microservicio

La documentación completa y en tiempo real está disponible a través de **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8088/swagger-ui/index.html)**

O puedes acceder directamente mediante la URL:

```
http://localhost:8088/swagger-ui/index.html
```


## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Topic Read Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)



