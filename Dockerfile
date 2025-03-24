FROM openjdk:17-jdk-slim
WORKDIR /1d2h
COPY target/*.jar 1d2h.jar
EXPOSE 8079
CMD ["java", "-jar", "1d2h.jar"]