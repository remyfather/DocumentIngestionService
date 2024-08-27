package com.rag.documentingestionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlExtractRequestDto {

    private String url;
    private List<String> divClasses;
    private int chunkSize;
    private int chunkOverlap;
}
