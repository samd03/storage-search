package com.hevo.listeners;

import com.hevo.Event;
import com.hevo.EventPublisher;
import com.hevo.FileEntityBuilder;
import com.hevo.dao.EsFileDao;
import com.hevo.elasticsearch.ESClient;
import com.hevo.elasticsearch.EsIndexRequest;
import com.hevo.entity.EsFileEntity;
import com.hevo.filefetcher.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileDetectionListener implements EventListener {

    @Autowired
    EsFileDao dao;
    @Autowired
    FileEntityBuilder fileEntityBuilder;


    EventPublisher eventPublisher;

    @Autowired
    public FileDetectionListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        eventPublisher.subscribe(this, Event.FILE_DETECTION);
    }

    @Override
    public void listen(Object payload) {
        File file = (File) payload;

        EsFileEntity esIndexRequest = fileEntityBuilder.buildParent(file);
        String id = dao.save(esIndexRequest);
        eventPublisher.publish(Event.RECORD_CEATION, new RecordCreationPayload(file, id));

    }
}
