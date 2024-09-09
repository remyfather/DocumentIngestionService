package com.rag.documentingestionservice.dto.embedding;

import lombok.Data;
import java.util.List;

@Data
public class UpstageEmbeddingResponse {
    private String object;
    private List<EmbeddingData> data;  // "data" 필드를 리스트로 선언

    @Data
    public static class EmbeddingData {
        private String object;
        private int index;
        private double[] embedding;  // 실제 임베딩 데이터가 포함된 배열
    }

    private String model;
    private Usage usage;

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int total_tokens;
    }
}
