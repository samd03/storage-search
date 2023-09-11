package com.hevo.dropbox;

import com.hevo.StorageType;
import com.hevo.filefetcher.File;
import lombok.Getter;

@Getter
public class DropboxFile extends File {

    public DropboxFile(String name, String absolutePath) {
        super(StorageType.DROPBOX, name, absolutePath);
    }
}
