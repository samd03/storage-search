package com.hevo.listeners;

import com.hevo.filefetcher.File;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class RecordCreationPayload {
    private File file;
    private String parentId;
}
