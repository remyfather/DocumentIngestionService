package com.rag.documentingestionservice.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * {
 *     "answer": "검색된 문서에서 생성된 답변",
 *     "documents": [
 *         {
 *             "id": "문서 ID",
 *             "content": "문서의 내용"
 *         }
 *     ]
 * }
 */

@Data
@AllArgsConstructor
public class SearchResponseDto {
    private String answer;
    private List<ChunkedDataDto> documents;

    // Nested class to represent the documents in the response
    @Data
    @AllArgsConstructor
    public static class ChunkedDataDto {
        private String id;
        private String content;  // 하나의 String으로 합쳐진 content
    }
}
