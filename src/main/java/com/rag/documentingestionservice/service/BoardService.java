package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.entity.UrlOptions;

import java.util.List;

public interface BoardService {
    /**
     * Description : POST /api/rag/url-options 는 사용자에 입력한 데이터를 saveUrlOptions를 통해 데이터베이스에 저장함
     */
    UrlOptions saveUrlOptions(UrlOptionsDto urlOptionsDto);
    List<UrlOptions> getAllUrlOptions();
    UrlOptions getUrlOptionsById(Long id);  // 추가된 메서드
}
