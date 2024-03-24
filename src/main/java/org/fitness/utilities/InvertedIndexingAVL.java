package org.fitness.utilities;

import org.fitness.classes.AVLTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvertedIndexingAVL {

    private AVLTree<String, Set<String>> invertedIndex;
    public final String outputFolderName = "/Users/rohansethi/Downloads/ACC_Project/src/main/resources/ParsedFiles";
    public Utilities utl = new Utilities();

    public InvertedIndexingAVL() {
        invertedIndex = new AVLTree<>();
    }

    public Map<String, Set<String>> buildInvertedIndex() {
        File directoryCNC = new File(outputFolderName);

        if (!directoryCNC.exists() || !directoryCNC.isDirectory()) {
            System.err.println("Directory path not valid");
            return new HashMap<>();
        }

        File[] filesCNC = directoryCNC.listFiles();

        if (filesCNC == null || filesCNC.length == 0) {
            System.err.println("Files could not be found in the directory");
            return new HashMap<>();
        }

        for (File fileCNC : filesCNC) {
            if (fileCNC.isFile() && fileCNC.getName().endsWith(".txt")) {
                indexDocument(fileCNC);
            }
        }

        // Convert AVLTree to Map before returning
        Map<String, Set<String>> invertedIndexMap = new HashMap<>();
        invertedIndex.inOrderTraversal((key, value) -> invertedIndexMap.put(key, value));

        return invertedIndexMap;
    }

    private void indexDocument(File fileCNC) {
        try (BufferedReader redrCNC = new BufferedReader(new FileReader(fileCNC))) {
            String lineCNC;
            String documentId = fileCNC.getName();

            while ((lineCNC = redrCNC.readLine()) != null) {
                String[] terms = lineCNC.split("\\s+");

                for (String term : terms) {
                    Set<String> documents = invertedIndex.get(term);
                    if (documents == null) {
                        documents = new HashSet<>();
                        invertedIndex.put(term, documents);
                    }
                    documents.add(documentId);
                }
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while interacting with file");
        }
    }

    public void printIndex() {
        invertedIndex.inOrderTraversal((key, value) -> System.out.println(key + ": " + value));
    }
}
