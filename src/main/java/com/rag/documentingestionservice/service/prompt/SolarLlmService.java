package com.rag.documentingestionservice.service.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SolarLlmService {

    private static final Logger logger = LoggerFactory.getLogger(SolarLlmService.class);

    @Value("${upstage.api.key}")
    private String upstageApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // LLM 요청을 보내고 응답을 받는 메서드
    public String getAnswerFromLlm(String userQuery, List<SearchResponseDto2.ChunkedDataDto> topKDocuments) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + upstageApiKey);
        headers.set("Content-Type", "application/json");

        System.out.println("asdfasdfadsfaa1111"+topKDocuments);
        // 상위 K개 문서를 기반으로 프롬프트를 생성합니다.
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("사용자의 질문: ").append(userQuery).append("\n\n");
        promptBuilder.append("다음은 관련된 정보입니다:\n");
        for (SearchResponseDto2.ChunkedDataDto doc : topKDocuments) {
            promptBuilder.append("- ").append(doc.getContent()).append("\n");
        }
        String prompt = promptBuilder.toString();
        System.out.println("프롬프트 만들어진거 확인---->"+prompt);

        // LLM API 요청 본문 작성
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "solar-1-mini-chat");
        requestBodyMap.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBodyMap.put("stream", false);


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBodyMap, headers);

        try {
            logger.info("보낼 JSON: {}", new ObjectMapper().writeValueAsString(requestBodyMap)); // 요청 본문 출력
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.upstage.ai/v1/solar/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            logger.info("LLM API 응답 -----> {}", response.getBody());

            // 응답에서 필요한 내용을 추출합니다.
            String answer = extractAnswerFromResponse(response.getBody());
            return answer;

        } catch (Exception e) {
            logger.error("LLM API 요청 중 오류 발생: ", e);
            return "LLM 요청에 실패했습니다.";
        }
    }


    // 응답에서 답변을 추출하는 메서드
    private String extractAnswerFromResponse(String responseBody) {
        // 응답을 JSON으로 파싱하여 "content" 필드를 추출합니다.
        // Jackson 또는 Gson 같은 라이브러리를 사용하여 쉽게 구현할 수 있습니다.
        // 여기서는 간단한 예제로 정규식을 사용합니다.
        String pattern = "\"content\":\"([^\"]*)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "LLM 응답에서 답변을 찾을 수 없습니다.";
        }
    }

    public String getAnswerFromLlm2(String userQuery, List<SearchResponseDto2.ChunkedDataDto> topKDocuments) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + upstageApiKey);
        headers.set("Content-Type", "application/json");

        // 프롬프트 생성
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음 데이터만 기반으로 답변하세요: ");

        // 청킹된 데이터 추가
        for (SearchResponseDto2.ChunkedDataDto doc : topKDocuments) {
            promptBuilder.append(doc.getContent()).append("\n");
        }

        promptBuilder.append("\n질문: ").append(userQuery).append("\n\n");
        promptBuilder.append("만약 제공된 데이터에 답변이 없을 경우, '해당 데이터에는 관련 정보가 없습니다.'라고 답변해주세요.");

        String prompt = promptBuilder.toString();
        logger.info("생성된 프롬프트 -----> {}", prompt);

        // LLM API 요청 본문 작성
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "solar-1-mini-chat");
        requestBodyMap.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBodyMap.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBodyMap, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.upstage.ai/v1/solar/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            logger.info("LLM API 응답 -----> {}", response.getBody());

            // 응답에서 필요한 내용을 추출합니다.
            String answer = extractAnswerFromResponse(response.getBody());
            return answer;

        } catch (Exception e) {
            logger.error("LLM API 요청 중 오류 발생: ", e);
            return "LLM 요청에 실패했습니다.";
        }
    }

}
