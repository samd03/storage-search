package com.hevo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
public class EsFileEntity {
    private String fileName;
    private String absolutePath;
    private String format;
    private String title;
    private Join joinField;
    private String storageType; // s3, dropbox
    private RecordEntity record;
    @Setter
    private String id;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordEntity {
        private String orderId;
        private String content;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Join {
        String name;
        String parent;
    }
}
