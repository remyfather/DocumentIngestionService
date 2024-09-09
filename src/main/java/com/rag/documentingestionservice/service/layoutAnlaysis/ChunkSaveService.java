package com.rag.documentingestionservice.service.layoutAnlaysis;

import com.rag.documentingestionservice.dto.ChunkedDataDto;
import com.rag.documentingestionservice.dto.layoutanalysis.LayoutAnalysisResponse;
import com.rag.documentingestionservice.repository.ChunkedDataElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChunkSaveService {

    private final ChunkedDataElasticsearchRepository chunkedDataElasticsearchRepository;

    public void saveChunkedData(List<LayoutAnalysisResponse.Element> elements, String documentId, String sourceFileName) {
        for (LayoutAnalysisResponse.Element element : elements) {
            // 메타데이터 및 청크 생성
            ChunkedDataDto chunkedData = new ChunkedDataDto();
            chunkedData.setId(String.valueOf(element.getId())); // ID를 String으로 변환
            chunkedData.setContent(List.of(element.getText())); // content 설정
            chunkedData.setSourceUrl(sourceFileName); // sourceUrl 설정
            chunkedData.setChunkSize(element.getText().length()); // chunkSize 설정

            // Elasticsearch에 청크 데이터 저장
            chunkedDataElasticsearchRepository.saveChunkedData(chunkedData);
        }
    }
}
