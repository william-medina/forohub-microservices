FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/course-service-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=null
ENV EUREKA_URL=null
ENV CONFIG_SERVER_HOST=null
ENV MYSQL_HOST=null
ENV MYSQL_PORT=null
ENV MYSQL_COURSE_SERVICE=null
ENV MYSQL_COURSE_PASSWORD=null
ENTRYPOINT ["java","-jar","/app.jar"]