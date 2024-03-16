package org.fitness.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Utilities {

    public static Hashtable<String, Integer> citySrhCount;
    public static final String citySearchFile = "/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/CitySearchHistory.txt";

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

        List<CitySearchFrqPair> srchdCityList = new ArrayList<>();
        citySrhCount = fileToHashmap(citySearchFile);
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

            String formattedString = (i + 1) + ". " + cityName + "\t\tsearched " + frequency + " times";

            // Using the city name as the key and the formatted string as the value
            citySearchFrequencyMap.put(cityName, formattedString);
        }

        // Now, if you want to print the contents of the map, you can do so like this:
        for (String cityName : citySearchFrequencyMap.keySet()) {
            System.out.println(citySearchFrequencyMap.get(cityName));
        }

        return citySearchFrequencyMap;
    }

    public List<String> proceedWithManualInput(String locationIn) {

        if (locationIn.charAt(locationIn.length() - 1) == '*') {
            locationIn = locationIn.substring(0, locationIn.length() - 1);
            // check if it is a valid string
            WordCompletion wordCompletion = new WordCompletion("/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/Cities.txt");
            List<String> cmpltdWordList = wordCompletion.findWordsWithPrefix(locationIn);

            return cmpltdWordList;
        } else { // spell checking logic when input is without *
               /* while (!locationIn.matches("[a-zA-Z0-9.\\-'* ]+")) {
                    System.out.println();
                    //locationIn = scn.nextLine();
                    scn.nextLine();   // clear the buffer
                    return proceedWithManualInput();
                }*/
            SpellChecker spellChecker = new SpellChecker("/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/Files/Cities.txt");
            if (spellChecker.searchInTrie(locationIn)) {
                System.out.println("Inside if block"); // Add print statement to check if block execution
                System.out.println("LocationIn: " + locationIn + ", searchResult: " + spellChecker.searchInTrie(locationIn));
                List<String> list = new ArrayList<>();
                list.add(locationIn);
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
                    return list;
                        /*while (true) {
                            printOnConsole(cmd, "Does the list contain the city you want to search? (y/n)");
                            String confirmation = scn.nextLine();
                            if (confirmation.equals("y") || confirmation.equals("Y")) {
                                printOnConsole(cmd, "Please enter the corrosponding number to proceed");
                                byte choice = 0;
                                try {
                                    choice = scn.nextByte();
                                    scn.nextLine();  // clearing buffer
                                }
                                catch (InputMismatchException ex) {
                                    printOnConsole(msg, "Invalid input! Please try again");
                                    scn.nextLine();   // to clear the buffer and avoid infinite loop.
                                    return proceedWithManualInput();
                                }
                                if (choice > 0 && choice <= suggestions.size()) {
                                    return suggestions.get(choice-1);
                                }
                                else {
                                    printOnConsole(msg, "Invalid input! Please try again.");
                                    return proceedWithHistory();
                                }
                            }
                            else if (confirmation.equals("n") || confirmation.equals("N")) {
                                printOnConsole(msg, "Please try again!");
                                return proceedWithManualInput();
                            }
                            else {
                                printOnConsole(msg, "Invalid input! Please try again.");
                            }
                        }
                    }
                }
            }*/
                }
       /* else if (inChoice == 2) {  // starting from beginning.
            return getLocationInput();
        }
        else {
            printOnConsole(msg, "Invalid input! Please try again.");
            return proceedWithManualInput();
        }*/
            }
        }
        return null;
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


