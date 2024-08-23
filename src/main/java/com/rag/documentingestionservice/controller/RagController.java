package com.rag.documentingestionservice.controller;


import com.rag.documentingestionservice.dto.UrlExtractRequestDto;
import com.rag.documentingestionservice.dto.UrlExtractResponseDto;
import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.entity.UrlOptions;
import com.rag.documentingestionservice.service.BoardService;
import com.rag.documentingestionservice.service.RagService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private static final Logger logger = LoggerFactory.getLogger(RagController.class);

    //RagController: 클라이언트 요청을 받아 서비스 레이어로 전달합니다. 여기서는 ExtractRequestDto를 받아서 처리 결과를 ExtractResponseDto로 반환합니다.

    private final RagService ragService;
    private final BoardService boardService;

    @PostMapping("/extract")
    public ResponseEntity<UrlExtractResponseDto> extractData(@RequestBody UrlExtractRequestDto requestDto) {
        UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/save-url-options")
    public ResponseEntity<UrlOptions> saveUrlOptions(@RequestBody UrlOptionsDto urlOptionsDto) {
        UrlOptions savedOptions = boardService.saveUrlOptions(urlOptionsDto);
        return ResponseEntity.ok(savedOptions);
    }

    @GetMapping("/url-options")
    public ResponseEntity<List<UrlOptions>> getUrlOptions() {
        List<UrlOptions> urlOptionsList = boardService.getAllUrlOptions();
        return ResponseEntity.ok(urlOptionsList);
    }

//    @GetMapping("/url-options/{id}")
//    public ResponseEntity<UrlOptions> getUrlOptionById(@PathVariable Long id) {
//        UrlOptions urlOptions = boardService.getUrlOptionsById(id);
//        if (urlOptions != null) {
//            return ResponseEntity.ok(urlOptions);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/url-options/{id}")
    public ResponseEntity<UrlOptions> getUrlOptionById(@PathVariable("id") Long id) {
        try {
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            return ResponseEntity.ok(urlOptions);
        } catch (Exception e) {
            logger.error("Error fetching URL options with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/extract/{id}")
    public ResponseEntity<UrlExtractResponseDto> extractDataById(@PathVariable("id") Long id) {

        try {
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            logger.info("urlOptions 확인 : {}",urlOptions);
            UrlExtractRequestDto requestDto = new UrlExtractRequestDto();
            requestDto.setUrl(urlOptions.getUrl());
            requestDto.setDivClasses(urlOptions.getDivClasses());
            requestDto.setChunkSize(urlOptions.getChunkSize());
            requestDto.setChunkOverlap(urlOptions.getChunkOverlap());

            logger.info("넘어온 URL : {}",requestDto.getUrl());
            logger.info("넘어온 DivClasses : {}",requestDto.getDivClasses());
            logger.info("넘어온 청크 사이즈 : {}",requestDto.getChunkSize());
            logger.info("넘어온 청크 오버랩 : {}",requestDto.getChunkOverlap());

            UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Error fetching URL options with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // try {
//            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
//            logger.info("urlOptions 확인 : {}",urlOptions);
//
//            UrlExtractRequestDto requestDto = new UrlExtractRequestDto();
//            requestDto.setUrl(urlOptions.getUrl());
//            requestDto.setDivClasses(urlOptions.getDivClasses());
//            requestDto.setChunkSize(urlOptions.getChunkSize());
//            requestDto.setChunkOverlap(urlOptions.getChunkOverlap());
//
//            UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
//            return ResponseEntity.ok(responseDto);
//        } catch (NoSuchElementException e) {
//            logger.error("No URL options found with ID: {}", id, e);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        } catch (Exception e) {
//            logger.error("Unexpected error during extraction for ID: {}", id, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
    }

}
