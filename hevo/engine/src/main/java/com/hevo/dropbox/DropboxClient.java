package com.hevo.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.hevo.StorageType;
import com.hevo.filefetcher.StorageClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class DropboxClient extends StorageClient {


    private DropboxConfig config;

    private DbxClientV2 client;

    @Autowired
    public DropboxClient(DropboxConfig config) {
        this.config = config;
        DbxRequestConfig dbxconfig = DbxRequestConfig.newBuilder(config.getAppName()).build();
        this.client = new DbxClientV2(dbxconfig, config.getAccessToken());
    }

    public List<DropboxFile> fetchFiles(String prefix) {
        List<DropboxFile> files = new ArrayList<>();
        prefix = prefix == null?"": prefix;
        listFilesRecursively(prefix, files);
        return files;
    }


    public void downloadFile(String dropboxFilePath, String localFilePath) {
        try {

            // Check if the local file already exists and delete it
            File localFile = new File(localFilePath);
            cleanUp(localFile);

            // Create parent directories for the local file if they don't exist
            File parentDir = localFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("Failed to create parent directories for the local file.");
            }

            // Download the file from Dropbox and save it locally
            try (OutputStream outputStream = new FileOutputStream(localFilePath)) {
                client.files().downloadBuilder(dropboxFilePath)
                        .download(outputStream);
                System.out.println("File downloaded and saved to " + localFilePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void cleanUp(File localFile) {
        if (localFile.exists()) {
            if (localFile.delete()) {
                log.info("Local file deleted.");
            } else {
                System.err.println("Failed to delete local file.");
            }
        }
    }


    private void listFilesRecursively(String folderPath, List<DropboxFile> files) {
        try {
            ListFolderResult result = client.files().listFolder(folderPath);

            for (Metadata metadata : result.getEntries()) {
                if (metadata instanceof FileMetadata) {
                    files.add(new DropboxFile(metadata.getName(), metadata.getPathDisplay()));
                } else if (metadata instanceof FolderMetadata) {
                    // Recursively list files in subfolder
                    listFilesRecursively(metadata.getPathDisplay(), files);
                }
            }
        } catch (DbxException e) {
            throw new RuntimeException(e);
        }
    }
}
