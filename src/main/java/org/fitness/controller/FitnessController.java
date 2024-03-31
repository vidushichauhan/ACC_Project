package org.fitness.controller;

import org.fitness.services.FitnessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class FitnessController {

    String citySearchFile = "/Users/rohansethi/Downloads/ACC_Project/src/main/resources/Files/CitySearchHistory.txt";

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

    //WordCompletion, SpellChecker
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
    public String invertedIndexing(@RequestBody Map filterParams) {
        return fitnessService.invertedIndexing(filterParams);
    }

    @RequestMapping("/getFTDetails/{location}")
    public String ftDetails(@PathVariable("location") String location){
        try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(citySearchFile, true))) {
            BufferWriter.write(location);
            BufferWriter.newLine(); // No need to close here, try-with-resources handles it.
        } catch (IOException e) {
            e.printStackTrace(); // Log stack trace for debugging.
        }
        return fitnessService.ftDetails(location);
    }
    @RequestMapping("/getGlOutput/{location}")
    public String glDetails(@PathVariable("location") String location){
        try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(citySearchFile, true))) {
            BufferWriter.write(location);
            BufferWriter.newLine(); // No need to close here, try-with-resources handles it.
        } catch (IOException e) {
            e.printStackTrace(); // Log stack trace for debugging.
        }

        return fitnessService.glDetails(location);
    }

    @RequestMapping("/getPFOutput/{location}")
    public String pfDetails(@PathVariable("location") String location){
        try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(citySearchFile, true))) {
            BufferWriter.write(location);
            BufferWriter.newLine(); // No need to close here, try-with-resources handles it.
        } catch (IOException e) {
            e.printStackTrace(); // Log stack trace for debugging.
        }

        return fitnessService.pfDetails(location);
    }

    @RequestMapping("/bestDeals")
    public List<Map> bestDeals(){
        return fitnessService.bestDeals();
    }

    @RequestMapping("/availableLocation/{location}")
    public String locationAvailable(@PathVariable("location") String location) throws InterruptedException {
        return fitnessService.locationsAvailable(location);
    }


}
