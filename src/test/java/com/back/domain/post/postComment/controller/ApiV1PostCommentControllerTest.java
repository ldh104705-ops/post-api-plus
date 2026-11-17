package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.entity.PostComment;
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

// 댓글 API 테스트. 글 테스트와 같은 구조 (요청 → 검증)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1PostCommentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;
    // 댓글 리포지터리가 없어서 댓글 수를 셀 때 사용
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("댓글 작성")
    void t1() throws Exception {
        int postId = 1;

        // 요청: 1번 글에 댓글 작성
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts/%d/comments".formatted(postId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "내용"
                                        }
                                        """)
                )
                .andDo(print());

        // 방금 작성된 댓글 = 1번 글의 댓글 목록의 마지막
        Post post = postService.findById(postId).get();
        PostComment postComment = post.getComments().getLast();

        // 검증
        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 작성되었습니다.".formatted(postComment.getId())))
                // 저장된 댓글이라면 DB가 매긴 번호(1 이상)가 있어야 한다
                .andExpect(jsonPath("$.data.id").value(Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data.id").value(postComment.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(postComment.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(postComment.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.content").value("내용"));
    }

    @Test
    @DisplayName("댓글 삭제")
    void t2() throws Exception {
        int postId = 1;
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/%d/comments/%d".formatted(postId, id))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 삭제되었습니다.".formatted(id)));

        // 글의 댓글 목록에서 빠졌는지
        Post post = postService.findById(postId).get();
        assertThat(post.findCommentById(id)).isEmpty();

        // DB에서도 지워졌는지: JPQL을 실행하면 먼저 미뤄 둔 SQL(orphanRemoval의 DELETE)이 실행된 뒤 센다
        // 다른 댓글은 그대로이므로 5건 → 4건
        long commentCount = em
                .createQuery("select count(pc) from PostComment pc", Long.class)
                .getSingleResult();
        assertThat(commentCount).isEqualTo(4);
    }

    @Test
    @DisplayName("댓글 삭제, 없는 댓글이면 404")
    void t3() throws Exception {
        int postId = 1;
        int id = 4; // 4번 댓글은 있지만 1번 글이 아니라 2번 글의 댓글

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/%d/comments/%d".formatted(postId, id))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));

        // 아무것도 지워지지 않았다 (샘플 5건 그대로)
        long commentCount = em
                .createQuery("select count(pc) from PostComment pc", Long.class)
                .getSingleResult();
        assertThat(commentCount).isEqualTo(5);
    }
}
