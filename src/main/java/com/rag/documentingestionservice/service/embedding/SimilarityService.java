package com.rag.documentingestionservice.service.embedding;

import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SimilarityService {

    public List<SearchResponseDto2.ChunkedDataDto> calculateTopKDocuments(List<double[]> embeddings, List<SearchResponseDto2.ChunkedDataDto> documents, int k) {
        // 유사도 계산 및 top-k 추출 로직을 구현합니다.
        List<ScoredDocument> scoredDocuments = new ArrayList<>();

        for (int i = 0; i < embeddings.size(); i++) {
            double score = calculateCosineSimilarity(embeddings.get(i), embeddings.get(0)); // 첫 번째 임베딩과 비교
            scoredDocuments.add(new ScoredDocument(documents.get(i), score));
        }

        // 유사도 점수를 기준으로 내림차순 정렬
        scoredDocuments.sort(Comparator.comparingDouble(ScoredDocument::getScore).reversed());

        // 상위 k개의 문서를 반환
        List<SearchResponseDto2.ChunkedDataDto> topKDocuments = new ArrayList<>();
        for (int i = 0; i < Math.min(k, scoredDocuments.size()); i++) {
            topKDocuments.add(scoredDocuments.get(i).getDocument());
        }

        return topKDocuments;
    }

    private double calculateCosineSimilarity(double[] vec1, double[] vec2) {
        // 코사인 유사도 계산 로직을 구현합니다.
        return 0;  // 임시 반환값
    }

    private static class ScoredDocument {
        private final SearchResponseDto2.ChunkedDataDto document;
        private final double score;

        public ScoredDocument(SearchResponseDto2.ChunkedDataDto document, double score) {
            this.document = document;
            this.score = score;
        }

        public SearchResponseDto2.ChunkedDataDto getDocument() {
            return document;
        }

        public double getScore() {
            return score;
        }
    }
}
