package org.fitness.controller;

import org.fitness.services.FitnessService;
import org.fitness.utilities.DealFinder;
import org.fitness.utilities.HtmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/webScrapping/{location}")
    public void webScrapper(@PathVariable("location") String location){
        fitnessService.webScrapper(location);
    }

    //html parser, spell checking, web crawling
    @RequestMapping("/parsing")
    public void parsing(){
        fitnessService.parseWebsite();
    }

    //inverted indexing, page ranking, regex, frequency count
    @RequestMapping("/invertedIndexing")
    public void invertedIndexing(@RequestBody  Map filterParams){
        fitnessService.invertedIndexing(filterParams);
    }




}
