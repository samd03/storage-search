package com.hevo.filefetcher;

import java.util.List;

public abstract class StorageClient {
    public abstract List<? extends File> fetchFiles(String prefix);
    public abstract void downloadFile(String filePath, String localFilePath);
}
