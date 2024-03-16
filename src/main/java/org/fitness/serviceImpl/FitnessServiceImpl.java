package org.fitness.serviceImpl;

import org.fitness.services.FitnessService;
import org.fitness.utilities.Utilities;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FitnessServiceImpl implements FitnessService {
    public static final String citySearchFile = "/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/CitySearchHistory.txt";

    Utilities utl =new Utilities();

    public Map<String, String> historySearchList() {
        // To get list of cities that were searched before
        return utl.historySearchList();
    }

    @Override
    public List<String> manualSearchList(String location) {
        //To get list of cities that we have already in cities List
        return utl.proceedWithManualInput(location);
    }


}
