# DAY22. DevLog Entity 설계

#### 1. DevLog Entity 설계
- DevLog 메뉴 구성할 Entity 구현
    - DevLogGroup : 게시물 시리즈 Group Entity
    - DevLogSubscribe : 시리즈 별 구독 Entity
    - DevLogItem : 시리즈 별 게시물 Entity
    - DevLogLike : 시리즈/게시물 별 좋아요 Entity
    - DevLogShare : 시리즈/게시물 별 공유 Entity
    - DevLogLang : 게시물 별 조회수 Entity
    - DevLogLang : 게시물 별 사용한 언어 Entity

#### 2. DevLog Controller, Service, Repository 구현
- DevLog에서 사용할 controller, Service, Repository 구현
- /api/dlog/** 로 들어오는 모든 request 처리
- Spring Security "/api/dlog/**" permitAll 적용