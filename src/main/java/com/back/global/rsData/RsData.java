package com.back.global.rsData;

import com.back.domain.post.post.dto.PostDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

// 조회가 아닌 요청(작성/삭제 등)의 공통 응답 모양 (RsData = Result Data, 강의 13~15강)
// resultCode: "HTTP응답코드-세부코드" 형식. 예) "201-1" = 생성 성공(201) 중 1번 상황
// statusCode: resultCode의 앞자리 숫자(201). ResponseAspect가 이 값으로 HTTP 응답코드를 정한다
//             JSON에는 resultCode와 같은 정보라 중복이므로 @JsonIgnore로 숨긴다 (강의 28·31강)
// msg: 사람이 읽을 결과 메시지
// data: 함께 돌려줄 데이터
// 지금은 data 타입이 PostDto로 고정되어 있다 → 댓글 데이터는 담을 수 없음 (나중에 제네릭으로 바꾼다)
public record RsData(
        String resultCode,
        @JsonIgnore int statusCode,
        String msg,
        PostDto data
) {
    // statusCode는 직접 넣지 않고 resultCode에서 뽑아낸다
    // "201-1".split("-", 2) → ["201", "1"] → 201
    public RsData(String resultCode, String msg, PostDto data) {
        this(resultCode, Integer.parseInt(resultCode.split("-", 2)[0]), msg, data);
    }
}
