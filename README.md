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

### 002-02 엔티티, 완료, Post·PostComment 연관관계와 리포지터리
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/002-02)
- 강의: 14172 3강, 12강, 16강
- 작업: Post(글), PostComment(댓글) 엔티티와 1:N 연관관계, cascade(PERSIST, REMOVE)·orphanRemoval, 글 안의 댓글 추가/찾기/삭제 메서드, PostRepository
- 이유: 댓글은 글에 딸린 데이터이므로 글을 통해서만 추가·삭제하고(댓글 리포지터리 없음), 글이 지워지면 댓글도 함께 지워지게 하기 위함
- 결과: 서버 실행 시 SQL 로그에서 post, post_comment(post_id 포함) 테이블 생성 확인

### 003-01 샘플 데이터, 시작, 앱 시작 시 글 3건·댓글 5건 자동생성
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/003-01)
- 강의: 14172 3강
- 작업: PostService(count, write), BaseInitData(ApplicationRunner + 자기 자신 프록시 `self`로 @Transactional 적용)로 앱 시작 시 샘플 데이터 생성
- 이유: 과제 요구사항(글 3건, 댓글 5건 자동생성). 데이터가 이미 있으면 건너뛰어 재시작해도 중복 생성되지 않게 함
- 결과: 개발 DB에 글 1~3, 댓글 1~5(1번 글에 1·2·3, 2번 글에 4·5) 생성 확인. 재시작 후에도 3건/5건 유지

### 003-02 샘플 데이터, 완료, 샘플 데이터 생성 테스트
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/003-02)
- 강의: 14172 3강, 33강(테스트 프로파일·롤백)
- 작업: BaseInitDataTest로 테스트 DB(메모리)에 글 3건, 댓글 5건(1번 글 3건, 2번 글 2건, 3번 글 0건)이 만들어지는지 검사
- 이유: "글 3건, 댓글 5건 자동생성" 요구사항을 눈으로 확인하는 데서 끝내지 않고 테스트로 증명하기 위함. 이후 모든 테스트가 이 데이터를 전제로 함
- 결과: BaseInitDataTest 통과

### 004-01 글 작성, 시작, POST /api/v1/posts와 PostDto
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-01)
- 강의: 14172 4강(@RestController), 6강(API 버저닝), 8~9강(PostDto record), 22·24강(@RequestBody, PostWriteReqBody)
- 작업: ApiV1PostController에 글 작성 API 추가. 요청 JSON은 PostWriteReqBody로 받고, 응답은 엔티티 대신 PostDto(record)로 반환
- 이유: 엔티티를 그대로 JSON으로 내보내면 글↔댓글 순환참조와 API 모양 변경 문제가 생기므로 DTO로 분리
- 결과: `{"title":"제목 4","content":"내용 4"}` 요청 시 4번 글이 만들어지고 JSON 응답 확인. 단, 빈 제목/내용도 그대로 저장되는 문제 발견 → 다음 커밋에서 검증 추가

### 004-02 글 작성, @Valid로 입력값 검증
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-02)
- 강의: 14172 3강(validation 의존성), 22강(@Valid, @NotBlank, @Size)
- 작업: validation 의존성 추가, PostWriteReqBody에 @NotBlank·@Size(제목 2~100자, 내용 2~5000자), 컨트롤러 파라미터에 @Valid
- 이유: 빈 제목/내용의 글이 저장되던 문제를 막기 위함
- 결과: 정상 요청은 200, 빈 제목/내용 요청은 400(스프링 기본 에러 응답)이고 DB에 저장되지 않음(글 4건 유지)

### 004-03 글 작성, RsData 도입 (PostDto 전용)
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-03)
- 강의: 14172 13~15강
- 작업: 공통 응답 record RsData(resultCode, msg, data) 도입. 글 작성 응답을 `"200-1"`, `"N번 글이 작성되었습니다."`, 작성된 글로 감쌈. data 타입은 아직 PostDto로 고정
- 이유: 작성·삭제처럼 조회가 아닌 요청은 "무슨 일이 일어났는지"(결과 코드와 메시지)를 함께 알려 주는 게 좋기 때문
- 결과: 응답이 `{"resultCode":"200-1","msg":"4번 글이 작성되었습니다.","data":{...}}` 모양으로 바뀜

### 004-04 글 작성, 실험(실패), resultCode를 201-1로 바꿔도 HTTP 응답코드는 200
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-04)
- 강의: 14172 26~27강
- 작업: 글 작성의 resultCode를 `"201-1"`(201 Created)로 변경
- 이유: 새로 만든 경우에 가장 알맞은 HTTP 응답코드는 201이므로, resultCode 앞자리도 201로 맞춤
- 결과: **실패**. JSON의 resultCode는 201-1이지만 실제 HTTP 응답코드는 여전히 200. resultCode는 그냥 JSON 안의 글자일 뿐, HTTP 응답코드를 바꾸지 않음 → 다음 커밋에서 해결

### 004-05 글 작성, ResponseAspect로 resultCode 앞자리를 HTTP 응답코드에 반영
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-05)
- 강의: 14172 27·28·31·37강
- 작업: RsData에 statusCode(resultCode 앞자리, JSON에서는 숨김) 추가, AOP로 RsData를 반환하는 컨트롤러 액션이 끝나면 HTTP 응답코드를 statusCode로 설정하는 ResponseAspect 추가
- 이유: 액션마다 ResponseEntity로 상태코드를 따로 지정하는 대신, resultCode 하나만 정하면 HTTP 응답코드가 자동으로 맞춰지게 하기 위함
- 결과: 글 작성 응답이 HTTP 201로 바뀜. JSON에는 statusCode가 나오지 않음

### 004-06 글 작성, 글 작성 테스트 (MockMvc)
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-06)
- 강의: 14172 33·35·38·39강
- 작업: ApiV1PostControllerTest에 글 작성 테스트(t1). MockMvc로 POST 요청 → 처리한 컨트롤러·메서드, HTTP 201, resultCode, msg, data의 모든 필드를 DB의 최신 글과 비교. 이를 위해 PostRepository.findFirstByOrderByIdDesc, PostService.findLatest 추가
- 이유: 과제 요구사항(글 작성 테스트케이스). 테스트마다 롤백되므로 항상 샘플 데이터 상태에서 검사
- 결과: 글 작성 테스트 통과

### 004-07 글 작성, 완료, 검증 실패를 400 RsData로 응답 (GlobalExceptionHandler)
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/004-07)
- 강의: 14172 48·50·53강
- 작업: @ControllerAdvice 전역 예외 처리기에서 MethodArgumentNotValidException을 `400-1` RsData(실패 항목을 "필드-검증종류-메시지"로 정렬)로 바꿔 HTTP 400 응답. 제목 없이 글 작성 시 400 테스트(t2) 추가
- 이유: 검증 실패 시 스프링 기본 에러 JSON 대신, 성공 응답과 같은 RsData 모양으로 무엇이 틀렸는지 알려 주기 위함
- 결과: `{"resultCode":"400-1","msg":"title-NotBlank-공백일 수 없습니다\ntitle-Size-크기가 2에서 100 사이여야 합니다","data":null}` (HTTP 400), 테스트 4개 통과

### 005-01 글 삭제, 시작, GET /api/v1/posts/{id}/delete
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/005-01)
- 강의: 14172 16강
- 작업: PostService.findById·delete 추가, 글 삭제 API를 일단 GET 방식(`/{id}/delete`)으로 구현. 응답은 RsData(`200-1`, data 없음)
- 이유: 브라우저 주소창에서 바로 호출해 동작을 확인하기 위해 강의처럼 GET으로 먼저 만듦. 원래 삭제는 GET이면 안 되므로 다음 커밋에서 DELETE로 바꿈
- 결과: 1번 글 삭제 → `{"resultCode":"200-1","msg":"1번 글이 삭제되었습니다.","data":null}`. DB에서 글 1번과 그 댓글 1·2·3번이 함께 삭제됨(cascade REMOVE), 글 2·3번과 댓글 4·5번은 남음

### 005-02 글 삭제, GET 대신 DELETE 메서드로 변경
- 날짜: 2026-11-17
- [링크](https://github.com/ldh104705-ops/post-api-plus/tree/005-02)
- 강의: 14172 21강
- 작업: `GET /api/v1/posts/{id}/delete` → `DELETE /api/v1/posts/{id}` (@DeleteMapping)
- 이유: 주소는 대상(몇 번 글)만, 동작(삭제)은 HTTP 메서드로 나타내는 것이 REST 규칙. GET은 조회용이라 미리 불러오기나 검색엔진 방문만으로 삭제될 위험이 있음
- 결과: 예전 주소(GET .../delete)는 404, DELETE /api/v1/posts/1 → `200-1`, DB에서 1번 글 삭제 확인
