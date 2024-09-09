package com.rag.documentingestionservice.controller;


import com.rag.documentingestionservice.dto.UrlExtractRequestDto;
import com.rag.documentingestionservice.dto.UrlExtractResponseDto;
import com.rag.documentingestionservice.dto.UrlOptionsDto;
import com.rag.documentingestionservice.dto.ChunkedDataDto;
import com.rag.documentingestionservice.dto.embedding.UpstageEmbeddingResponse;
import com.rag.documentingestionservice.dto.layoutanalysis.LayoutAnalysisResponse;
import com.rag.documentingestionservice.dto.search.SearchResponseDto;
import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import com.rag.documentingestionservice.entity.UrlOptions;
import com.rag.documentingestionservice.service.BoardService;
import com.rag.documentingestionservice.service.RagService;
import com.rag.documentingestionservice.service.SearchService;
import com.rag.documentingestionservice.service.TaskStatusService;
import com.rag.documentingestionservice.service.layoutAnlaysis.ChunkSaveService;
import com.rag.documentingestionservice.service.layoutAnlaysis.LayoutAnalysisService;
import com.rag.documentingestionservice.service.prompt.SolarLlmService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;


@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private static final Logger logger = LoggerFactory.getLogger(RagController.class);

    //RagController: 클라이언트 요청을 받아 서비스 레이어로 전달합니다. 여기서는 ExtractRequestDto를 받아서 처리 결과를 ExtractResponseDto로 반환합니다.

    private final RagService ragService;
    private final BoardService boardService;
    private final TaskStatusService taskStatusService;
    private final SearchService searchService;
    private final SolarLlmService solarLlmService;
    private final LayoutAnalysisService layoutAnalysisService;
    private final ChunkSaveService chunkSaveService;


    @PostMapping("/extract")
    public ResponseEntity<UrlExtractResponseDto> extractData(@RequestBody UrlExtractRequestDto requestDto) {
        UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 기능 : URL, 청크 사이즈, 청크 옵션, divClasses 정보를 저장
     * 요청 본문 : UrlOptionsDto
     * Description : POST /api/rag/url-options 는 사용자에 입력한 데이터를 MySQL 데이터베이스에 저장하며,
     * 저장된 데이터는 나중에 데이터를 추출하고 청킹을 하는데 사용함
     */

    @PostMapping("/url-options")
    public ResponseEntity<UrlOptions> saveUrlOptionsToMySQL(@RequestBody UrlOptionsDto urlOptionsDto) {
        try {
            UrlOptions savedOptions = boardService.saveUrlOptions(urlOptionsDto);
            logger.info("Id {}", savedOptions.getId());
            logger.info("URL {}", savedOptions.getUrl());
            logger.info("Chunk Size {}", savedOptions.getChunkSize());
            logger.info("Div Classes {}", savedOptions.getDivClasses());
            return ResponseEntity.ok(savedOptions);
        } catch (Exception e) {
            logger.error("Error saving URL options: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
    @PostMapping("/chunk")
    public void saveChunkedData(@RequestBody ChunkedDataDto chunkedData) {
        ragService.processAndSaveChunkedData(chunkedData);
    }

    @PostMapping("/chunk-and-save")
    public void chunkAndSaveData(@RequestBody ChunkedDataDto chunkedDataDto) {
        System.out.println("Received content: " + chunkedDataDto.getContent());
        ragService.processAndSaveChunkedData(chunkedDataDto);
    }


    /**
     * 기능 : 저장된 URL 옵션 ID를 사용하여 데이터를 추출, 청킹, 엘라스틱서치에 저장하는 작업을 한 번에 수행합니다.
     * Description : POST /api/rag/process/{id}
     * 이 API는 UrlOptions를 조회한 후, 자동으로 데이터를 추출, 청킹, 엘라스틱서치에 저장하는 작업을 수행합니다.
     */

    @PostMapping("/process/{id}")
    public ResponseEntity<UrlExtractResponseDto> processById(@PathVariable("id") Long id) {
        try {
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            logger.info("{} 기반으로 조회한 넘어온 전체 데이터 조회 : {}",id, urlOptions);

            UrlExtractRequestDto requestDto = new UrlExtractRequestDto(urlOptions.getUrl(), urlOptions.getDivClasses(), urlOptions.getChunkSize(), urlOptions.getChunkOverlap());
            logger.info("Request Dto's Url : {}", requestDto.getUrl());
            logger.info("Request Dto's Chunked Size : {}", requestDto.getChunkSize());
            logger.info("Request Dto's Chunk Overlap : {}", requestDto.getChunkOverlap());
            logger.info("Request Dto's Div Classes : {}", requestDto.getDivClasses());

            UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
            logger.info("Chunks : {}", responseDto.getChunks());

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Error processing data with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PostMapping("/process2/{id}")
    public void proecssById2(@PathVariable("id") Long id) {
        try {
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            logger.info("{} 기반으로 조회한 넘어온 전체 데이터 조회 : {}",id, urlOptions);

            UrlExtractRequestDto requestDto = new UrlExtractRequestDto(urlOptions.getUrl(), urlOptions.getDivClasses(), urlOptions.getChunkSize(), urlOptions.getChunkOverlap());
            logger.info("Request Dto's Url : {}", requestDto.getUrl());
            logger.info("Request Dto's Chunked Size : {}", requestDto.getChunkSize());
            logger.info("Request Dto's Chunk Overlap : {}", requestDto.getChunkOverlap());
            logger.info("Request Dto's Div Classes : {}", requestDto.getDivClasses());

            UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
            logger.info("Chunks : {}", responseDto.getChunks());

            ChunkedDataDto chunkedDataDto = new ChunkedDataDto();
            chunkedDataDto.setContent(responseDto.getChunks());
            chunkedDataDto.setChunkSize(responseDto.getChunks().size());

            ragService.processAndSaveChunkedData(chunkedDataDto);

        } catch (Exception e) {
            logger.error("Error processing data with ID: {}", id, e);
        }
    }


    @PostMapping("/process3/{id}")
    public String proecssById3(@PathVariable("id") Long id) {
        try {
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            logger.info("{} 기반으로 조회한 넘어온 전체 데이터 조회 : {}", id, urlOptions);

            UrlExtractRequestDto requestDto = new UrlExtractRequestDto(urlOptions.getUrl(), urlOptions.getDivClasses(), urlOptions.getChunkSize(), urlOptions.getChunkOverlap());
            logger.info("Request Dto's Url : {}", requestDto.getUrl());
            logger.info("Request Dto's Chunked Size : {}", requestDto.getChunkSize());
            logger.info("Request Dto's Chunk Overlap : {}", requestDto.getChunkOverlap());
            logger.info("Request Dto's Div Classes : {}", requestDto.getDivClasses());

            UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
            logger.info("Chunks : {}", responseDto.getChunks());

            ChunkedDataDto chunkedDataDto = new ChunkedDataDto();
            chunkedDataDto.setContent(responseDto.getChunks());
            chunkedDataDto.setChunkSize(responseDto.getChunks().size());

            ragService.processAndSaveChunkedData(chunkedDataDto);
            return "200";

        } catch (Exception e) {
            logger.error("Error processing data with ID: {}", id, e);
            return "error";

        }
    }


    @PostMapping("/index-process/{id}")
    public ResponseEntity<String> IndexProcessById(@PathVariable("id") Long id) {
        try {
            // 1. URL 옵션 조회
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            logger.info("조회된 URL 옵션 (ID: {}): {}", id, urlOptions);

            // 2. URL 옵션을 기반으로 데이터 추출 및 청킹
            UrlExtractRequestDto requestDto = new UrlExtractRequestDto(
                    urlOptions.getUrl(),
                    urlOptions.getDivClasses(),
                    urlOptions.getChunkSize(),
                    urlOptions.getChunkOverlap()
            );
            UrlExtractResponseDto responseDto = ragService.extractAndChunkData(requestDto);
            logger.info("추출된 청크 데이터: {}", responseDto.getChunks());

            // 3. 청크된 데이터를 Elasticsearch에 저장
            ChunkedDataDto chunkedDataDto = new ChunkedDataDto();
            chunkedDataDto.setId(UUID.randomUUID().toString());  // 고유 ID 생성
            chunkedDataDto.setContent(responseDto.getChunks());
            chunkedDataDto.setChunkSize(responseDto.getChunks().size());
            chunkedDataDto.setSourceUrl(requestDto.getUrl());  // 원본 URL 추가

            ragService.processAndSaveChunkedData(chunkedDataDto);

            return ResponseEntity.ok("데이터가 성공적으로 처리되고 저장되었습니다.");

        } catch (Exception e) {
            logger.error("ID {}에 대한 데이터 처리 중 오류 발생: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터 처리 중 오류가 발생했습니다.");
        }
    }


    /**
     * 기능 : 상세화면 진입 시, 해당 데이터를 저장
     * Description : POST /api/rag/chunk
     * 기존 API로, 필요 시 사용자가 청킹된 데이터를 직접 엘라스틱서치에 저장할 수 있습니다.
     */

    @PostMapping("/index-process")
    public void IndexProcess(@RequestBody ChunkedDataDto chunkedData) {
        ragService.processAndSaveChunkedData(chunkedData);
    }




    /**
     * RagController에서 비동기 작업을 실행하고, 작업의 상태를 확인할 수 있는 API를 제공합니다.
     */

    @PostMapping("/process/async/{id}")
    public ResponseEntity<String> processByIdAsync(@PathVariable("id") Long id) {
        try {
            UrlOptions urlOptions = boardService.getUrlOptionsById(id);
            UrlExtractRequestDto requestDto = new UrlExtractRequestDto(
                    urlOptions.getUrl(),
                    urlOptions.getDivClasses(),
                    urlOptions.getChunkSize(),
                    urlOptions.getChunkOverlap()
            );

            String taskId = UUID.randomUUID().toString();
            taskStatusService.updateTaskStatus(taskId, "Processing");

            Future<String> result = ragService.processAndSaveChunkedDataAsync(requestDto);

            // 작업이 완료되면 상태를 업데이트하는 별도의 스레드를 시작
            new Thread(() -> {
                try {
                    String status = result.get();
                    taskStatusService.updateTaskStatus(taskId, status);
                } catch (Exception e) {
                    taskStatusService.updateTaskStatus(taskId, "Failed");
                }
            }).start();

            return ResponseEntity.ok("Task started with ID: " + taskId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start task");
        }
    }

    @GetMapping("/process/status/{taskId}")
    public ResponseEntity<String> getTaskStatus(@PathVariable("taskId") String taskId) {
        String status = taskStatusService.getTaskStatus(taskId);
        return ResponseEntity.ok("Task ID: " + taskId + " is " + status);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponseDto> searchByQuery(@RequestParam("query") String query) {
        logger.info("query 들어오는 것 확인 -> {}", query);
        SearchResponseDto response = searchService.searchByQuery2(query);
        logger.debug("Response 들어오는거 확인 -> {}", response.getAnswer());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search_and_embed")
    public ResponseEntity<SearchResponseDto2> searchAndEmbed(@RequestParam("query") String query) {
        logger.info("query 들어오는 것 확인 -> {}", query);
        SearchResponseDto2 response = searchService.searchAndEmbed(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search_and_embed2")
    public ResponseEntity<SearchResponseDto2> searchAndEmbed2(@RequestParam("query") String query) {
        logger.info("query 들어오는 것 확인 -> {}", query);
        SearchResponseDto2 response = searchService.searchAndEmbed2(query);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search_and_embed3")
    public ResponseEntity<String> searchAndEmbed3(@RequestParam("query") String query) {
        logger.info("query 들어오는 것 확인 -> {}", query);
        SearchResponseDto2 response = searchService.searchAndEmbed2(query);
        System.out.println("리스판스"+response);

        // 상위 K개 문서를 기반으로 LLM에 요청
        String llmAnswer = solarLlmService.getAnswerFromLlm2(query, response.getDocuments());

        System.out.println("asdasdasdasdasdasd"+llmAnswer);

        return ResponseEntity.ok(llmAnswer);
    }


    /**
     * 문서 분석을 위한 테스트 엔드포인트
     * @param file 문서 파일 (예: PDF, 이미지)
     * @param ocr OCR 옵션 (기본값: true)
     * @return 레이아웃 분석 결과
     */
    @PostMapping("/test/layout-analyze")
    public ResponseEntity<LayoutAnalysisResponse> analyzeDocument(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam(value = "ocr", defaultValue = "true") boolean ocr) {
        try {
            logger.info("문서 레이아웃 분석 요청 시작");
            LayoutAnalysisResponse response = layoutAnalysisService.analyzeDocument(file, ocr);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("문서 레이아웃 분석 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 청킹된 데이터를 저장하기 위한 테스트 엔드포인트
     * @param documentId 문서 ID
     * @param sourceFileName 원본 파일 이름
     * @return 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/save-chunks")
    public ResponseEntity<String> saveChunkedData(@RequestParam("documentId") String documentId,
                                                  @RequestParam("sourceFileName") String sourceFileName,
                                                  @RequestBody LayoutAnalysisResponse layoutAnalysisResponse) {
        try {
            logger.info("청킹된 데이터 저장 요청 시작");
            chunkSaveService.saveChunkedData(layoutAnalysisResponse.getElements(), documentId, sourceFileName);
            return ResponseEntity.ok("청킹된 데이터가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            logger.error("청킹된 데이터를 저장하는 동안 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("청킹된 데이터를 저장하는 동안 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
