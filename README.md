# post-api-plus

글·댓글 REST API를 만드는 부트캠프 과제입니다. 장희성 강사의 "스프링부트 REST API" 강의(14172) 방식을 따릅니다.

- **목표**: 완성된 소스코드를 내가 이해하는 것
- **진행 방식**: 새 개념 하나 = 커밋 하나. 커밋마다 같은 이름의 태그를 붙여, 태그 링크로 그 시점의 코드를 볼 수 있습니다.

## 과제 요구사항

- [ ] 프로젝트 새로 생성
- [ ] 글 3건, 댓글 5건 자동생성
- [ ] 글 작성 기능/테스트케이스
- [ ] 글 삭제 기능/테스트케이스
- [ ] 댓글 작성 기능/테스트케이스
- [ ] 댓글 삭제 기능/테스트케이스
- [ ] 리포지터리 제출

## 참고 자료

| 자료 | 링크 |
|---|---|
| 14172 스프링부트 REST API | [강의](https://www.slog.gg/p/14172/raw) / [GitHub](https://github.com/jhs512/p-14172-2) |

## 기술 스택

- Java 25, Spring Boot 4.1.2, Gradle (Kotlin DSL)
- Spring Data JPA, H2 (개발: 파일 DB, 테스트: 메모리 DB)

## 작업내역

### 000-01 프로젝트 준비, 완료, 저장소와 README 구성
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/000-01)
- 작업: git 저장소 초기화(main 브랜치), .gitignore, .gitattributes, README 작성
- 이유: 모든 작업을 작은 커밋과 태그로 기록해서, 나중에 커밋 하나씩 따라가며 이해하기 위함
- 결과: 첫 커밋 생성, GitHub 공개 저장소에 업로드

### 001-01 프로젝트 생성, 완료, Spring Boot 4 프로젝트 생성과 개발/테스트 DB 분리
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/001-01)
- 강의: 14172 1강, 3강
- 작업: Spring Boot 4.1.2 / Java 25 / Gradle Kotlin DSL 프로젝트 생성 (webmvc, Data JPA, H2, H2 콘솔, Lombok, DevTools), 설정 파일을 yaml로 작성하고 dev(파일 DB) / test(메모리 DB) 프로파일 분리
- 이유: 개발 데이터는 서버를 껐다 켜도 남아야 하고, 테스트는 매번 깨끗한 DB에서 개발 데이터를 건드리지 않고 돌아야 하기 때문
- 결과: contextLoads 테스트 통과(메모리 DB), 서버 실행 시 프로젝트 폴더에 db_dev.mv.db 생성 확인, localhost:8080 접속 시 404(아직 API 없음)

### 002-01 엔티티, 시작, BaseEntity와 날짜 자동 기록
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/002-01)
- 강의: 14172 3강
- 작업: 모든 엔티티의 공통 필드(id, createDate, modifyDate)를 BaseEntity에 모으고, @EnableJpaAuditing으로 날짜가 자동 기록되게 함. id 기반 equals/hashCode 정의
- 이유: 글과 댓글 모두 같은 공통 필드가 필요하므로 한 곳에서 관리하고, 날짜를 직접 넣는 실수를 없애기 위함
- 결과: 컴파일과 contextLoads 테스트 통과 (아직 엔티티가 없어 테이블은 생기지 않음)
