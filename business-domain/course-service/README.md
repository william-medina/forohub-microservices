# 🌐 ForoHub - [Course Service]
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)
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

El **Course Service** es el microservicio encargado de gestionar los **cursos disponibles** dentro de la plataforma **ForoHub**.

Permite **listar cursos y obtener detalles individuales**. No publica ni consume eventos, ni se comunica con otros microservicios, ofreciendo una gestión sencilla y directa de los cursos almacenados en la base de datos.

Su objetivo es proporcionar un acceso **rápido, confiable y seguro** a la información de los cursos.


## ✨ Características principales

- 📚 **Listado de cursos**: Permite obtener todos los cursos disponibles mediante el endpoint público.
- ⚙️ **Simplicidad y fiabilidad**: Servicio independiente, sin integración externa ni dependencias en tiempo de ejecución.
- 🗄️ **Gestión interna de cursos**: Permite obtener detalles de un curso específico o varios cursos en batch, pero solo mediante los endpoints internos.


## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.
- **MySQL**: Base de datos para almacenar información de cursos.
- **API REST**: Exposición de endpoints para consulta de cursos.
- **Springdoc OpenAPI / Swagger UI**: Documentación de endpoints.


## 📦 Dependencias

- **Spring Boot**: Framework principal.
- **Spring Data JPA**: Persistencia de datos en MySQL.
- **Spring Boot Starter Web**: Exposición de APIs REST.
- **Flyway**: Migraciones de base de datos.
- **MySQL Connector**: Conector JDBC.
- **Lombok**: Reducción de código repetitivo.
- **SpringDoc OpenAPI / Swagger UI**: Documentación de endpoints.
- **Spring Boot Starter Actuator**: Monitorización del servicio.


## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **MySQL** para almacenamiento de cursos.
- **IDE compatible con Java** (IntelliJ IDEA recomendado).

## 🧩 Variables de Entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 📊 Base de Datos MySQL
MYSQL_HOST=your_mysql_host
MYSQL_PORT=your_mysql_port
MYSQL_COURSE_SERVICE=your_course_username
MYSQL_COURSE_PASSWORD=your_course_password

# 🏗️ Infraestructura y servicios
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

## 🧱 Arquitectura y Comunicación

El **Course Service** es un microservicio independiente que forma parte del **Business Domain** de ForoHub. No publica ni consume eventos, ni se comunica con otros microservicios mediante Feign o WebFlux. Su función principal es **proporcionar información sobre los cursos almacenados**.

- **REST API**: Permite exponer los cursos disponibles para consultas.
- **Internal Endpoints**: Habilita que otros microservicios puedan consultar cursos por ID o en batch.


## 🗄️ Base de Datos

El microservicio utiliza **MySQL** como base de datos relacional para almacenar la información de los cursos.  
Las **migraciones** se administran mediante **Flyway**.

**Tablas principales:**

- `courses` → Contiene información de cada curso disponibles.


## 🔗 Endpoints Expuestos

Estos endpoints pueden ser accesibles a través del **API Gateway**.

| Endpoint           | Método | Descripción |
|-------------------|--------|-------------|
| `/api/course`      | `GET`  | Obtiene todos los cursos disponibles. |

## 🔒 Endpoints Internos

Estos endpoints **no están expuestos al API Gateway** y son usados para comunicación interna entre microservicios:

| Endpoint                     | Método | Descripción |
|-------------------------------|--------|-------------|
| `/internal/course/{courseId}` | `GET`  | Obtiene un curso específico por su ID. |
| `/internal/course/batch?ids={ids}` | `GET` | Obtiene múltiples cursos enviando una lista de IDs separados por coma. |

> 🧩 Documentados con **OpenAPI/Swagger**, ocultos con `@Hidden` para evitar su exposición pública.


## 📘 Documentación del microservicio

La documentación completa está disponible mediante **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8084/swagger-ui/index.html)**

O mediante la URL directa:

```
http://localhost:8084/swagger-ui/index.html
```

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Course Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)




