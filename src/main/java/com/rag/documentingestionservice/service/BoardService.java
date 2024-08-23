package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.entity.UrlOptions;

import java.util.List;

public interface BoardService {
    UrlOptions saveUrlOptions(UrlOptionsDto urlOptionsDto);
    List<UrlOptions> getAllUrlOptions();
    UrlOptions getUrlOptionsById(Long id);  // 추가된 메서드
}
