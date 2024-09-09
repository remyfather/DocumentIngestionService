package com.rag.documentingestionservice.dto.layoutanalysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode를 포함합니다.
@NoArgsConstructor // 파라미터가 없는 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 파라미터로 갖는 생성자 생성
public class ChunkMetadata {
    private String documentId; // 문서 ID
    private String sourceFileName; // 문서의 원본 파일 이름
    private String category; // 요소 유형 (예: 제목, 본문, 표)
    private int pageNumber; // 페이지 번호
    private List<BoundingBox> boundingBox; // 텍스트 위치 (Bounding Box)
    private String text; // 청크된 텍스트 내용

    // 내부 클래스 BoundingBox
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoundingBox {
        private int x;
        private int y;
    }
}
