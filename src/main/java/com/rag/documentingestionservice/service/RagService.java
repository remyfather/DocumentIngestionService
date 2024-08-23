package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.UrlExtractResponseDto;
import com.rag.documentingestionservice.dto.UrlExtractRequestDto;

public interface RagService {
    //RagService 인터페이스와 구현체 (RagServiceImpl): 실제 데이터 추출 및 청킹 처리를 담당합니다. Jsoup을 사용하여 URL에서 데이터를 추출하고, 지정된 크기와 오버랩을 고려하여 데이터를 분할합니다.
    UrlExtractResponseDto extractAndChunkData(UrlExtractRequestDto requestDto);
}
