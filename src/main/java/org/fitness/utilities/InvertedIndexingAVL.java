package org.fitness.utilities;

import org.fitness.classes.AVLTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InvertedIndexingAVL {
    private AVLTree invertedIndex = new AVLTree();
    private final String outputFolderName = "/Users/rohansethi/Downloads/ACC_Project/src/main/resources/ParsedFiles";

    public Map<String, Set<String>> buildInvertedIndex() {
        File directory = new File(outputFolderName);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Directory path not valid");
            return new HashMap<>();
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            System.err.println("No files found in the directory");
            return new HashMap<>();
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                indexDocument(file);
            }
        }

        Map<String, Set<String>> invertedIndexMap = new HashMap<>();
        invertedIndex.inOrderTraversal(invertedIndexMap::put);
        return invertedIndexMap;
    }

    private void indexDocument(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String term : line.split("\\s+")) {
                    invertedIndex.insert(term.toLowerCase(), file.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getPath());
        }
    }
}

