package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController = @Controller + @ResponseBody → 메서드가 돌려준 객체를 JSON으로 응답
// /api/v1: API 주소에 버전을 붙여, 나중에 v2를 만들어도 기존 클라이언트가 깨지지 않게 한다 (강의 6강)
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    // 요청 본문(JSON)을 받을 객체. 이 컨트롤러에서만 쓰므로 안쪽에 둔다 (강의 22·24강)
    // { "title": "...", "content": "..." } → PostWriteReqBody
    record PostWriteReqBody(
            String title,
            String content
    ) {
    }

    // 글 작성: POST /api/v1/posts
    // 조회는 GET, 생성은 POST (같은 주소라도 HTTP 메서드로 기능을 구분)
    // @RequestBody: 요청 본문의 JSON을 PostWriteReqBody로 바꿔서 받는다
    // @Transactional: DB를 바꾸는 작업이므로 액션 메서드에 트랜잭션 (강의 11강)
    @PostMapping
    @Transactional
    public PostDto write(@RequestBody PostWriteReqBody reqBody) {
        Post post = postService.write(reqBody.title(), reqBody.content());

        return new PostDto(post);
    }
}
