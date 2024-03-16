package org.fitness.controller;

import org.fitness.services.FitnessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FitnessController {
    public static final String citySearchFile = "/Users/vidushichauhan/git/Java_FitFinder/src/main/java/Files/CitySearchHistory.txt";
    @Autowired
    private final FitnessService fitnessService;
    @Autowired
    public FitnessController(FitnessService fitnessService) {
        this.fitnessService = fitnessService;
    }

    @GetMapping("/historySearchList/history") // when user wants to check history city count
    public Map<String, String> historySearchList(){
        return fitnessService.historySearchList();
    }
    @GetMapping("/manualSearchList/{location}")
    public List<String> manualSearchList(@PathVariable("location") String location){
        return fitnessService.manualSearchList(location);
    }




}
