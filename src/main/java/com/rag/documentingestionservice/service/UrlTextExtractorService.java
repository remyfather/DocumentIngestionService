package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.UrlExtractRequestDto;
import com.rag.documentingestionservice.dto.UrlExtractResponseDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the RagService interface.
 * Handles the extraction of data from a given URL and chunks the extracted data.
 */
@Service
public class UrlTextExtractorService implements RagService{

    private static final Logger logger = LoggerFactory.getLogger(UrlTextExtractorService.class);


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
}
