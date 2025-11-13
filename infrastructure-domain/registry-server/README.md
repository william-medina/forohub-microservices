# 🌐 ForoHub - [Registry Server]

![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![Java](https://img.shields.io/badge/Language-Java-blue)
![Spring Cloud](https://img.shields.io/badge/Cloud-Spring%20Cloud-blueviolet)
![Eureka](https://img.shields.io/badge/Service-Discovery-yellow)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-lightgrey)
![DevOps](https://img.shields.io/badge/Process-DevOps-red)


## 📚 Índice

1. [📝 Descripción](#-descripción)
2. [✨ Características principales](#-características-principales)
3. [💻 Tecnologías principales](#-tecnologías-principales)
4. [📦 Dependencias](#-dependencias)
5. [🔧 Requisitos del proyecto](#-requisitos-del-proyecto)
6. [🔍 Verificación y Panel de Eureka](#-verificación-y-panel-de-eureka)
7. [👨‍💻 Autor](#-autor)


## 📝 Descripción

El **Registry Server** es el componente encargado del **registro y descubrimiento de servicios** en el ecosistema **ForoHub**. Está basado en **Spring Cloud Netflix Eureka Server**, permitiendo que cada microservicio se registre dinámicamente y pueda descubrir a los demás sin necesidad de configuraciones manuales.

Gracias a este servidor, los microservicios se comunican de forma segura, flexible y escalable, lo que facilita el balanceo de carga y la tolerancia a fallos.


## ✨ Características principales

- 🧭 **Service Discovery**: Todos los microservicios se registran automáticamente al iniciar.
- 🌀 **Alta disponibilidad**: Soporta clústeres Eureka replicados para entornos productivos.
- 🔄 **Actualización dinámica**: Los servicios pueden añadirse o eliminarse sin reiniciar el servidor.
- ⚙️ **Integración nativa con Spring Cloud**.
- 🧩 **Compatibilidad con Config Server** y **API Gateway**.

## 💻 Tecnologías principales

- **Spring Boot**: Desarrollo ágil de microservicios en Java.

## 📦 Dependencias

ForoHub está construido sobre una arquitectura de **microservicios**. A continuación se listan todas las principales dependencias utilizadas en los distintos servicios del proyecto:

- **Spring Boot**: Framework principal para el desarrollo de microservicios en Java.
- **Spring Boot Starter Actuator**: Monitorización y métricas de los microservicios.
- **Spring Cloud Starter Netflix Eureka Server**: Registro de servicios.

## 🔧 Requisitos del proyecto

- **JDK 21** o superior.
- **Maven** para la gestión de dependencias.
- **IntelliJ IDEA** o cualquier IDE compatible con Java.

## 🔍 Verificación y Panel de Eureka

Una vez iniciado el **Registry Server**, puedes acceder al panel de administración de **Eureka** desde tu navegador en la siguiente URL:

🔗 **http://localhost:8761**

Este panel web muestra todos los microservicios registrados en tiempo real, junto con su estado, nombre, dirección IP y puerto. Desde allí puedes verificar la disponibilidad de los servicios y el correcto funcionamiento del registro dinámico.


## 👨‍💻 Autor

**William Medina**  
Autor y desarrollador de **ForoHub - [Registry Server]**. Puedes encontrarme en [GitHub](https://github.com/william-medina)

