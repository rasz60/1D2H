# Dockerfile
FROM eclipse-temurin:17-jdk-alpine

# Application JAR 복사
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Port (Fly.io)
ENV PORT=8079
EXPOSE 8079

# RUN
ENTRYPOINT["java", "-Xms128m", "-Xms512m", "-jar", "/app.jar"]

#WORKDIR /1d2h
#COPY target/*.jar 1d2h.jar
#EXPOSE 8079
#CMD ["java", "-jar", "1d2h.jar"]