package com.hevo;

import com.hevo.elasticsearch.EsIndexRequest;
import com.hevo.entity.EsFileEntity;
import com.hevo.filefetcher.File;
import com.hevo.parse.Record;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public  class FileEntityBuilder {
    public EsFileEntity buildParent(File file) {
        return EsFileEntity.builder()
                .fileName(file.getName())
                .absolutePath(file.getAbsolutePath())
                .storageType(file.getStorageType().name())
                .joinField(new EsFileEntity.Join("file", null))
                .build();
    }

    public EsFileEntity buildChild(Record record, String parentId) {
        return EsFileEntity.builder()
                .joinField(new EsFileEntity.Join("record", parentId))
                .record(EsFileEntity.RecordEntity.builder()
                        .content(record.getContent())
                        .build())
                .build();
    }



}
