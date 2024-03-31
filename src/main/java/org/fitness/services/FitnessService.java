package org.fitness.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FitnessService {


    public Map<String, String> historySearchList();
    public List<String> manualSearchList(String location);

    public void webScrapper(String location);


    public void parseWebsite();

    public String invertedIndexing(Map filterParams);

    public String  ftDetails(String location);

    String glDetails(String location);

    String pfDetails(String location);

    List<Map> bestDeals();

    String locationsAvailable(String location) throws InterruptedException;
}
