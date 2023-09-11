package com.hevo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameTokenizer {

    public static void main(String[] args) {
        String filename = "/sanchit/example_file_name_v1.2.txt";

        // Define your custom tokenizer pattern
        Pattern pattern = Pattern.compile("[\\W_]+"); // Tokenize by non-word characters and underscores

        // Tokenize the filename
        String[] tokens = pattern.split(filename);

        // Filter tokens based on your criteria
        for (String token : tokens) {
            if (isValidToken(token)) {
                System.out.println("Valid Token: " + token);
            }
        }
    }

    public static boolean isValidToken(String token) {
        // Implement your filtering criteria here
        // For example, you can filter tokens that are at least 3 characters long
        return token.length() >= 3;
    }
}
