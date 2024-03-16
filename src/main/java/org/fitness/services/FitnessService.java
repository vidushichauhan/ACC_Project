package org.fitness.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface FitnessService {


    public Map<String, String> historySearchList();
    public List<String> manualSearchList(String location);

}
