package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.controller.RagController;
import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.entity.UrlOptions;
import com.rag.documentingestionservice.repository.UrlOptionsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the BoardService interface.
 * Handles the saving and retrieval of URL options.
 */
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UrlOptionsRepository urlOptionsRepository;
    private static final Logger logger = LoggerFactory.getLogger(RagController.class);

    /**
     * Description : POST /api/rag/url-options 는 사용자에 입력한 데이터를 saveUrlOptions를 통해 데이터베이스에 저장함
     */
    @Override
    public UrlOptions saveUrlOptions(UrlOptionsDto urlOptionsDto) {
        try {
            UrlOptions urlOptions = new UrlOptions();
            urlOptions.setUrl(urlOptionsDto.getUrl());
            urlOptions.setDivClasses(urlOptionsDto.getDivClasses());
            urlOptions.setChunkSize(urlOptionsDto.getChunkSize());
            urlOptions.setChunkOverlap(urlOptionsDto.getChunkOverlap());
            logger.debug("Successful : {}", urlOptionsDto);
            return urlOptionsRepository.save(urlOptions);
        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 예외 던지기
            logger.error("Error saving URL options: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save URL options", e);
        }
    }

    @Override
    public List<UrlOptions> getAllUrlOptions() {
        return urlOptionsRepository.findAll();
    }
    @Override
    public UrlOptions getUrlOptionsById(Long id) {
        return urlOptionsRepository.findById(id).orElseThrow(() -> new RuntimeException("ID not found: " + id));
    }
}
