package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.ChunkedDataDto;
import com.rag.documentingestionservice.dto.search.SearchRequestDto;
import com.rag.documentingestionservice.dto.search.SearchResponseDto;
import com.rag.documentingestionservice.dto.search.SearchResponseDto2;
import com.rag.documentingestionservice.repository.ChunkedDataElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final ChunkedDataElasticsearchRepository chunkedDataElasticsearchRepository;




    public SearchResponseDto SearchByQuery(SearchRequestDto requestDto) {
        logger.info("Extracting data from URL: {}", requestDto.getQuery());


        return null;

    }

    @Override
    public SearchResponseDto searchByQuery2(String query) {
        return chunkedDataElasticsearchRepository.searchByQuery(query);
    }

    @Override
    public List<ChunkedDataDto> searchByQuery3(String query) {
        return chunkedDataElasticsearchRepository.searchByQuery2(query);
    }

    @Override
    public SearchResponseDto2 searchAndEmbed(String query) {
        return chunkedDataElasticsearchRepository.searchAndEmbed(query);
    }

    @Override
    public SearchResponseDto2 searchAndEmbed2(String query) {
        return chunkedDataElasticsearchRepository.searchAndEmbed2(query);
    }

}
