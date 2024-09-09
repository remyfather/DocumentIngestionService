package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.UrlExtractRequestDto;
import com.rag.documentingestionservice.dto.UrlExtractResponseDto;
import com.rag.documentingestionservice.dto.ChunkedDataDto;
import com.rag.documentingestionservice.repository.ChunkedDataElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Implementation of the RagService interface.
 * Handles the extraction of data from a given URL and chunks the extracted data.
 */
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService{

    private static final Logger logger = LoggerFactory.getLogger(RagServiceImpl.class);

    private final ChunkedDataElasticsearchRepository chunkedDataElasticsearchRepository;

    @Override
    public UrlExtractResponseDto extractAndChunkData(UrlExtractRequestDto requestDto) {

        logger.info("Extracting data from URL: {}", requestDto.getUrl());
        logger.info("Extracting data from DivClass's Length: {}", requestDto.getDivClasses().size());
        for (int i=0; i<=requestDto.getDivClasses().size(); i++){
            logger.info("Extracting data from DivClass [{}]: {}", i, requestDto.getDivClasses().get(0));

        }
        logger.info("Extracting data from ChunkSize: {}", requestDto.getChunkSize());
        logger.info("Extracting data from OverLap: {}", requestDto.getChunkOverlap());


        String url = requestDto.getUrl();
        List<String> divClasses = requestDto.getDivClasses();
        int chunkSize = requestDto.getChunkSize();
        int chunkOverlap = requestDto.getChunkOverlap();

        try {
            // Connect to the URL and parse the document
            Document doc = Jsoup.connect(url).get();
            StringBuilder extractedData = new StringBuilder();

            // Extract text from the specified div classes
            for (String divClass : divClasses){
                logger.debug("Extracting data for div class: {}", divClass);
                Elements elements = doc.getElementsByClass(divClass);
                extractedData.append(elements.text()).append(" ");
            }

            // Chunk the extracted data
            List<String> chunks = chunkData(extractedData.toString().trim(), chunkSize, chunkOverlap);
            logger.info("Data extraction and chunking complete.");
            return new UrlExtractResponseDto(chunks);

        } catch (IOException e) {
            logger.error("Failed to connect to the URL: {}", url, e);
            throw new RuntimeException("Failed to connect to the URL: " + url, e);
        }
    }

    /**
     * Splits the extracted data into chunks based on the specified chunk size and overlap.
     *
     * @param data the data to be chunked
     * @param chunkSize the size of each chunk
     * @param chunkOverlap the overlap between consecutive chunks
     * @return a list of chunked data
     */

    private List<String> chunkData(String data, int chunkSize, int chunkOverlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < data.length()) {
            int end = Math.min(start + chunkSize, data.length());
            chunks.add(data.substring(start, end));
            start += chunkSize - chunkOverlap;
        }

        logger.debug("Chunking complete: {} chunks created.", chunks.size());



        return chunks;
    }

    /**
     * 청크된 데이터를 Elasticsearch에 저장하는 메서드
     *
     * @param chunks 청크된 데이터 리스트
     * @param sourceUrl 원본 URL
     */
    private void saveChunksToElasticsearch(List<String> chunks, String sourceUrl) {
        ChunkedDataDto chunkedData = new ChunkedDataDto();
        chunkedData.setId(UUID.randomUUID().toString()); // 고유 ID 생성
        chunkedData.setContent(chunks); // 전체 chunk 리스트를 content로 설정
        chunkedData.setSourceUrl(sourceUrl);

        // 전체 chunks의 길이를 합산하여 chunkSize 설정
        int totalChunkSize = chunks.stream().mapToInt(String::length).sum();
        chunkedData.setChunkSize(totalChunkSize);

        // Elasticsearch에 저장
        chunkedDataElasticsearchRepository.saveChunkedData(chunkedData);
        logger.debug("Saved chunked data to Elasticsearch with ID: {}", chunkedData.getId());
    }

    @Override
    public void processAndSaveChunkedData(ChunkedDataDto chunkedDataDto) {
        logger.info("Processing and saving chunked data: {}", chunkedDataDto.getId());
        chunkedDataElasticsearchRepository.saveChunkedData(chunkedDataDto);
        logger.info("Chunked data saved successfully");
    }

    public void processAndSaveChunkedData2(ChunkedDataDto chunkedDataDto) {
        logger.info("Processing and saving chunked data: {}", chunkedDataDto.getId());
        chunkedDataElasticsearchRepository.saveChunkedData(chunkedDataDto);
        logger.info("Chunked data saved successfully");
    }

    /**
     * 비동기 작업을 처리하는 코드는 @Async 어노테이션을 사용하여 정의되어 있습니다.
     * 이 작업은 CompletableFuture를 반환하여 비동기적으로 실행되며, 작업이 완료되면 상태를 업데이트할 수 있습니다.
     * processAndSaveChunkedDataAsync 메서드는 데이터를 추출하고 청킹하며, 이를 엘라스틱서치에 저장합니다.
     * 작업이 완료되면 "Success" 또는 "Failure" 상태를 반환합니다.
     */

    @Async
    public CompletableFuture<String> processAndSaveChunkedDataAsync(UrlExtractRequestDto requestDto) {
        try {
            // 데이터 추출 및 청킹 작업
            UrlExtractResponseDto responseDto = extractAndChunkData(requestDto);

            // ChunkedDataDto에 전체 청크 리스트를 설정
            ChunkedDataDto chunkedDataDto = new ChunkedDataDto();
            chunkedDataDto.setId(UUID.randomUUID().toString());
            chunkedDataDto.setContent(responseDto.getChunks()); // 전체 청크 리스트를 설정
            chunkedDataDto.setChunkSize(responseDto.getChunks().size());
            chunkedDataDto.setSourceUrl(requestDto.getUrl());

            // 엘라스틱서치에 저장
            chunkedDataElasticsearchRepository.saveChunkedData(chunkedDataDto);

            return CompletableFuture.completedFuture("Success");
        } catch (Exception e) {
            // 예외 발생 시 "Failure" 반환
            return CompletableFuture.completedFuture("Failure");
        }
    }
}
