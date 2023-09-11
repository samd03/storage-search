package com.hevo.filefetcher;

import com.hevo.StorageType;
import com.hevo.dropbox.DropboxClient;
import org.elasticsearch.common.inject.Singleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StorageClientFactory {

    @Autowired
    DropboxClient dropboxClient;

    public StorageClient get(StorageType type) {
        switch (type) {
            case DROPBOX:
                return dropboxClient;
        }
        return null;
    }
}
