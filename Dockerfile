# stage 1 : Gradle Build
FROM gradle:8.3-jdk17 AS builder
WORKDIR /app

## COPY Gradle Setting
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

## COPY source
COPY src ./src

## Build (except test)
RUN gradle clean build -x test

# stage 2 : RUNTIME
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

## COPY build .jar
COPY --from=builder /app/build/libs/*.jar app.jar

## PORT
ENV PORT=8079
EXPOSE 8079

## RUN
ENTRYPOINT ["java", "-Xms128m", "-Xms512m", ".jar", "/app.jar"]