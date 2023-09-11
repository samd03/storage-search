package com.hevo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter

@SuperBuilder
public abstract class FileDetail {
    private String fileName;
    private String filePath;
}
