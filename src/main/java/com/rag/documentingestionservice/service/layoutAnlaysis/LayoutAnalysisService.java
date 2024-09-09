package com.rag.documentingestionservice.service.layoutAnlaysis;

import com.rag.documentingestionservice.dto.layoutanalysis.LayoutAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class LayoutAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(LayoutAnalysisService.class);

    @Value("${upstage.api.key}")
    private String upstageApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public LayoutAnalysisResponse analyzeDocument(MultipartFile file, boolean ocr) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + upstageApiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("document", file.getResource());
        body.add("ocr", ocr);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<LayoutAnalysisResponse> response = restTemplate.exchange(
                    "https://api.upstage.ai/v1/document-ai/layout-analysis",
                    HttpMethod.POST,
                    requestEntity,
                    LayoutAnalysisResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Layout Analysis 결과: {}", response.getBody());
                return response.getBody();
            } else {
                logger.error("Layout Analysis 요청 실패. 상태 코드: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("Layout Analysis 요청 중 오류 발생: ", e);
            return null;
        }
    }

}
