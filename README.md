# Kanban board
<img src="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&style=for-the-badge&logo=SpringBoot&logoColor=white"> <img src="https://img.shields.io/badge/MongoDB-47A248.svg?&style=for-the-badge&logo=MongoDB&logoColor=white"> <img src="https://img.shields.io/badge/Vue.js-4FC08D.svg?&style=for-the-badge&logo=Vue.js&logoColor=white"> <img src="https://img.shields.io/badge/Vuetify-1867C0.svg?&style=for-the-badge&logo=Vuetify&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/Visual Studio Code-007ACC.svg?&style=for-the-badge&logo=VisualStudioCode&logoColor=white">

> Webflux와 Vue.js 프레임워크를 사용한 칸반 보드 프로젝트

Webflux, Vue, Docker, Mongodb, Test, Spring REST docs에 대한 이해를 목표로 진행한 프로젝트 입니다. 
구글 및 깃허브 OAuth2를 통해 로그인을 구현하였으며 [Marked](https://marked.js.org/)를 활용하여 Markdown 기반 Task를 작성할 수 있습니다. 
작성한 Task는 [Vue Draggable](https://github.com/SortableJS/Vue.Draggable)을 사용하여 특정 행 또는 순서를 변경할 수 있습니다. 
단위 테스트를 작성하고 Spring REST docs를 통해 문서화하였으며, Docker를 통해 Spring boot, Vue.js 그리고 MongoDB를 컨테이너화하고 compose를 통해 다중 컨테이너 어플리케이션을 구성하였습니다.

## Features
- 보드 관리
- 태그 관리
- Task 기반 달력
- 로그인/아웃

## Skills
- Backend
    - Java 11, Spring Boot, Spring Data MongoDB, Spring Security, Spring Validation, Spring REST docs, JWT, Gradle, Mockito, Junit 5, Docker
- Frontend
    - Vue.js, Vuetify, Vuex, Vue-Router, Axios
- Database
    - MongoDB
- Tool
    - Visual Studio Code

## Systen architecture
![kanban-arch](https://user-images.githubusercontent.com/48203569/134038605-ae0ac49b-3a99-4bff-8dcf-80f2d448e24d.png)  
Spring Boot, MongoDB 그리고 Nginx를 통한 Vue.js 배포를 Docker를 활용해 컨테이너화하고 Docker Compose를 통해 다층 컨테이너 어플리케이션을 정의했습니다.  
frontend에서 backend 호출 시 cors 문제를 해결하기 위해 proxy를 설정하였습니다.


## ERD
![kanban-erd](https://user-images.githubusercontent.com/48203569/134038533-72236b43-8efa-4287-919a-f1e4f86b7e6f.png)

## Screen flow
![kanban-flow](https://user-images.githubusercontent.com/48203569/134038502-40e94c46-cc47-4157-a5f8-f8eea6067a12.png)

## Installation and Getting Started
시작하기 앞서 Google와 Github의 OAuth Client ID와 Client Secret이 필요합니다.
또한, Authorization callback URL을 http://localhost:9999/login/oauth2/code/{ github or google }로 등록해야 합니다.

```
git clone https://github.com/oognuyh/kanban-spring.git
```

해당 프로젝트를 클론 후 아래의 application-oauth2.yml을 부여받은 값으로 교체하여 추가해야 합니다.

```
spring:
    security:
        oauth2:
            client:
                registration:
                    google:
                        client-id: YOUR CLIENT ID
                        client-secret: YOUR CLIENT SECRET
                        scope:
                            - profile
                            - email
                    github:
                        client-id: YOUR CLIENT ID
                        client-secret: YOUR CLIENT SECRET
```

추가하였다면 아래의 명령을 통해 실행시킬 수 있습니다.

```
cd kanban-spring
./gradlew bootJar
docker-compose up --build
```

실행된 후, [localhost:9998](http://localhost:9998) 로 접속하여 확인할 수 있습니다.

## Screens
- {frontend}/board
![calendar](https://user-images.githubusercontent.com/48203569/134038337-fa0557fe-c779-4d0b-9557-cff6d8129057.png)

- {frontend}/board/{id}
![board](https://user-images.githubusercontent.com/48203569/134038402-085d6451-56b9-473e-becb-cae69a7b1d37.png)
![manage-tags](https://user-images.githubusercontent.com/48203569/134038396-36b909ad-e72b-47b2-a4b0-7452064495f2.png)
![edit-task](https://user-images.githubusercontent.com/48203569/134038391-e869d7be-02e9-4e65-9f7d-27d9932ab045.png)

- {frontend}/login
![login](https://user-images.githubusercontent.com/48203569/134038466-32c2d5cf-57b2-483f-833b-d8416f2daf34.png)

- {backend}/docs/api-docs.html
![apidocs](https://user-images.githubusercontent.com/48203569/134038677-53cf6409-bd31-4b78-9e7d-c379663fd759.png)

## What i learned
- Spring boot
- Webflux
- MongoDB
- Vue
- JWT 
- Unit tests
- Spring REST docs
- OAuth2
- Docker

## Library used
- [Marked](https://marked.js.org/)
- [Vuetify](https://vuetifyjs.com)
- [Vue Draggable](https://github.com/SortableJS/Vue.Draggable)
- [momentjs](https://momentjs.com/)