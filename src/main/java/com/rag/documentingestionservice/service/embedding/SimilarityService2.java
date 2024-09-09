package com.rag.documentingestionservice.service.embedding;

import com.rag.documentingestionservice.dto.embedding.ScoredChunk;
import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimilarityService2 {
    private static final Logger logger = LoggerFactory.getLogger(SimilarityService.class);


    public List<ScoredChunk> calculateSimilarity(double[] queryEmbedding, List<double[]> chunkEmbeddings, List<SearchResponseDto2.ChunkedDataDto> documents) {
        List<ScoredChunk> scoredChunks = new ArrayList<>();

        for (int i = 0; i < chunkEmbeddings.size(); i++) {
            double similarityScore = calculateCosineSimilarity(queryEmbedding, chunkEmbeddings.get(i));
            scoredChunks.add(new ScoredChunk(documents.get(i), similarityScore));

            // 유사도 계산 결과를 로그로 출력
            logger.info("Chunk ID: {}, Similarity Score: {}", documents.get(i).getId(), similarityScore);
        }

        return scoredChunks;
    }

    private double calculateCosineSimilarity(double[] vec1, double[] vec2) {
        // 코사인 유사도 계산 로직
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }

        double cosineSimilarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));

        // 코사인 유사도 계산 중간 값들을 로그로 출력
        logger.info("Dot Product: {}, NormA: {}, NormB: {}, Cosine Similarity: {}", dotProduct, normA, normB, cosineSimilarity);

        return cosineSimilarity;
    }
}
