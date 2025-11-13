# 🌐 ForoHub - [Content Analysis Service]
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![Spring AI](https://img.shields.io/badge/AI-Spring%20AI-orange)
![WebFlux](https://img.shields.io/badge/Reactive-WebFlux-brightblue)
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
7. [🤖 Inteligencia Artificial](#-inteligencia-artificial)
8. [🧱 Arquitectura y Comunicación](#-arquitectura-y-comunicación)
9. [🔒 Endpoints Internos](#-endpoints-internos)
10. [📘 Documentación del microservicio](#-documentación-del-microservicio)
11. [👨‍💻 Autor](#-autor)


## 📝 Descripción

El **Content Analysis Service** es el microservicio encargado de la **validación automática de contenido** dentro de la plataforma **ForoHub**. Su objetivo principal es **detectar contenido inapropiado, ofensivo, spam o irrelevante** en títulos, descripciones y nombres de usuario antes de que sean almacenados o mostrados en otros servicios.

Este servicio se integra con **OpenAI** mediante **Spring AI** y **WebFlux** para realizar análisis de manera reactiva y asíncrona.  
No requiere base de datos, no consume ni publica eventos, y su uso está limitado a **comunicaciones internas entre microservicios**.


## ✨ Características principales

- 🤖 **Validación de contenido textual**: Analiza títulos y descripciones para detectar spam, lenguaje ofensivo o contenido inapropiado.
- 🧑‍💻 **Validación de nombres de usuario**: Garantiza que los nombres de usuario sean válidos y no contengan contenido ofensivo o sin sentido.
- ⚡ **Reactividad y rendimiento**: Procesa las solicitudes de manera asíncrona usando **WebFlux** y **Spring AI**.
- 🔒 **Uso interno exclusivo**: Solo se accede a través de endpoints internos para otros microservicios, garantizando seguridad y consistencia.


## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo de microservicio en Java.
- **Spring AI**: Integración con modelos de OpenAI para análisis de texto.
- **Spring WebFlux**: Procesamiento reactivo y asíncrono de solicitudes.
- **OpenAPI / Swagger UI**: Documentación interactiva de endpoints.


## 📦 Dependencias

- **Spring Boot Starter WebFlux**: Soporte para programación reactiva.
- **Spring AI Starter OpenAI**: Integración con modelos de lenguaje de OpenAI.
- **SpringDoc OpenAPI / Swagger UI**: Generación automática de documentación de API.
- **Lombok**: Reducción de código repetitivo con anotaciones.
- **Spring Boot Starter Validation**: Validación de objetos y parámetros de entrada.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para gestión de dependencias.
- **Credenciales de IA** (OpenAI) para el análisis de contenido.
- **IntelliJ IDEA** u otro IDE compatible con Java.


## 🧩 Variables de Entorno

Estas variables son necesarias para el correcto funcionamiento del microservicio.

```dotenv
# 🤖 Inteligencia Artificial
AI_API_KEY=your_ai_api_key
AI_ENABLED=true/false

# 🏗️ Infraestructura y servicios
EUREKA_URL=your_eureka_server_url
CONFIG_SERVER_HOST=your_config_server_url
SPRING_PROFILES_ACTIVE=default
```

> Reemplaza los valores de ejemplo con los detalles de tu configuración real.

## 🤖 Inteligencia Artificial

La API utiliza **inteligencia artificial generativa**  para detectar contenido inapropiado en los tópicos, respuestas y nombres de usuario. Esta funcionalidad ayuda a garantizar que las interacciones dentro de la aplicación se mantengan dentro de los límites de respeto y seguridad. Se utiliza la API de OpenAI para procesar y verificar los datos, asegurando que el contenido generado o recibido cumpla con los estándares adecuados.

### Configuración de la API de OpenAI

Para habilitar la detección de contenido inapropiado, es necesario configurar la **API key** de OpenAI y el modelo que se utilizará para procesar las solicitudes. Asegúrate de tener la **API key** activa y accesible.

1. **Configura la API key de OpenAI:**

    - En el archivo `application.yml`, se presenta la siguiente línea para configurar la **API key**:

      ```yml
       spring:
         ai:
           openai:
              api-key: ${AI_API_KEY}
      ```

    - Luego, asegúrate de que la variable de entorno `AI_API_KEY` esté configurada en tu sistema operativo o IDE con la **API key** proporcionada por OpenAI.

2. **Habilitar o deshabilitar la funcionalidad de IA:**

   Si no cuentas con las credenciales necesarias o simplemente deseas deshabilitar la funcionalidad de IA, puedes desactivar esta característica. Esto evitará que el sistema realice validaciones de contenido, lo que podría ahorrar recursos y prevenir posibles errores causados por credenciales incorrectas o inexistentes.

   Para hacerlo, puedes configurar la variable de entorno `AI_ENABLED` de la siguiente manera:

    - En el archivo `application.properties`, debes configurar la siguiente línea:

      ```yml
      ai:
        enabled: ${AI_ENABLED:true}
      ```

    - Luego, configura la variable de entorno `AI_ENABLED` en tu sistema operativo o IDE. Si deseas deshabilitar la IA, establece la variable en `false`. Si quieres habilitar la funcionalidad de IA, configúralo en `true`.

   > **⚠️ Importante:** Si deshabilitas la funcionalidad de IA, los contenidos no serán validados antes de ser procesados, lo que podría permitir que se envíe contenido inapropiado.


### Detección de Contenido Inapropiado

La inteligencia artificial se encarga de verificar el contenido ingresado por los usuarios, incluyendo nombres de usuario, tópicos y respuestas. Si se detecta contenido inapropiado, la API enviará un mensaje de error y evitará que el usuario cree o actualice un tópico, respuesta o nombre de usuario.


## 🧱 Arquitectura y Comunicación

`content-analysis-service` forma parte del **Business Domain** de ForoHub y se comunica con otros servicios mediante **endpoints internos**.  

- **Entrada de datos**: recibe textos y nombres de usuario de otros microservicios como `topic-service` , `reply-service` o `user-service`.
- **Procesamiento**: utiliza **Spring AI + OpenAI** para validar el contenido de manera asíncrona.
- **Salida de datos**: devuelve un objeto con la validación, indicando si el contenido es válido o inapropiado.

> No utiliza bases de datos ni eventos; su propósito es únicamente **procesamiento y validación de contenido**.


## 🔒 Endpoints Internos

Estos endpoints **no están expuestos al API Gateway** y son usados exclusivamente para la comunicación interna entre microservicios:

| Endpoint             | Método | Descripción |
|---------------------|--------|-------------|
| `/validation/content`  | `POST` | Valida un texto (título o descripción) para detectar contenido inapropiado, spam o lenguaje ofensivo. |
| `/validation/username` | `POST` | Valida un nombre de usuario, detectando contenido ofensivo, spam o nombres sin sentido. |

> 🧩 Documentados con **OpenAPI/Swagger** y de uso interno.


## 📘 Documentación del microservicio

La documentación completa está disponible mediante **Swagger UI**:

🔗 **[Ver documentación Swagger UI](http://localhost:8087/swagger-ui/index.html)**

O puedes acceder directamente mediante la URL:


```
http://localhost:8087/swagger-ui/index.html
```

## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Content Analysis Service]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

