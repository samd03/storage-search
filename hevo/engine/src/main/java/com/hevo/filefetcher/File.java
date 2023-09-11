package com.hevo.filefetcher;

import com.hevo.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Getter
@AllArgsConstructor
public abstract class File {

    private StorageType storageType;

    private String name;

    private String absolutePath;



}
