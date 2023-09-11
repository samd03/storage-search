package com.hevo;

import com.hevo.dao.EsFileDao;
import com.hevo.dropbox.DropboxClient;
import com.hevo.dropbox.DropboxFile;
import com.hevo.entity.EsFileEntity;
import com.hevo.filefetcher.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    DropboxClient dropboxClient;
    @Autowired
    EventPublisher eventPublisher;

    @Autowired
    EsFileDao dao;

    public void indexFiles(String prefix) {
        List<DropboxFile> files = dropboxClient.fetchFiles(prefix);
        files.forEach(f -> eventPublisher.publish(Event.FILE_DETECTION, f));
    }

    public List<FileDetail> search(String query) {
        List<EsFileEntity> entities = dao.searchByQuery(query);
        return entities.stream()
                .map(entity -> DropBoxFileDetails.builder()
                        .fileName(entity.getFileName())
                        .filePath(entity.getAbsolutePath()).build())
                .collect(Collectors.toList());
    }

    public void delete(String absolutePath) {
        Optional<EsFileEntity> entity = dao.search(absolutePath);
        if (!entity.isPresent()) {
            return;
        }
        dao.delete(entity.get().getId());
    }


}
