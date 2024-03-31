package org.fitness.serviceImpl;

import org.fitness.services.FitnessService;
import org.fitness.utilities.*;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FitnessServiceImpl implements FitnessService {
    private static DealFinder dealFinder = new DealFinder();
    private static DealFinderAPIs dealFinderAPIs = new DealFinderAPIs();
    Utilities utl =new Utilities();
    private static FrequencyCount frequencyCount = new FrequencyCount();
    private static HtmlParser parser = new HtmlParser();
    public static final String citySearchFile = "/Users/rohansethi/Downloads/ACC_Project/src/main/resources/Files/CitySearchHistory.txt";
    public static String outputFolderName ="/Users/rohansethi/Downloads/ACC_Project/src/main/resources/ParsedFiles";
    public static Map<String, Set<String>> i_index;
    public static final String keywordSearchFile = "/Users/rohansethi/Downloads/ACC_Project/src/main/resources/Files/KeySearchHistory.txt";
    private static InvertedIndexing invertedIndexing = new InvertedIndexing();
    private static InvertedIndexingAVL  invertedIndexingAVL = new InvertedIndexingAVL();

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
    public String invertedIndexing(Map filterParams) {
        i_index = invertedIndexingAVL.buildInvertedIndex();
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

            if (!keywordFrequencyMap.isEmpty()) {
                StringBuilder jsonOutput = new StringBuilder();
                int count = 1;
                jsonOutput.append("{ \"relevantSites\": [");
                for (Map.Entry<String, Integer> entry : keywordFrequencyMap) {
                    jsonOutput.append("{")
                            .append("\"site\": \"").append(entry.getKey()).append("\", ")
                            .append("\"occurrences\": ").append(entry.getValue())
                            .append("}");
                    if (count < keywordFrequencyMap.size()) {
                        jsonOutput.append(", ");
                    }
                    count++;
                }
                jsonOutput.append("] }");
                return jsonOutput.toString();
            } else {
                return "{ \"message\": \"Nothing found related to your search! Try something else related to a gym and fitness\" }";
            }



        }
        return null;
    }

    @Override
    public String ftDetails(String location) {
        return dealFinderAPIs.webScraperForFT(location);
    }

    @Override
    public String glDetails(String location) {
        return dealFinderAPIs.webScraperForGL(location);
    }

    @Override
    public String pfDetails(String location) {
        return dealFinderAPIs.webScraperForPF(location);
    }

    public List<Map> bestDeals() {
        List<Map> allDeals = dealFinderAPIs.bestDeals();

        List<Map> top3Deals = allDeals.stream()
                .sorted(Comparator.comparingInt(deal -> (int) getPriceFromDeal((String) ((Map) deal).get("price"))))
                .sorted(Comparator.comparingInt(deal -> ((List<String>) ((Map) deal).get("features")).size()).reversed())
                .limit(3)
                .collect(Collectors.toList());

        return top3Deals; // or return top3Deals if needed
    }

    @Override
    public String locationsAvailable(String location) throws InterruptedException {
        return dealFinderAPIs.locationsAvailable(location);
    }

    private double getPriceFromDeal(String priceString) {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(priceString);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        }
        return Double.MAX_VALUE; // Return max value if price cannot be parsed
    }
}