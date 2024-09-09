package com.rag.documentingestionservice.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *     "query": "사용자가 질의할 질문"
 * }
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    private String query;
}
