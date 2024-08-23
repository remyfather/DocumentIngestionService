package com.rag.documentingestionservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UrlExtractRequestDto {

    private String url;
    private List<String> divClasses;
    private int chunkSize;
    private int chunkOverlap;
}
