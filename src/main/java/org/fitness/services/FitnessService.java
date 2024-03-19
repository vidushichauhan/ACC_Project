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

    public void invertedIndexing(Map filterParams);
}
