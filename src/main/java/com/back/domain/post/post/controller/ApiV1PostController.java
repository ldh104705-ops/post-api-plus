package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
            // @NotBlank: null, "", "   " 모두 안 됨
            // @Size: 글자 수 범위
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    // 글 작성: POST /api/v1/posts
    // 조회는 GET, 생성은 POST (같은 주소라도 HTTP 메서드로 기능을 구분)
    // @RequestBody: 요청 본문의 JSON을 PostWriteReqBody로 바꿔서 받는다
    // @Valid: 바꾼 뒤 위의 검증 어노테이션을 검사한다. 실패하면 메서드가 실행되지 않고 400 응답
    // @Transactional: DB를 바꾸는 작업이므로 액션 메서드에 트랜잭션 (강의 11강)
    @PostMapping
    @Transactional
    public RsData write(@RequestBody @Valid PostWriteReqBody reqBody) {
        Post post = postService.write(reqBody.title(), reqBody.content());

        // 결과 코드, 메시지, 작성된 글을 함께 응답
        // 새로 만든 경우에 가장 알맞은 HTTP 응답코드는 201(Created)이므로 201-1
        return new RsData(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );
    }
}
