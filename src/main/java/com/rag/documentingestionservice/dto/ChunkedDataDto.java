package com.rag.documentingestionservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChunkedDataDto {
    private String id;
    private List<String> content;  // List<String> 타입으로 수정
    private String sourceUrl;
    private int chunkSize;
}
