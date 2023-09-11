package com.hevo.parse;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParserFactory {

    @Autowired
    private CSVParser csvParser;

    public FileParser get(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        switch (ext) {
            case "csv":
                return csvParser;
        }
        return null;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ""; // Return an empty string for files with no extension
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
