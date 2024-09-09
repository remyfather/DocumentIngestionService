package com.rag.documentingestionservice.dto.embedding;

import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoredChunk {
    private SearchResponseDto2.ChunkedDataDto document; // 청크 데이터
    private double score;  // 유사도 점수
}
