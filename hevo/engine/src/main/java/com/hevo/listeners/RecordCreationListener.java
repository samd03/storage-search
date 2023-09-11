package com.hevo.listeners;

import com.hevo.Event;
import com.hevo.EventPublisher;
import com.hevo.FileEntityBuilder;
import com.hevo.dao.EsFileDao;
import com.hevo.elasticsearch.ESClient;
import com.hevo.elasticsearch.EsIndexRequest;
import com.hevo.entity.EsFileEntity;
import com.hevo.filefetcher.File;
import com.hevo.filefetcher.StorageClient;
import com.hevo.filefetcher.StorageClientFactory;
import com.hevo.parse.FileParser;
import com.hevo.parse.ParserFactory;
import com.hevo.parse.Record;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordCreationListener implements EventListener{

    @Autowired
    private StorageClientFactory storageClientFactory;

    @Autowired
    private ParserFactory parserFactory;

    @Autowired
    private EsFileDao dao;

    @Autowired
    private FileEntityBuilder fileEntityBuilder;


    private EventPublisher publisher;

    @Autowired
    public RecordCreationListener(EventPublisher publisher) {
        this.publisher = publisher;
        publisher.subscribe(this, Event.RECORD_CEATION);
    }

    @Override
    public void listen(Object payload) {
        RecordCreationPayload recordCreationPayload =(RecordCreationPayload) payload;
        File file = recordCreationPayload.getFile();
        StorageClient storageClient = storageClientFactory.get(file.getStorageType());
        String localaPath = String.format("tmp/%s/%s", UUID.randomUUID(), file.getAbsolutePath());
        storageClient.downloadFile(file.getAbsolutePath(), localaPath);
        FileParser parser  = parserFactory.get(file.getName());
        List<Record> recordList = parser.getRecords(localaPath);

        recordList.forEach(
                rec -> dao.save(fileEntityBuilder.buildChild(rec, recordCreationPayload.getParentId())));
    }
}
