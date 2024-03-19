package org.fitness.serviceImpl;

import org.fitness.services.FitnessService;
import org.fitness.utilities.*;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class FitnessServiceImpl implements FitnessService {
    private static DealFinder dealFinder = new DealFinder();
    Utilities utl =new Utilities();
    private static FrequencyCount frequencyCount = new FrequencyCount();
    private static HtmlParser parser = new HtmlParser();
    public static final String citySearchFile = "/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/CitySearchHistory.txt";
    public static String outputFolderName ="/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/ParsedFiles";
    public static Map<String, Set<String>> i_index;
    public static final String keywordSearchFile = "/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/KeySearchHistory.txt";
    private static InvertedIndexing invertedIndexing = new InvertedIndexing();
    public Map<String, String> historySearchList() {
        // To get list of cities that were searched before
        return utl.historySearchList();
    }

    @Override
    public List<String> manualSearchList(String location) {
        //To get list of cities that we have already in cities List
        return utl.proceedWithManualInput(location);
    }

    @Override
    public void webScrapper(String location) {
        dealFinder.webScrapper(location);
    }

    @Override
    public void parseWebsite() {
        File PrcdFilesFldr = new File(outputFolderName);
        //check_if_the_folder_already_exists

        if (PrcdFilesFldr.exists() == false) {
            PrcdFilesFldr.mkdirs();
            parser.parseWebsites();
        }
        else {
            if (PrcdFilesFldr.listFiles().length == 0) {   // if there are files present in the folder
                parser.parseWebsites();
            }
        }

    }

    @Override
    public void invertedIndexing(Map filterParams) {
        i_index = invertedIndexing.buildInvertedIndex();
        if (i_index != null) {
            String input = filterParams.get("text").toString().toLowerCase();
                // add searched city into history file
                try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(keywordSearchFile, true))) {
                    BufferWriter.write(input);
                    BufferWriter.newLine(); // new line
                    BufferWriter.close();
                }
                catch (IOException e) {
                    System.out.println("Something went wrong while interacting with file");
                }

                // processing valid input
                String[] in_arr = input.split(" ");
                // storing files that contain given keyword
                Set<String> matchedDocs = new HashSet<>();
                for (String keyword : in_arr) {
                    if (i_index.get(keyword) != null)
                        matchedDocs.addAll(i_index.get(keyword));
                }

                // processing inverted index to get frequency count and page ranking
                List<String> matchedDocsList = new ArrayList<>(); matchedDocsList.addAll(matchedDocs);
                List<String> keywordsList = Arrays.asList(in_arr);
                List<Map.Entry<String, Integer>> keywordFrequencyMap = frequencyCount.getFrequencyCount(matchedDocsList, keywordsList);

                if (keywordFrequencyMap.size() > 0) {
                    System.out.println("Here are the list of most relavent sites for your search");
                    int count=1;
                    for (Map.Entry<String, Integer> entry : keywordFrequencyMap) {
                        System.out.println(count++ +". "+ entry.getKey() + "\t(total " + entry.getValue() + " Occurrence)");
                    }
                }
                else {
                    System.out.println( "Nothing found related to your search! Try something else related to a gym and fitness");
                }



        }
    }


}
