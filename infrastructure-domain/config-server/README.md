# 🌐 ForoHub - [Config Server]

![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![Java](https://img.shields.io/badge/Language-Java-blue)
![YAML](https://img.shields.io/badge/Config-YAML-orange)
![GitHub](https://img.shields.io/badge/Repo-GitHub-lightgrey)
![Spring Cloud](https://img.shields.io/badge/Cloud-Spring%20Cloud-blueviolet)
![Config Server](https://img.shields.io/badge/Server-Config%20Server-yellowgreen)
![DevOps](https://img.shields.io/badge/Process-DevOps-red)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-lightgrey)


## 📚 Índice

1. [📝 Descripción](#-descripción)
2. [✨ Características principales](#-características-principales)
3. [💻 Tecnologías principales](#-tecnologías-principales)
4. [📦 Dependencias](#-dependencias)
5. [🔧 Requisitos del proyecto](#-requisitos-del-proyecto)
6. [🧩 Variables de Entorno](#-variables-de-entorno)
7. [📁 Creación del Repositorio de Configuraciones en GitHub](#-creación-del-repositorio-de-configuraciones-en-github)
8. [🧾 Estructura de Archivos de Configuración](#-estructura-de-archivos-de-configuración)
9. [🔒 Conexión segura con GitHub](#-conexión-segura-con-github)
10. [👨‍💻 Autor](#-autor)

## 📝 Descripción

El **Config Server** es un microservicio del ecosistema **ForoHub** que centraliza y distribuye las configuraciones de todos los microservicios de la plataforma.

Utiliza **Spring Cloud Config Server** para conectarse de forma segura a un **repositorio remoto de GitHub**, desde donde obtiene y gestiona los archivos de configuración (`application.yml` y `application-{perfil}.yml`).

Esto permite que todos los servicios compartan configuraciones consistentes, seguras y versionadas, reduciendo errores y mejorando la escalabilidad del sistema.


## ✨ Características principales

- 📦 **Centralización**: Todas las configuraciones están unificadas en un solo repositorio Git.
- 🔄 **Actualización dinámica**: Los microservicios pueden refrescar sus configuraciones sin necesidad de redeploy.
- 🔒 **Conexión segura** mediante tokens personales de GitHub.
- 🌍 **Soporte multientorno** (`dev`, `qa`, `prod`) con configuración por perfiles.
- ⚙️ **Integración con Spring Cloud Config** y compatibilidad con Eureka y otros servicios del ecosistema ForoHub.


## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.



## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Cloud Netflix Eureka**: Registro y descubrimiento de microservicios.
- **Spring Boot Starter Actuator**: Monitorización y métricas de los microservicios.
- **Spring Cloud Config Server**: Configuración centralizada (`config-server`).


## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **IntelliJ IDEA** o cualquier IDE compatible con Java.

## 🧩 Variables de entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 🏗️ Infraestructura y servicios
EUREKA_URL=your_eureka_server_url
SPRING_PROFILES_ACTIVE=default

# 💻 Repositorio de configuración
GIT_URI=your_git_config_repo
GIT_USERNAME=your_git_username
GIT_TOKEN=your_git_token
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

---

## 📁 Creación del Repositorio de Configuraciones en GitHub

Antes de iniciar el **Config Server**, es necesario contar con un **repositorio remoto** donde se almacenarán las configuraciones de todos los microservicios del ecosistema.

Este repositorio funcionará como una **fuente única de verdad (Single Source of Truth)** para todos los entornos (`dev`, `qa`, `prod`).

### 🏗️ Pasos para crear el repositorio

1. Crea un nuevo repositorio en GitHub llamado, por ejemplo:  
   **`microservice-configs`** (privado).
2. Clónalo localmente en tu máquina:
   ```bash
   git clone https://github.com/<TU_USUARIO>/microservice-configs.git
    ```
3. Dentro del repositorio, crea una carpeta con el nombre de tu proyecto principal, por ejemplo:
    ```bash
   forohub/
   ```
4. Dentro de esa carpeta, crea subcarpetas para cada microservicio:
    ```
   forohub/
   ├─ user-service/
   ├─ topic-service/
   ├─ reply-service/
   ├─ course-service/ 
   ├─ content-analysis-service/
   ├─ notification-service/
   ├─ email-service/
   ├─ topic-read-service/
   ├─ api-gateway/
   ├─ auth-server/
   └─ token-gateway/
    ```

   Cada microservicio contendrá sus archivos de configuración base y por entorno.

## 🧾 Estructura de Archivos de Configuración

Cada microservicio dentro del repositorio de configuraciones debe seguir una estructura clara y estandarizada.  
Esto permite que el **Config Server** pueda identificar fácilmente los archivos según el servicio y el entorno correspondiente.

### 📂 Organización recomendada

```
foro-hub/
├─ user-service/
│ ├─ user-service.yml
│ ├─ user-service-qa.yml
│ └─ user-service-prod.yml
│
├─ topic-service/
│ ├─ topic-service.yml
│ ├─ topic-service-qa.yml
│ └─ topic-service-prod.yml
│
└─ reply-service/
  ├─ reply-service.yml
  ├─ reply-service-qa.yml
  └─ reply-service-prod.yml
```

Cada archivo debe coincidir con el valor definido en `spring.application.name` del microservicio correspondiente. Esto permite que el **Config Server** pueda resolver correctamente la configuración al consultar:

### 🧩 Ejemplo: Configuración del `topic-service`

A continuación se muestra un ejemplo real del microservicio **Topic Service**, con archivos específicos para cada entorno.

#### 🧱 Entorno default (`topic-service.yml`)

```yaml
db:
  url: jdbc:mysql://localhost:3306/topic_db
  username: topic_service
  password: your_topic_password

kafka:
  bootstrap-servers: localhost:9092
  group-id: topic-service-group-dev

eureka:
  url: http://localhost:8761/eureka
```

#### 🧪 Entorno de QA (`topic-service-qa.yml`)

```yaml
db:
  url: jdbc:mysql://localhost:3306/topic_db
  username: topic_service
  password: your_topic_password

kafka:
  bootstrap-servers: localhost:9092
  group-id: topic-service-group-qa

eureka:
  url: http://localhost:8761/eureka
```

#### 🚀 Entorno de Producción (`topic-service-prod.yml`)

```yml
db:
  url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT:3306}/topic_db
  username: ${MYSQL_TOPIC_SERVICE}
  password: ${MYSQL_TOPIC_PASSWORD}

kafka:
  bootstrap-servers: ${KAFKA_SERVERS}
  group-id: topic-service-group-prod

eureka:
  url: ${EUREKA_URL}
```

En el entorno de producción, se utilizan variables de entorno para proteger credenciales y datos sensibles.
De esta forma, la configuración se vuelve dinámica y segura, evitando exponer información en el código.

Cada microservicio del ecosistema ForoHub debe seguir una estructura similar a la mostrada para el topic-service.
Solo deben cambiarse las configuraciones específicas de cada servicio.

## 🔒 Conexión Segura con GitHub

El **Config Server** se conecta a un repositorio remoto en **GitHub** que almacena todos los archivos de configuración de los microservicios. Para garantizar una conexión segura y evitar exponer credenciales, se recomienda usar un **Fine-grained Personal Access Token**.

Este token permite al servidor leer las configuraciones del repositorio sin necesidad de autenticación manual y con permisos limitados.


### 🧭 Pasos para generar un Fine-grained Token en GitHub

1. Inicia sesión en tu cuenta de GitHub.
2. Ve a **Settings → Developer settings → Personal access tokens → Fine-grained tokens**.
3. Haz clic en **Generate new token**.
4. En **Repository access**, selecciona **Only select repositories** y elige tu repositorio de configuraciones (por ejemplo, `microservice-configs`).
5. En **Repository permissions**, otorga permisos de **Read repository contents**.
6. Genera el token y cópialo — se mostrará solo una vez.
7. Guarda el token en un lugar seguro y configúralo como variable de entorno en tu **Config Server**.

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Config Server]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

