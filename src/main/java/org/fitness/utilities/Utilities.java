package org.fitness.utilities;

import com.google.gson.Gson;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utilities {


    public static final String citySearchFile = "/Users/rohansethi/Downloads/ACC_Project/src/main/resources/Files/CitySearchHistory.txt";
    private static final String URL_REGEX = "(\\b(https?|ftp|file)://)?[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    Gson gson = new Gson();
    public Hashtable<String, Integer> fileToHashmap(String filePath) {
        Hashtable<String, Integer> srhCount = new Hashtable<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String city = line.trim();

                if (srhCount.containsKey(city)) {  // if city has been added already.
                    srhCount.put(city, srhCount.get(city) + 1);
                } else {
                    srhCount.put(city, 1);
                }
            }
            return srhCount;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }


    public Map<String, String> historySearchList() {
        Hashtable<String, Integer> citySrhCount;
        citySrhCount = fileToHashmap(citySearchFile);
        List<CitySearchFrqPair> srchdCityList = new ArrayList<>();
        // preparing list of CitySearchFrqPair from the hashtable.
        for (String key : Collections.list(citySrhCount.keys())) {
            srchdCityList.add(new CitySearchFrqPair(key, citySrhCount.get(key)));
        }

        // sorting the list in the descending order.
        Collections.sort(srchdCityList);

        // showing list of cities with its search frequency.
        Map<String, String> citySearchFrequencyMap = new HashMap<>();

        for (int i = 0; i < srchdCityList.size(); i++) {
            String cityName = srchdCityList.get(i).cityName;
            int frequency = srchdCityList.get(i).frq;

            String formattedString = String.valueOf(frequency) ;

            // Using the city name as the key and the formatted string as the value
            citySearchFrequencyMap.put(cityName, formattedString);
        }

        // Now, if you want to print the contents of the map, you can do so like this:
        for (String cityName : citySearchFrequencyMap.keySet()) {
            System.out.println(citySearchFrequencyMap.get(cityName));
        }

        return citySearchFrequencyMap;
    }

    public boolean isUrlValid(String url) {
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
    public List<String> proceedWithManualInput(String locationIn) {
        String regex = ".*[^a-zA-Z]+.*";
        ArrayList caseHandlinglist = new ArrayList();
        if (locationIn.matches(regex)) {
            caseHandlinglist.add("please type alphabet for search");
            return caseHandlinglist;
        }
        Hashtable<String, Integer> citySrhCount;
        citySrhCount = fileToHashmap(citySearchFile);

            locationIn = locationIn.substring(0, locationIn.length() - 1);
            // check if it is a valid string
            WordCompletion wordCompletion = new WordCompletion("/Users/rohansethi/Downloads/ACC_Project/src/main/resources/Files/Cities.txt");
            List<String> cmpltdWordList = wordCompletion.findWordsWithPrefix(locationIn);
            if(cmpltdWordList.size()>0){
            return cmpltdWordList;}
            else{
            SpellChecker spellChecker = new SpellChecker("/Users/rohansethi/Downloads/ACC_Project/src/main/resources/Files/Cities.txt");
            if (spellChecker.searchInTrie(locationIn)) {
                System.out.println("LocationIn: " + locationIn + ", searchResult: " + spellChecker.searchInTrie(locationIn));
                List<String> list = new ArrayList<>();
                list.add(locationIn);
                // recording the searched location in the history.
                if (citySrhCount != null && citySrhCount.contains(locationIn)) {  // if city has been added already.
                    citySrhCount.put(locationIn, citySrhCount.get(locationIn)+1);
                }
                else {
                    citySrhCount.put(locationIn, 1);
                }

                // add searched city into history file
                try (BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(citySearchFile, true))) {
                    BufferWriter.write(locationIn);
                    BufferWriter.newLine(); // new line
                    BufferWriter.close();
                }
                catch (IOException e) {
                   System.out.println("Something went wrong while interacting with file");
                }
                return list;
            }  else {
                List<String> suggestions = spellChecker.suggestCorrectionsCNC(locationIn);
                if (suggestions.isEmpty()) {
                    ArrayList list = new ArrayList();
                    list.add("No city found in the record matching to the given input. Please try again");
                } else {
                    // printOnConsole(msg, "Did you mean: ");
                    ArrayList list = new ArrayList();
                    for (int i = 0; i < suggestions.size(); i++) {
                        list.add(suggestions.get(i));
                    }

                    if(list.size()==0){
                        caseHandlinglist.add("you are typing wrong..!!");
                        return caseHandlinglist;
                    }
                    return list;

                }
            }
        }
        caseHandlinglist.add("you are typing wrong..!!");
        return caseHandlinglist;
        //return null;
    }
}

    class CitySearchFrqPair implements Comparable<CitySearchFrqPair> {
        String cityName;
        int frq;

        public CitySearchFrqPair(String cityName, int frq) {
            this.cityName = cityName;
            this.frq = frq;
        }

        @Override
        public int compareTo(CitySearchFrqPair o) {
            return Integer.compare(o.frq, this.frq);
        }
    }


