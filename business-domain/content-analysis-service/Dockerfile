FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/content-analysis-service-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=null
ENV EUREKA_URL=null
ENV CONFIG_SERVER_HOST=null
ENV AI_API_KEY=null
ENV AI_ENABLED=null
ENTRYPOINT ["java","-jar","/app.jar"]