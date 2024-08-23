package com.rag.documentingestionservice.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class UrlOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ElementCollection
    private List<String> divClasses;

    private int chunkSize;
    private int chunkOverlap;
}
