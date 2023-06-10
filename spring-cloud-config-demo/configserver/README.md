# ConfigServer

이 프로젝트는 spring server 의 데모입니다.

또한 자주 쓰는 설정으로 spring config server 를 쉽게 구성할 수 있는 도커 이미지를 제공합니다.

# Config Server Docker

## 필수 환경변수

BASIC_PASSWORD : Basic 인증에 사용할 비밀번호

BASIC_USERNAME : Basic 인증에 사용할 아이디

GIT_REPO_SSH_URL : git repository url

GIT_DEFAULT_BRANCH : git repository 의 기본 브랜치

GIT_PRIVATE_KEY_PATH : git repository 에 접근하기 위한 ssh private key

ENCRYPT_KEY : 암호화를 위한 대칭키

GIT_SEARCH_PATH : git repository 에서 설정 파일을 찾을 경로

_예시 spring-cloud-config-demo/configTarget/{application}_
```text
.
├── README.md
└── spring-cloud-config-demo
    ├── README.md
    ├── configTarget
    │   ├── service1
    │   │   ├── application-dev.yml
    │   │   └── application-test.yml
    │   └── service2
    │       ├── application-dev.yml
    │       └── application-test.yml

```

## 설정 예시
### (docker-compose.yml)


```yaml
version: "3.8" # 버전 지정

services: # 컨테이너 설정
  config-server: # 컨테이너 이름
    container_name: config-server # 컨테이너 이름
    build:
      context: . # Dockerfile이 있는 경로
      dockerfile: Dockerfile # Dockerfile 이름
    ports:
      - "9000:9000/tcp"
      - "12342:12342/tcp"
    environment:
      BASIC_PASSWORD: password
      BASIC_USERNAME: user 
      GIT_REPO_SSH_URL: git@github.com:Onji-K/tech-practice-demos.git
      GIT_SEARCH_PATH: spring-cloud-config-demo/configTarget/{application}
      GIT_DEFAULT_BRANCH: spring-cloud-config-demo
      ENCRYPT_KEY: 1234567890123456
      GIT_SSH_PRIVATE_KEY: |
        -----BEGIN RSA PRIVATE KEY-----
        ...
        -----END RSA PRIVATE KEY-----
```
### 실행
GET http://localhost:9000/{application}/{profile}/{branch}

예시 : GET http://localhost:9000/service2/dev/spring-cloud-config-demo

POST http://localhost:9000/decrypt

바디에 암호화된 문자열을 넣으면 복호화된 문자열을 반환합니다.

POST http://localhost:9000/encrypt

바디에 복호화된 문자열을 넣으면 암호화된 문자열을 반환합니다.

### 참고
/actuator 을 제외한 api 호출은 Basic 인증이 필요합니다.
