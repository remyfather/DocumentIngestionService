package com.rag.documentingestionservice.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.rag.documentingestionservice.dto.ChunkedDataDto;
import com.rag.documentingestionservice.dto.embedding.ScoredChunk;
import com.rag.documentingestionservice.dto.layoutanalysis.ChunkMetadata;
import com.rag.documentingestionservice.dto.search.SearchResponseDto;
import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import com.rag.documentingestionservice.service.embedding.EmbeddingService;
import com.rag.documentingestionservice.service.embedding.SimilarityService;
import com.rag.documentingestionservice.service.embedding.SimilarityService2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;


import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChunkedDataElasticsearchRepository {

    private static final Logger logger = LoggerFactory.getLogger(ChunkedDataElasticsearchRepository.class);


    private final ElasticsearchClient elasticsearchClient;
    private final EmbeddingService embeddingService;
    private final SimilarityService similarityService;
    private final SimilarityService2 similarityService2;

    /**
     * Elasticsearch에 ChunkedDataDto를 저장합니다.
     *
     * @param chunkedData 저장할 청크 데이터
     */

    public void saveChunkedData(ChunkedDataDto chunkedData) {
        try {
            elasticsearchClient.index(i -> i
                    .index("chunked-data")  // 인덱스 이름
                    .id(chunkedData.getId())  // 문서 ID
                    .document(chunkedData)  // 문서 데이터
            );
        } catch (IOException e) {
            System.err.println("Elasticsearch에 데이터를 저장하는 동안 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에 데이터를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

    /**
     * 주어진 쿼리를 기반으로 Elasticsearch에서 데이터를 검색합니다.
     *
     * @param query 검색할 쿼리
     * @return 검색 결과를 포함한 SearchResponseDto
     */
    public SearchResponseDto searchByQuery(String query) {
        try {
            SearchResponse<ChunkedDataDto> response = elasticsearchClient.search(s -> s
                            .index("chunked-data")
                            .query(q -> q
                                    .match(m -> m
                                            .field("content")
                                            .query(query)
                                    )
                            )
                            .size(10),
                    ChunkedDataDto.class
            );

            logger.info("검색된 결과 -----> {}", response);

            // 검색된 문서를 기반으로 응답 생성
            List<SearchResponseDto.ChunkedDataDto> documents = response.hits().hits().stream()
                    .map(hit -> new SearchResponseDto.ChunkedDataDto(
                            hit.source().getId(),
                            String.join(" ", hit.source().getContent())  // List<String>을 하나의 String으로 결합
                    ))
                    .collect(Collectors.toList());

            // 검색된 문서들로부터 답변 생성
            String answer = generateAnswerFromDocuments2(documents);

            return new SearchResponseDto(answer, documents);

        } catch (IOException e) {
            System.err.println("Elasticsearch에서 데이터를 검색하는 동안 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에서 데이터를 검색하는 동안 오류가 발생했습니다.", e);
        }
    }

    /**
     * 검색된 문서들로부터 답변을 생성합니다.
     *
     * @param documents 검색된 청크 데이터 목록
     * @return 생성된 답변 문자열
     */
    // 문서들로부터 답변 생성
    private String generateAnswerFromDocuments(List<SearchResponseDto.ChunkedDataDto> documents) {
        if (documents.isEmpty()) {
            return "No relevant documents found.";
        } else {
            return documents.get(0).getContent();
        }
    }

    // 문서들로부터 답변 생성
    private String generateAnswerFromDocuments2(List<SearchResponseDto.ChunkedDataDto> documents) {
        if (documents.isEmpty()) {
            return "No relevant documents found.";
        } else {
            // 여러 문서의 내용을 결합하여 답변 생성 (예시: 상위 3개의 문서 내용 합치기)
            return documents.stream()
                    .limit(10) // 상위 3개의 문서만 사용 (필요에 따라 조정 가능)
                    .map(SearchResponseDto.ChunkedDataDto::getContent) // 각 문서의 내용을 가져옴
                    .collect(Collectors.joining(" ")); // 문서 내용들을 합쳐서 하나의 답변으로 생성
        }
    }


    public List<ChunkedDataDto> searchByQuery2(String query) {
        try {
            SearchResponse<ChunkedDataDto> response = elasticsearchClient.search(s -> s
                            .index("chunked-data")
                            .query(q -> q
                                    .match(m -> m
                                            .field("content")
                                            .query(query)
                                    )
                            )
                            .size(10),  // 상위 10개의 문서를 검색하도록 설정
                    ChunkedDataDto.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 중 오류 발생: " + e.getMessage(), e);
        }
    }


    /**
     * Upstage Embedding API를 호출하여 임베딩을 생성하고 유사도를 계산하여 상위 K개의 문서를 추출합니다.
     *
     * @param query 검색할 쿼리
     * @return 검색된 문서와 상위 K개의 문서를 포함한 응답 객체
     */
    public SearchResponseDto2 searchAndEmbed(String query) {
        try {
            // Step 1: Elasticsearch에서 상위 10개 문서를 검색합니다.
            SearchResponse<ChunkedDataDto> response = elasticsearchClient.search(s -> s
                            .index("chunked-data")
                            .query(q -> q.match(m -> m.field("content").query(query)))
                            .size(10),
                    ChunkedDataDto.class
            );

            // Step 2: 검색된 문서들을 변환합니다.
            List<SearchResponseDto2.ChunkedDataDto> documents = response.hits().hits().stream()
                    .map(hit -> new SearchResponseDto2.ChunkedDataDto(
                            hit.source().getId(),
                            String.join(" ", hit.source().getContent())
                    ))
                    .collect(Collectors.toList());

            // Step 3: Upstage Embedding API 호출하여 임베딩을 생성합니다.
            List<double[]> embeddings = documents.stream()
                    .map(document -> embeddingService.getEmbedding(document.getContent()))  // 각 문서의 content를 임베딩
                    .filter(Objects::nonNull)  // null 제거 (임베딩이 실패한 경우)
                    .collect(Collectors.toList());

            // Step 4: 임베딩된 벡터를 사용하여 유사도를 계산하고 상위 5개 문서를 추출합니다.
            List<SearchResponseDto2.ChunkedDataDto> topKDocuments = similarityService.calculateTopKDocuments(embeddings, documents, 5);

            // Step 5: 검색된 문서 목록을 포함한 응답 객체를 반환합니다.
            return new SearchResponseDto2(topKDocuments);

        } catch (IOException e) {
            System.err.println("Elasticsearch에서 데이터를 검색하는 동안 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에서 데이터를 검색하는 동안 오류가 발생했습니다.", e);
        }
    }


    public SearchResponseDto2 searchAndEmbed2(String query) {
        try {
            // Step 1: Elasticsearch에서 상위 10개 문서를 검색합니다.
            SearchResponse<ChunkedDataDto> response = elasticsearchClient.search(s -> s
                            .index("chunked-data")
                            .query(q -> q.match(m -> m.field("content").query(query)))
                            .size(10),
                    ChunkedDataDto.class
            );

            // 검색된 문서를 변환합니다.
            List<SearchResponseDto2.ChunkedDataDto> documents = response.hits().hits().stream()
                    .map(hit -> new SearchResponseDto2.ChunkedDataDto(
                            hit.source().getId(),
                            String.join(" ", hit.source().getContent())
                    ))
                    .collect(Collectors.toList());

            // Step 2: 입력된 쿼리를 임베딩합니다.
            double[] queryEmbedding = embeddingService.getEmbedding(query);
            logger.info("임베딩된 쿼리 값 확인 : {}", queryEmbedding);

            // Step 3: 검색된 청크 문서들을 임베딩합니다.
            List<double[]> chunkEmbeddings = documents.stream()
                    .map(document -> embeddingService.getEmbedding(document.getContent()))  // 각 문서의 content를 임베딩
                    .filter(Objects::nonNull)  // null 제거 (임베딩이 실패한 경우)
                    .collect(Collectors.toList());

            // Step 4: 쿼리와 청크 간의 유사도를 계산합니다.
            List<ScoredChunk> scoredChunks = similarityService2.calculateSimilarity(queryEmbedding, chunkEmbeddings, documents);

            // Step 5: 유사도에 따라 상위 K개의 문서를 선택합니다.
            List<SearchResponseDto2.ChunkedDataDto> topKDocuments = scoredChunks.stream()
                    .sorted((a, b) -> Double.compare(b.getScore(), a.getScore())) // 유사도 점수로 정렬 (내림차순)
                    .limit(5)
                    .map(ScoredChunk::getDocument)  // Top 5 청크를 추출
                    .collect(Collectors.toList());

            // Step 6: 상위 K개의 청크 문서 목록을 포함한 응답 객체를 반환합니다.
            return new SearchResponseDto2(topKDocuments);

        } catch (IOException e) {
            System.err.println("Elasticsearch에서 데이터를 검색하는 동안 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에서 데이터를 검색하는 동안 오류가 발생했습니다.", e);
        }
    }



    public void saveChunkedDataWithMetadata(String indexName, ChunkMetadata chunkMetadata) {
        try {
            elasticsearchClient.index(i -> i
                    .index(indexName)
                    .id(chunkMetadata.getDocumentId()) // 문서 ID를 사용하여 ID로 저장
                    .document(chunkMetadata) // 메타 정보와 함께 청크를 저장
            );
        } catch (IOException e) {
            System.err.println("Elasticsearch에 데이터를 저장하는 동안 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에 데이터를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

    public void saveChunkedData2(ChunkedDataDto chunkedData) {
        try {
            // Elasticsearch 인덱스 설정
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("chunked-data")  // 사용할 인덱스 이름
                    .id(chunkedData.getId())  // ID 설정
                    .document(chunkedData)  // 저장할 문서
            );

            logger.info("청크 데이터 저장 성공, ID: {}", response.id());
        } catch (IOException e) {
            logger.error("Elasticsearch에 청크 데이터를 저장하는 동안 오류가 발생했습니다: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에 청크 데이터를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

}
