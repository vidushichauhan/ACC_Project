package org.fitness.controller;

import org.fitness.services.FitnessService;
import org.fitness.utilities.DealFinder;
import org.fitness.utilities.HtmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FitnessController {

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
    @GetMapping("/manualSearchList/{location}")//checking users data manually
    public List<String> manualSearchList(@PathVariable("location") String location){
        return fitnessService.manualSearchList(location);
    }
    //web crawling
    @RequestMapping("/webScrapping/{location}")
    public void webScrapper(@PathVariable("location") String location){
        fitnessService.webScrapper(location);
    }


    //html parser
    @RequestMapping("/parsing")
    public void invertedIndexing(){
        fitnessService.parseWebsite();
    }




}
