package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
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
}
