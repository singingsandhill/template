# Spring Boot 기반 JWT 인증/인가 웹 에플리케이션 (BE)

- 바로인턴 java 직무 과제

<div align="center">
  <img src="https://img.shields.io/badge/java-17-blue" alt="Java 17">
  <img src="https://img.shields.io/badge/spring%20boot-3.4.5-brightgreen" alt="Spring Boot 3.4.5">
  <img src="https://img.shields.io/badge/jwt-0.11.5-orange" alt="JWT 0.11.5">
  <img src="https://img.shields.io/badge/swagger-2.8.5-green" alt="Swagger 2.8.5">
  <img src="https://img.shields.io/badge/lombok-latest-red" alt="Lombok">
</div>

<div align="center">
  <p>메모리 기반 JWT 인증 시스템 템플릿</p>
</div>

## 📋 목차

- [프로젝트 개요](##프로젝트-개요)
- [배포](##배포)
- [기술 스택](##기술-스택)
- [프로젝트 구조](##프로젝트-구조)
- [주요 기능](##주요-기능)
- [환경 설정](##환경-설정)
- [API 명세](##API-명세서)
- [테스트](##테스트)
- [보안 설정](##보안-설정)

<br>

## 🚀 프로젝트 개요

이 프로젝트는 Spring Boot 기반의 JWT 인증 시스템 템플릿입니다. 사용자 등록, 로그인, 권한 기반 접근 제어 등의 기능을 제공하며, 메모리 내에서 데이터를 처리합니다. JUnit 테스트와 Swagger를 통한 API 문서화도 포함하고 있습니다.

<br>

## 배포
AWS EC2 이용.<br>
ex) http://ec2-3-34-94-55.ap-northeast-2.compute.amazonaws.com:8080/api/user/register
<br>
*하기의 API 명세서를 참고하여 요청 실행.

## 💻 기술 스택

<div align="center">
  <table>
    <tr>
      <th>구분</th>
      <th>기술</th>
    </tr>
    <tr>
      <td>언어 및 프레임워크</td>
      <td>
        <img src="https://img.shields.io/badge/java-17-blue" alt="Java 17">
        <img src="https://img.shields.io/badge/spring%20boot-3.4.5-brightgreen" alt="Spring Boot 3.4.5">
      </td>
    </tr>
    <tr>
      <td>인증 및 보안</td>
      <td>
        <img src="https://img.shields.io/badge/spring%20security-6.0-brightgreen" alt="Spring Security 6.0">
        <img src="https://img.shields.io/badge/jwt-0.11.5-orange" alt="JWT 0.11.5">
      </td>
    </tr>
    <tr>
      <td>문서화</td>
      <td>
        <img src="https://img.shields.io/badge/swagger-2.8.5-green" alt="Swagger 2.8.5">
      </td>
    </tr>
    <tr>
      <td>테스트</td>
      <td>
        <img src="https://img.shields.io/badge/junit-5-red" alt="JUnit 5">
        <img src="https://img.shields.io/badge/mockito-5.2.0-yellow" alt="Mockito 5.2.0">
      </td>
    </tr>
    <tr>
      <td>기타 도구</td>
      <td>
        <img src="https://img.shields.io/badge/lombok-latest-red" alt="Lombok">
        <img src="https://img.shields.io/badge/gradle-8.0-blue" alt="Gradle 8.0">
      </td>
    </tr>
  </table>
</div>

<br>

## 📁 프로젝트 구조

프로젝트는 계층화된 아키텍처로 설계되어 있으며, 주요 패키지 구조는 다음과 같습니다:

```
org.personal.template/
├── application/         # 애플리케이션 서비스 계층
│   └── service/         # 비즈니스 로직 서비스
├── domain/              # 도메인 계층
│   ├── entity/          # 도메인 모델 (엔티티)
│   └── repository/      # 리포지토리 인터페이스 및 구현체
├── infrastructure/      # 인프라 계층
│   ├── config/          # 설정 클래스
│   ├── exception/       # 예외 처리 관련 클래스
│   ├── jwt/             # JWT 관련 유틸리티 및 필터
│   ├── response/        # API 응답 형식 클래스
│   └── security/        # Spring Security 관련 클래스
└── presentation/        # 프레젠테이션 계층
    ├── controller/      # API 컨트롤러
    └── dto/             # 데이터 전송 객체
```

<br>

## 🔍 주요 기능

<div align="center">
  <table>
    <tr>
      <th>기능</th>
      <th>설명</th>
    </tr>
    <tr>
      <td>사용자 관리</td>
      <td>
        - 일반 사용자 회원가입<br>
        - 관리자 회원가입 (관리자 코드 필요)<br>
        - 사용자 정보 조회 (관리자 권한 필요)
      </td>
    </tr>
    <tr>
      <td>인증</td>
      <td>
        - 이메일/비밀번호 기반 로그인<br>
        - JWT 토큰 발급 및 검증<br>
        - 토큰 기반 인증 필터
      </td>
    </tr>
    <tr>
      <td>권한 관리</td>
      <td>
        - 역할 기반 접근 제어 (RBAC)<br>
        - 관리자 전용 API 엔드포인트<br>
        - 인증된 사용자만 접근 가능한 리소스
      </td>
    </tr>
    <tr>
      <td>예외 처리</td>
      <td>
        - 전역 예외 핸들러<br>
        - 구조화된 에러 응답<br>
        - 필드 유효성 검사 오류 처리
      </td>
    </tr>
  </table>
</div>

<br>

## ⚙️ 환경 설정

### 1. 사전 요구사항

- JDK 17 이상
- Gradle 8.0 이상

### 2. 환경 변수 설정

프로젝트 루트 디렉토리에 `.env` 파일을 생성하고 다음 내용을 추가합니다:

```properties
SPRING_JWT_SECRET=your_jwt_secret_key_should_be_at_least_256_bits_long_for_security
```

### 3. 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 4. API 문서 접근

실행시 Swagger UI는 다음 URL로 접근할 수 있습니다:

```
http://localhost:8080/docs
```

<br>

## API 명세서
<a href="http://ec2-3-34-94-55.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.htm">EC2로 배포된 Swagge 문서</a> <br>
or <br>
http://ec2-3-34-94-55.ap-northeast-2.compute.amazonaws.com:8080/docs

## 🧪 테스트

JUnit과 Mockito를 사용하여 API 엔드포인트에 대한 단위 테스트 및 통합 테스트를 구현했습니다.

### 주요 테스트 클래스

1. **UserControllerAPITest**: 사용자 관련 API 테스트

   - 회원가입 성공/실패 테스트
   - 로그인 성공/실패 테스트

2. **AdminControllerAPITest**: 관리자 관련 API 테스트
   - 관리자 권한으로 사용자 목록 조회 테스트
   - 권한 없는 사용자의 접근 제한 테스트

## 🔒 보안 설정

### JWT 설정

- 토큰 만료 시간: 60분
- 토큰 암호화 알고리즘: HS256
- 시크릿 키: 환경 변수에서 로드 (`SPRING_JWT_SECRET`)

### Spring Security 설정

- CSRF 보호 비활성화 (REST API에 적합)
- 세션 관리: STATELESS (JWT 사용으로 세션 미사용)
- 권한 없는 접근 시 적절한 HTTP 상태 코드 반환:
  - 인증 없음: 401 Unauthorized
  - 권한 부족: 403 Forbidden
- 공개 엔드포인트:
  - Swagger UI: `/swagger-ui/**`, `/v3/api-docs/**`, `/docs/**`
  - 사용자 등록 및 로그인: `/api/user/login`, `/api/user/register`, `/api/user/register/admin`
- 보호된 엔드포인트:
  - 관리자 API: `/api/admin/**` (ROLE_ADMIN 필요)
  - 기타 모든 엔드포인트: 인증 필요

<br>
