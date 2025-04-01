

## Day2. Spring Boot 🔗 POSTGRESQL

#### 1. PostgreSQL 설정 변경
- Database 이름 변경 : 1d2hDB
- Schema 생성 : 1d2h
- 계정 별 Privilege 설정
    - 1d2hADM
        - 1d2hDB : OWNER, CREATE, TEMPORARY, CONNECT
        - 1d2hDB.1d2h : CREATE, USAGE
    - 1d2hAPP
        - 1d2hDB : CONNECT
        - 1d2hDB.1d2h : CREATE, INSERT, SELECT, UPDATE, DELETE, EXECUTE

#### 2. DB 연결 속성 값 변경
- RUN Configurations, .env 변수 추가 (POSTGRES_DATABASE, POSTGRES_SCHEMA)
- application.yml 수정
    - spring.datasource.url : ${POSTGRES_DATABASE}?currentSchema=${POSTGRES_SCHEMA}

#### 3. Entity 설정 (USER)
- Entity 클래스 작성 (com.raszsixt._d2h.entity.User)
- JPA를 통해 PostgreSQL Table 생성 확인 완료