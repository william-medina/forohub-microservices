# 🌐 ForoHub - [API Gateway]

![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![Java](https://img.shields.io/badge/Language-Java-blue)
![Spring Cloud Gateway](https://img.shields.io/badge/Cloud-Spring%20Cloud%20Gateway-purple)
![JWT](https://img.shields.io/badge/Security-JWT-orange)
![Eureka](https://img.shields.io/badge/Discovery-Eureka-lightgrey)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-yellowgreen)
![Reactive](https://img.shields.io/badge/Stack-Reactive-blueviolet)


## 📚 Índice

1. [📝 Descripción](#-descripción)
2. [✨ Características principales](#-características-principales)
3. [💻 Tecnologías principales](#-tecnologías-principales)
4. [📦 Dependencias](#-dependencias)
5. [🔧 Requisitos del proyecto](#-requisitos-del-proyecto)
6. [🧩 Variables de Entorno](#-variables-de-entorno)
7. [🧱 Arquitectura y Comunicación](#-arquitectura-y-comunicación)
8. [🔒 Seguridad y Validación de Tokens](#-seguridad-y-validación-de-tokens)
9. [🎯 Gestión de Endpoints Públicos y Privados](#-gestión-de-endpoints-públicos-y-privados)
10. [🛡️ Cabecera de Identificación de Usuario](#-cabecera-de-identificación-de-usuario)
11. [⚙️ Configuración del Gateway](#-configuración-del-gateway)
12. [👨‍💻 Autor](#-autor)


## 📝 Descripción

El **API Gateway** es el **punto de entrada central** del ecosistema de microservicios de **ForoHub**.  
Su función principal es **gestionar, proteger y enrutar** las solicitudes externas hacia los microservicios internos, garantizando seguridad, eficiencia y control de acceso.

Este componente actúa como una capa de control entre los clientes y los servicios del backend, validando tokens JWT, aplicando filtros de seguridad y bloqueando el acceso directo a endpoints internos no autorizados.


## ✨ Características principales

- 🔒 **Validación JWT:** Verifica la autenticidad y vigencia de los tokens emitidos por el `auth-server`.
- 🧭 **Ruteo dinámico:** Se integra con **Eureka Server** para descubrir servicios de forma automática.
- ⚙️ **Gestión de endpoints:** Distingue entre rutas públicas y privadas de forma centralizada.
- 🧩 **Cabecera de usuario:** Inyecta la información del usuario autenticado (`X-User-Id`) hacia los microservicios destino.
- 🚫 **Protección interna:** Bloquea el acceso externo a endpoints de comunicación interna entre microservicios.
- 🌐 **Arquitectura reactiva:** Desarrollado con **Spring Cloud Gateway** y **Spring WebFlux** para un manejo eficiente y no bloqueante.

## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.
- **JWT**: Autenticación y autorización segura de usuarios.

## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Boot Starter Web / WebFlux**: Para exponer APIs REST y flujos reactivos.
- **Spring Boot Starter Validation**: Validación de objetos y parámetros de entrada.
- **Lombok**: Reduce código repetitivo con anotaciones (getters, setters, constructores).
- **Spring Cloud Netflix Eureka**: Registro y descubrimiento de microservicios.
- **Spring Cloud Config**: Configuración centralizada para los microservicios.
- **Spring Boot Starter Actuator**: Monitorización y métricas de los microservicios.
- **Spring Boot Starter OAuth2 Resource Server**: Validación de tokens en solicitudes al API Gateway.
- **Spring Cloud Starter Gateway**: API Gateway para enrutamiento, filtrado y seguridad de solicitudes.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **IntelliJ IDEA** o cualquier IDE compatible con Java.

## 🧩 Variables de entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 🏗️ Infraestructura y servicios
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default

# 🌍 Frontend y OAuth2
FRONTEND_URL=http://localhost:5173
AUTH_SERVER_ISSUER_URI=http://localhost:9000
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.


## 🧱 Arquitectura y Comunicación

El **API Gateway** se comunica con todos los microservicios registrados en **Eureka Server**.  
Cada solicitud que llega al Gateway pasa por filtros globales que determinan:

1. Si el endpoint solicitado es **público** o **privado**.
2. Si el token JWT es **válido** y pertenece a un usuario autenticado.
3. Si el endpoint pertenece a la **red interna de microservicios**, se bloquea el acceso externo.

📊 **Esquema general:**

```
Cliente → API Gateway → Eureka → Microservicios
```

Los microservicios no son accedidos directamente desde el exterior; toda comunicación externa pasa por el Gateway, que se encarga de autorizar, filtrar y reenviar las peticiones válidas.

## 🔒 Seguridad y Validación de Tokens

El Gateway implementa un **filtro de seguridad reactivo** que valida los **JWT** firmados por el `auth-server`.  
Cada solicitud con `Authorization: Bearer <token>` se valida antes de llegar al servicio destino.

### 🔁 Flujo de validación:
1. El cliente envía una solicitud con el token JWT.
2. El Gateway valida el token contra el **issuer URI** configurado (`app.auth.issuer-uri`).
3. Si el token es válido, el flujo continúa.
4. Si el token es inválido, expirado o ausente, se devuelve `401 Unauthorized`.


## 🎯 Gestión de Endpoints Públicos y Privados

### 🔓 Endpoints Públicos
No requieren autenticación.  
Ejemplos comunes:
- `/api/auth/create-account` (POST)
- `/api/auth/confirm-account/{token}`(POST)
- `/api/topic` (GET)
- `/api/reply/{replyId}` (GET)
- `/api/course` (GET)

El Gateway elimina el encabezado `Authorization` en estos casos para evitar validaciones innecesarias.

### 🔐 Endpoints Privados
Todos los demás endpoints requieren un **token JWT válido**.  
El Gateway valida el token y añade el identificador del usuario autenticado antes de redirigir la solicitud.

> **Ruteo condicional de `/api/topic`:**
> - **GET** → se enruta al servicio `topic-read-service` para lecturas de topics. **(publico)**
> - **POST, PUT, PATCH, DELETE** → se enruta al servicio `topic-service` para creación, actualización y eliminación de topics. **(privado)**


### 🚫 Endpoints Internos
Las rutas de comunicación entre microservicios (por ejemplo, `/internal/**`) están **protegidas** y no pueden ser accedidas desde el exterior. Esto evita el acceso directo y asegura que solo el tráfico interno pueda interactuar entre servicios.


## 🛡️ Cabecera de Identificación de Usuario

Para los endpoints privados, una vez validado el JWT, el Gateway extrae el identificador del usuario (`user_id`) y lo añade a la solicitud mediante la cabecera:

```
X-User-Id: <id_del_usuario>
```

De esta forma, los microservicios pueden reconocer al usuario sin tener que validar nuevamente el token.


## ⚙️ Configuración del Gateway

La configuración de rutas y filtros se realiza mediante el archivo `application.yml`.  
Cada servicio se define con su ruta, destino y condiciones de seguridad.

Ejemplo:

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Lectura de topics
        - id: topic-read-service
          uri: lb://topic-read-service
          predicates:
            - Path=/api/topic/**
            - Method=GET
          filters:
            - StripPrefix=1

        # Escritura/modificación de topics
        - id: topic-service
          uri: lb://topic-service
          predicates:
            - Path=/api/topic/**
            - Method=POST,PUT,PATCH,DELETE
          filters:
            - StripPrefix=1
```

El prefijo `lb://` indica que la ruta se resolverá dinámicamente a través de Eureka (Load Balancer).


## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [API Gateway]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

