package com.rag.documentingestionservice.service;

import com.rag.documentingestionservice.dto.ChunkedDataDto;
import com.rag.documentingestionservice.dto.search.SearchRequestDto;
import com.rag.documentingestionservice.dto.search.SearchResponseDto;
import com.rag.documentingestionservice.dto.search.SearchResponseDto2;

import javax.naming.directory.SearchResult;
import java.util.List;

public interface SearchService {
    SearchResponseDto SearchByQuery(SearchRequestDto requestDto);

    SearchResponseDto searchByQuery2(String query);

    List<ChunkedDataDto> searchByQuery3(String query);

    SearchResponseDto2 searchAndEmbed(String query);
    SearchResponseDto2 searchAndEmbed2(String query);
}
