package com.hevo.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CSVParser extends FileParser{
    @Override
    public MetaData parseMeta(String filePath) {
        return null;
    }

    @Override
    public List<Record> getRecords(String filePath) {
        try {
            return readCSVToList(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Record> readCSVToList(String filePath) throws IOException {
        List<Record> lines = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            lines.add(new Record(line));
        }

        bufferedReader.close();
        return lines;
    }
}
