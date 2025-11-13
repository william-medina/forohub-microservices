# 🌐 ForoHub - [Auth Server]

![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![Java](https://img.shields.io/badge/Language-Java-blue)
![OAuth2](https://img.shields.io/badge/Security-OAuth2-orange)
![Thymeleaf](https://img.shields.io/badge/Frontend-Thymeleaf-purple)
![Eureka](https://img.shields.io/badge/Discovery-Eureka-lightgrey)
![Spring Cloud](https://img.shields.io/badge/Cloud-Spring%20Cloud-yellowgreen)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-lightblue)

## 📚 Índice

1. [📝 Descripción](#-descripción)
2. [✨ Características principales](#-características-principales)
3. [💻 Tecnologías principales](#-tecnologías-principales)
4. [📦 Dependencias](#-dependencias)
5. [🔧 Requisitos del proyecto](#-requisitos-del-proyecto)
6. [🧩 Variables de Entorno](#-variables-de-entorno)
7. [🧱 Arquitectura y Flujo de Autenticación OAuth2](#-arquitectura-y-flujo-de-autenticación-oauth2)
8. [🔒 Seguridad y Acceso a Endpoints](#-seguridad-y-acceso-a-endpoints)
9. [🗄️ Base de Datos](#-base-de-datos)
10. [🔗 Endpoints Principales](#-endpoints-principales)
11. [👨‍💻 Autor](#-autor)

## 📝 Descripción

El **Auth Server** es el microservicio encargado de manejar la **autenticación de usuarios** en el ecosistema **ForoHub**. Implementa **OAuth2 Authorization Server** y se integra con el frontend en React mediante un **formulario de login en Thymeleaf** que mantiene el mismo estilo que el frontend.

Su función principal es:

- Validar credenciales de usuarios mediante el `user-service`.
- Generar códigos de autorización (**authorization codes**) y tokens de acceso y refresh.
- Servir como intermediario seguro junto con el **Token Gateway** para la obtención y renovación de tokens.
- Proteger endpoints internos, permitiendo que **solo `token-gateway` pueda acceder a ellos**.


## ✨ Características principales

- 🔒 **OAuth2 Authorization Server** con soporte para Authorization Code Flow.
- 🖥️ **Formulario de login en Thymeleaf** público, pero redirige solo desde el frontend autorizado.
- 🔁 **Integración con Token Gateway** para emisión y renovación de tokens.
- 🌐 **Rutas internas protegidas** que solo pueden ser accedidas por `token-gateway`.
- 📊 **Registro y descubrimiento** mediante Eureka.
- 🗂️ **Persistencia con MySQL** y migraciones gestionadas con Flyway.
- 🧩 **Validación de usuarios externa** mediante `user-service` usando Feign.


# 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.
- **MySQL**: Base de datos para los microservicios transaccionales.
- **OAuth2 y JWT**: Autenticación y autorización segura de usuarios.
- **API REST & Feign Clients**: Comunicación entre microservicios.

## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Data JPA**: Facilita la persistencia de datos en bases de datos relacionales (MySQL).
- **Flyway**: Migraciones y versionamiento de bases de datos.
- **MySQL Connector**: Conector JDBC para interactuar con MySQL.
- **Lombok**: Reduce código repetitivo con anotaciones (getters, setters, constructores).
- **Spring Cloud Netflix Eureka**: Registro y descubrimiento de microservicios.
- **Spring Cloud Config**: Configuración centralizada para los microservicios.
- **Spring Cloud OpenFeign**: Clientes HTTP declarativos para comunicación entre microservicios.
- **Spring Boot Starter Actuator**: Monitorización y métricas de los microservicios.
- **Spring Boot Starter Thymeleaf**: Renderizado de plantillas para login y UI integrada.
- **Spring Boot Starter OAuth2 Authorization Server**: Servidor OAuth2 para emisión de tokens.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **MySQL** para los microservicios transaccionales.
- **IntelliJ IDEA** o cualquier IDE compatible con Java.

## 🧩 Variables de Entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 📊 Base de Datos MySQL
MYSQL_HOST=your_mysql_host
MYSQL_PORT=your_mysql_port
MYSQL_AUTH_SERVER=your_auth_server_username
MYSQL_AUTH_PASSWORD=your_auth_server_password

# 🌍 Frontend y OAuth2
FRONTEND_URL=http://localhost:5173
AUTH_CLIENT_ID=your_client_id
AUTH_CLIENT_SECRET=your_client_secret
AUTH_REDIRECT_URI=your_frontend_oauth_callback
AUTH_SERVER_ISSUER_URI=http://localhost:9000

# 🏗️ Infraestructura y servicios
CONFIG_SERVER_HOST=your_config_server_url
EUREKA_URL=your_eureka_server_url
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

## 🧠 Arquitectura y Flujo de Autenticación OAuth2

El **Auth Server** implementa el patrón **OAuth2 Authorization Server**, siendo el núcleo del sistema de autenticación de **ForoHub**. Gestiona el **login**, la emisión de **authorization codes** y la generación de **access y refresh tokens**.

<img src="../../docs/architecture/schema-authentication.svg" alt="Flujo de Autenticación OAuth2 - Auth Server" width="500"/>

**Flujo general del proceso:**

1. 🧑‍💻 El usuario selecciona **Iniciar sesión** en el frontend React.
2. 🌐 El frontend redirige al **Auth Server**, que muestra un formulario **Thymeleaf**.
3. 🔎 El usuario ingresa sus credenciales y el Auth Server valida con el **user-service**.
4. ✅ Si la autenticación es correcta, el Auth Server genera un **authorization_code**.
5. 🔁 El Auth Server redirige al frontend con el código temporal.
6. 🧩 El frontend envía el código al **Token Gateway**, que solicita los tokens (**access** y **refresh**).
7. 🔒 El **access token** se usa para llamadas autenticadas; el **refresh token** se guarda en una **cookie HttpOnly**.
8. 🚪 Las renovaciones y cierres de sesión se manejan **exclusivamente** a través del **Token Gateway**.

> Este diseño desacopla la autenticación del frontend, centraliza el control de tokens y mejora la seguridad del ecosistema **ForoHub**.

## 🔒 Seguridad y Acceso a Endpoints

- Formulario de login público, pero solo válido si proviene del dominio configurado del frontend.
- Tokens emitidos incluyen **access token** y **refresh token**:
  - **Access token**: para peticiones a microservicios.
  - **Refresh token**: almacenado en **cookie HttpOnly**.
- Logout y renovación de tokens se gestionan únicamente a través del `Token Gateway`.

## 🗄️ Base de Datos

El Auth Server utiliza **MySQL** para persistir información de clientes OAuth2 y tokens emitidos.  
Se gestionan mediante **Flyway** para versionamiento y migraciones automáticas.

### Tablas principales

1. `oauth2_registered_client`  
   Almacena la configuración de clientes OAuth2 (IDs, secretos, URIs de redirección, grant types, scopes, etc.).

2. `oauth2_authorization`  
   Almacena los códigos de autorización, access tokens, refresh tokens y metadata asociada a cada usuario autenticado.

> Estas tablas permiten que el Auth Server gestione clientes, sesiones y tokens de manera segura y auditada.


## 🔗 Endpoints Principales

El **Auth Server** expone solo unos pocos endpoints que son utilizados principalmente por el **Token Gateway**.

El formulario `/login` es público, pero solo redirige desde el dominio autorizado del frontend.  

Todos los demás endpoints están protegidos y solo pueden ser accedidos por **Token Gateway**.

| Endpoint           | Método | Descripción                                                                                 |
|-------------------|--------|---------------------------------------------------------------------------------------------|
| `/login`           | GET    | Formulario de login en Thymeleaf. Público, pero solo accesible desde el frontend autorizado. |
| `/oauth2/token`    | POST   | Obtener **Access Token** y **Refresh Token** usando `grant_type=authorization_code`.        |
| `/oauth2/token`    | POST   | Obtener nuevo **Access Token** usando `grant_type=refresh_token`.                           |
| `/oauth2/revoke`   | POST   | Revocar refresh token previamente emitido.                              |

> **Nota:** Todos los endpoints internos de OAuth2 están protegidos y no deben ser accesibles directamente desde el frontend; solo **Token Gateway** puede interactuar con ellos.

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Auth Server]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)
