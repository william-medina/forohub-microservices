# 🌐 ForoHub - [Notification Service]
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
10. [📘 Documentación del microservicio](#-documentación-del-microservicio)
11. [👨‍💻 Autor](#-autor)


## 📝 Descripción

El **Notification Service** es el microservicio encargado de **almacenar y gestionar las notificaciones** internas generadas dentro de la plataforma **ForoHub**.

Este servicio **consume eventos Kafka** provenientes de los microservicios `topic-service` y `reply-service` para registrar notificaciones relacionadas con la actividad de los usuarios. Las notificaciones se almacenan en base de datos y pueden consultarse a través de sus **endpoints expuestos**.

---

## ✨ Características principales

- 🔔 **Recepción de notificaciones:** Consume eventos de `topic-service` y `reply-service` mediante Kafka.
- 💾 **Persistencia local:** Guarda todas las notificaciones en una base de datos MySQL.
- 📬 **Consulta de notificaciones:** Permite recuperar las notificaciones asociadas a un usuario autenticado.
- 📨 **Gestión del estado de lectura:** Las notificaciones pueden marcarse como leídas o no leídas.
- 🔁 **Prevención de duplicados:** Usa la tabla `processed_events` para evitar reprocesar eventos.
- ⚙️ **Integración con `user-service`:** Verifica los usuarios involucrados antes de registrar una notificación.

## 💻 Tecnologías principales

- **Spring Boot** – Framework principal para el desarrollo del microservicio.
- **Spring Data JPA** – Gestión de persistencia en MySQL.
- **Apache Kafka** – Consumo de eventos del ecosistema ForoHub.
- **Spring Cloud OpenFeign** – Comunicación HTTP con `user-service`.
- **Spring Cloud Netflix Eureka** – Registro y descubrimiento de servicios.
- **Spring Cloud Config** – Configuración centralizada.
- **Springdoc OpenAPI / Swagger UI** – Documentación interactiva.


## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Data JPA**: Facilita la persistencia de datos en bases de datos relacionales (MySQL).
- **Flyway**: Migraciones y versionamiento de bases de datos.
- **MySQL Connector**: Conector JDBC para interactuar con MySQL.
- **Lombok**: Reduce código repetitivo con anotaciones (getters, setters, constructores).
- **SpringDoc OpenAPI / Swagger UI**: Documentación automática de la API.
- **Apache Kafka**: Comunicación basada en eventos entre microservicios.
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
MYSQL_NOTIFICATION_SERVICE=your_notification_username
MYSQL_NOTIFICATION_PASSWORD=your_notification_password

# 🏗️ Infraestructura y servicios
KAFKA_SERVERS=your_kafka_bootstrap_servers
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default
```
> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

## 🧱 Arquitectura y Comunicación

El `notification-service` pertenece al **Business Domain** y se encarga de **consumir los eventos generados por otros microservicios** del ecosistema **ForoHub** para almacenar notificaciones internas que posteriormente pueden ser consultadas desde el frontend.

**Flujos principales:**

- **Kafka:**  
  Consume eventos publicados por `topic-service` y `reply-service` relacionados con acciones como creación, edición, eliminación o marcado de soluciones.  
  No publica eventos, su función es únicamente de consumo y persistencia local.  
  Cada evento procesado se registra en la tabla `processed_events` para garantizar la **idempotencia** y evitar procesamientos duplicados.

- **Feign (REST interno):**  
  Se comunica con `user-service` para **validar y obtener información básica del usuario** involucrado en el evento recibido.

> 📩 Este servicio no emite mensajes Kafka ni tiene lógica de negocio externa; su objetivo es **centralizar la gestión y visualización de notificaciones** en la plataforma.


### 🔔 Eventos Kafka consumidos

El servicio escucha los siguientes tópicos provenientes de otros microservicios:

| Tópico Kafka | Servicio origen | Descripción general                                                                           |
|---------------|------------------|-----------------------------------------------------------------------------------------------|
| `topic-events` | `topic-service` | Cambios en tópicos (edición, eliminación, cambio en su estado).                               |
| `reply-events` | `reply-service` | Cambios en respuestas (nuevas respuestas, ediciones, eliminaciones o marcadas como solución). |

> Los mensajes se procesan usando **Spring Kafka** con modo `manual_immediate` para confirmar de forma controlada cada evento procesado.


### ⚙️ Mecanismo de idempotencia

Para evitar procesar el mismo evento más de una vez, se utiliza la tabla `processed_events`, que registra cada evento Kafka consumido.

**Flujo básico:**
1. Se recibe un evento con un `event_id` único.
2. Se verifica si ese `event_id` existe en `processed_events`.
3. Si no existe, se procesa la notificación y se almacena el evento.
4. Si ya fue procesado, el evento se ignora.

De esta manera, se garantiza la **consistencia** y se evita la duplicación de notificaciones.


### 🧩 Tipos de notificaciones

Cada evento recibido se traduce en una notificación para uno o varios usuarios.  
Las notificaciones se clasifican por `type` (entidad relacionada) y `subtype` (acción específica):

| Tipo (`type`) | Subtipo (`subtype`) | Descripción                                            |
|----------------|---------------------|--------------------------------------------------------|
| `TOPIC` | `REPLY`             | Nueva respuesta en tu tópico.                          |
| `TOPIC` | `SOLVED`            | Un tópico que sigues ha sido marcado como solucionado. |
| `TOPIC` | `REPLY`             | Nueva respuesta en un tópico que sigues                |
| `TOPIC` | `EDITED`            | Tu tópico ha sido editado.                             |
| `TOPIC` | `SOLVED`            | Tu tópico fue marcado como solucionado.                |
| `TOPIC` | `DELETED`           | Tu tópico fue eliminado.                               |
| `REPLY` | `EDITED`            | Tu respuesta fue editada.                              |
| `REPLY` | `SOLVED`            | Tu respuesta fue marcada como solución.                |
| `REPLY` | `DELETED`           | Tu respuesta fue eliminada.                            |


## 🗄️ Base de Datos

El microservicio utiliza **MySQL** como base de datos relacional.  
Las migraciones son gestionadas mediante **Flyway** para asegurar la coherencia entre entornos.

**Tablas principales:**

- **`notifications`** → Almacena las notificaciones generadas para los usuarios.
- **`processed_events`** → Registra los eventos Kafka ya consumidos para garantizar idempotencia.


## 🔗 Endpoints Expuestos

Estos endpoints están disponibles a través del **API Gateway** y permiten a los usuarios autenticados **consultar, marcar o eliminar sus notificaciones personales** dentro de la plataforma **ForoHub**.

| Endpoint                             | Método       | Descripción                                                                                                      |
|--------------------------------------|--------------|------------------------------------------------------------------------------------------------------------------|
| `/api/notify`                            | `GET`        | Obtiene todas las notificaciones del usuario autenticado, ordenadas por fecha de creación.                       |
| `/api/notify/{notifyId}`                 | `DELETE`     | Elimina una notificación específica por su ID, si pertenece al usuario autenticado.                             |
| `/api/notify/{notifyId}`                 | `PATCH`      | Marca como leída una notificación específica por su ID, si pertenece al usuario autenticado.                    |

## 📘 Documentación del microservicio

La documentación completa está disponible mediante **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8085/swagger-ui/index.html)**

O accede directamente desde tu navegador:

```
http://localhost:8085/swagger-ui/index.html
```

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Notification Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

