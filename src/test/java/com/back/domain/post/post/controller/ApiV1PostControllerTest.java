package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import jakarta.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 컨트롤러 테스트: 실제 서버를 띄우지 않고, MockMvc로 가짜 HTTP 요청을 보내 응답을 검사한다 (강의 33강)
@ActiveProfiles("test")  // 메모리 DB 사용
@SpringBootTest          // 앱 전체(빈, DB, 샘플 데이터)를 띄운다
@AutoConfigureMockMvc    // MockMvc 준비
@Transactional           // 테스트가 끝나면 롤백 → 다음 테스트는 항상 샘플 데이터(글 3, 댓글 5)에서 시작
public class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;
    // 엔티티를 직접 조회할 때 쓰는 JPA 핵심 객체 (댓글 리포지터리가 없어서 댓글 수를 셀 때 사용)
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("글 작성")
    void t1() throws Exception {
        // 1단계: 요청 (강의 35강: 요청 → 검증 2단계 구조)
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        """)
                )
                .andDo(print()); // 요청/응답 내용을 콘솔에 출력

        // 방금 작성된 글 = 가장 최근 글
        Post post = postService.findLatest().get();

        // 2단계: 검증
        resultActions
                // 어느 컨트롤러의 어느 메서드가 처리했는지 (강의 38강)
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                // HTTP 201 (ResponseAspect)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(post.getId())))
                // 응답 data의 모든 필드를 DB의 글과 비교 (강의 39강)
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                // 날짜는 JSON과 자바의 소수점 자릿수가 다를 수 있어 앞 20글자만 비교
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.content").value("내용"));
    }

    @Test
    @DisplayName("글 작성, 제목 없이 요청하면 400")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "",
                                            "content": "내용"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                // 빈 제목은 @NotBlank와 @Size(min = 2) 둘 다 실패한다
                // 메시지 문장은 PC 언어 설정에 따라 영어/한글이 달라서, "필드-검증종류" 부분만 확인
                .andExpect(jsonPath("$.msg").value(Matchers.containsString("title-NotBlank-")))
                .andExpect(jsonPath("$.msg").value(Matchers.containsString("title-Size-")));

        // 실패했으므로 글이 늘어나지 않아야 한다 (샘플 3건 그대로)
        assertThat(postService.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("글 삭제")
    void t3() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 삭제되었습니다.".formatted(id)));

        // 응답만 보지 않고 DB에서 실제로 지워졌는지도 확인 (강의 lab2 방식)
        assertThat(postService.findById(id)).isEmpty();

        // 1번 글의 댓글 3건도 cascade REMOVE로 함께 지워져서, 댓글은 5건 → 2건
        // JPQL: 테이블이 아니라 엔티티(PostComment) 기준으로 쓰는 쿼리
        long commentCount = em
                .createQuery("select count(pc) from PostComment pc", Long.class)
                .getSingleResult();
        assertThat(commentCount).isEqualTo(2);
    }

    @Test
    @DisplayName("글 삭제, 없는 글이면 404")
    void t4() throws Exception {
        int id = Integer.MAX_VALUE; // 샘플 데이터에 없는 번호

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));

        // 아무것도 지워지지 않았다 (샘플 3건 그대로)
        assertThat(postService.count()).isEqualTo(3);
    }
}
