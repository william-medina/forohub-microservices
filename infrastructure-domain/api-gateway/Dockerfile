FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=null
ENV EUREKA_URL=null
ENV CONFIG_SERVER_HOST=null
ENV AUTH_SERVER_ISSUER_URI=null
ENV FRONTEND_URL=null
ENTRYPOINT ["java","-jar","/app.jar"]