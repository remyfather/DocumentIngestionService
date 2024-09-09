package com.rag.documentingestionservice.service.embedding;

import com.rag.documentingestionservice.dto.embedding.UpstageEmbeddingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    @Value("${upstage.api.key}")
    private String upstageApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 여러 텍스트의 임베딩을 얻는 메서드
    public List<double[]> getEmbeddings(List<String> texts) {
        return texts.stream()
                .map(this::getEmbedding)
                .collect(Collectors.toList());
    }

    // 개별 텍스트의 임베딩을 얻는 메서드
    public double[] getEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + upstageApiKey);
        headers.set("Content-Type", "application/json");

        String requestBody = String.format("{\"model\":\"solar-embedding-1-large-query\",\"input\":\"%s\"}", text);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<UpstageEmbeddingResponse> response = restTemplate.exchange(
                    "https://api.upstage.ai/v1/solar/embeddings",
                    HttpMethod.POST,
                    entity,
                    UpstageEmbeddingResponse.class
            );

            logger.info("임베딩 응답 확인 -----> {}", response.getBody());

            // 응답이 성공적이고 임베딩 데이터가 있을 때
            if (response.getBody() != null && !response.getBody().getData().isEmpty()) {
                return response.getBody().getData().get(0).getEmbedding();
            } else {
                logger.warn("임베딩 데이터를 찾을 수 없습니다. 응답: {}", response.getBody());
                return new double[0]; // 빈 배열 반환
            }

        } catch (Exception e) {
            logger.error("임베딩 요청 중 오류 발생: ", e);
            return new double[0]; // 오류 시 빈 배열 반환
        }
    }
}
