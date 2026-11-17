package com.back.global.rsData;

import com.back.domain.post.post.dto.PostDto;

// 조회가 아닌 요청(작성/삭제 등)의 공통 응답 모양 (RsData = Result Data, 강의 13~15강)
// resultCode: "HTTP응답코드-세부코드" 형식. 예) "200-1" = 성공(200) 중 1번 상황
// msg: 사람이 읽을 결과 메시지
// data: 함께 돌려줄 데이터
// 지금은 data 타입이 PostDto로 고정되어 있다 → 댓글 데이터는 담을 수 없음 (나중에 제네릭으로 바꾼다)
public record RsData(
        String resultCode,
        String msg,
        PostDto data
) {
}
