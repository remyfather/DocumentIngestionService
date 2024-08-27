package com.rag.documentingestionservice.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.rag.documentingestionservice.dto.ChunkedDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChunkedDataElasticsearchRepository {

    private final ElasticsearchClient elasticsearchClient;

    public void saveChunkedData(ChunkedDataDto chunkedData) {
        try {
            elasticsearchClient.index(i -> i
                    .index("chunked-data")
                    .id(chunkedData.getId())
                    .document(chunkedData) // 전체 ChunkedDataDto 객체를 저장
            );
        } catch (IOException e) {
            System.err.println("Elasticsearch에 데이터를 저장하는 동안 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Elasticsearch에 데이터를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }
}
