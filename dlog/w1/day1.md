## Day1. BACKEND-INIT

#### 1. Spring Initializer (https://start.spring.io/)

- Projects : Gradle-Groovy
- Language : JAVA
- Spring-Boot : 3.3.10
- Packing: Jar
- JAVA : 17
- Dependencies
  - Spring Web
  - Spring Security
  - Spring Data JPA
  - PostgreSQL Driver
  - Spring Boot DevTools
  - JAVA mail sender
  - Lombok

#### 2. PostgreSQL DB 설정
- 계정 생성 : 1d2hADM, 1d2hAPP
- Database 생성 : 1d2h
- 계정 권한 부여

#### 3. IntelliJ(Community Ver.) Import
- 프로젝트 폴더 open
- gradle/wrapper/gradle-wrapper.properties -> distributionUrl 변경 (gradle-8.3-bin.zip)
- application.yml : local server port, Datasource, JPA, devtools, log 설정
- Edit Run Configurations -> Add New Configurations -> Application
  - java 17, 1d2h.main, 1d2hApplication.class 설정
  - Enviroment variables : POSTGRES_USER, POSTGRES_PASSWORD 설정

#### 4. Docker
- ROOT 경로에 Dockerfile, docker-compose.yml, .env 파일 추가
- .gitignore 파일에 .env 추가

#### 5. RUN
- Spring Security Login 화면 호출