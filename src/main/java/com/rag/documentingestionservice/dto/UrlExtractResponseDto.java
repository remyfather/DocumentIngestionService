package com.rag.documentingestionservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UrlExtractResponseDto {
    private List<String> chunks;
}
