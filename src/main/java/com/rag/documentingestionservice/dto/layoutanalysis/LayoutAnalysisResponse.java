package com.rag.documentingestionservice.dto.layoutanalysis;

import lombok.Data;

import java.util.List;

@Data
public class LayoutAnalysisResponse {
    private String api;
    private int billed_pages;
    private List<Element> elements;
    private String html;
    private Metadata metadata;
    private String mimetype;
    private String model;
    private String text;

    @Data
    public static class Element {
        private List<BoundingBox> bounding_box;
        private String category;
        private String html;
        private int id;
        private int page;
        private String text;
    }

    @Data
    public static class BoundingBox {
        private int x;
        private int y;
    }

    @Data
    public static class Metadata {
        private List<Page> pages;

        @Data
        public static class Page {
            private int height;
            private int page;
            private int width;
        }
    }
}
