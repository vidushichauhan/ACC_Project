package org.fitness.serviceImpl;

import org.fitness.services.FitnessService;
import org.fitness.utilities.DealFinder;
import org.fitness.utilities.HtmlParser;
import org.fitness.utilities.Utilities;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class FitnessServiceImpl implements FitnessService {
    private static DealFinder dealFinder = new DealFinder();
    Utilities utl =new Utilities();
    private static HtmlParser parser = new HtmlParser();
    public static final String citySearchFile = "/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/CitySearchHistory.txt";
    public static String outputFolderName ="/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/ParsedFiles";

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


}
