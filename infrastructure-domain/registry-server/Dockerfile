FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/registry-server-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=null
ENTRYPOINT ["java", "-jar", "/app.jar"]