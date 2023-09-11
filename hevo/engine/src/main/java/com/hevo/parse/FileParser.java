package com.hevo.parse;

import java.util.List;

public abstract class FileParser {
    public abstract MetaData parseMeta(String filePath);

    public abstract List<Record> getRecords(String filePath);

}
