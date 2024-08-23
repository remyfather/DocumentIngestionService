package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.entity.UrlOptions;
import com.rag.documentingestionservice.repository.UrlOptionsRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public UrlOptions saveUrlOptions(UrlOptionsDto urlOptionsDto) {
        UrlOptions urlOptions = new UrlOptions();
        urlOptions.setUrl(urlOptionsDto.getUrl());
        urlOptions.setDivClasses(urlOptionsDto.getDivClasses());
        urlOptions.setChunkSize(urlOptionsDto.getChunkSize());
        urlOptions.setChunkOverlap(urlOptionsDto.getChunkOverlap());
        return urlOptionsRepository.save(urlOptions);
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
