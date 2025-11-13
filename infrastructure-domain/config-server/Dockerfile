FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/config-server-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=null
ENV EUREKA_URL=null
ENV GIT_URI=null
ENV GIT_USERNAME=null
ENV GIT_TOKEN=null
ENTRYPOINT ["java","-jar","/app.jar"]