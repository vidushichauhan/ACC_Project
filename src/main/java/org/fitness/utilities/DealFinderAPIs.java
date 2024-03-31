package org.fitness.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClients;
import org.fitness.classes.FitnessWorldMembership;
import org.fitness.classes.GoodLifeMembership;
import org.fitness.classes.LocationDetails;
import org.fitness.classes.PlanetFitnessMembership;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DealFinderAPIs {
    private static final String CHROME_DRIVER_PATH = "/Users/vidushichauhan/Desktop/Course SEM1/Advance Computing Concepts/ACC_Project/src/main/resources/chromedriver";
    private static final String FITNESS_WORLD_URL = "https://www.fitnessworld.ca/explore-memberships/";
    private static final String GOODLIFE_FITNESS_URL = "https://www.goodlifefitness.com/membership.html";
    private static final String PLATNET_FITNESS_URL = "https://www.planetfitness.ca/";
    MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "ACC_PROJECT");

    public String webScraperForFT(String cityName) {
        System.setProperty("webdriver.chrome.driver", "/Users/rohansethi/Downloads/Assignment_3/chromedriver");
        WebDriver edgeWebDriver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) edgeWebDriver;

        edgeWebDriver.manage().window().maximize();
        edgeWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        edgeWebDriver.get(FITNESS_WORLD_URL);

        WebDriverWait wait = new WebDriverWait(edgeWebDriver, Duration.ofSeconds(60));

        By elementLocator = By.className("close-popup");

        try {
            // Wait until the element is present on the page
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));

            By closeButtonAvailability = By.className("close-popup");
            //Email Signup popup is being selected

            if (isElementPresent(edgeWebDriver, closeButtonAvailability)) {
                //condition checks whether the button is present on screen or not
                WebElement closeButton = edgeWebDriver.findElement(By.className("close-popup"));
                //if button is present on screen than it clicked and pop is closed
                closeButton.click();
                System.out.println("Email Signup popup Cancelled clicked successfully!");
            }
        } catch (Exception e) {
            System.out.println("Element not found within the specified time.");
        }
        try {
            WebElement parentElement = edgeWebDriver.findElement(By.className("club-container"));

            // Scroll to the specific element
            js.executeScript("arguments[0].scrollIntoView(true);", parentElement);

            //List < WebElement > maincontainers = edgeWebDriver.findElements(By.className("club-option__benefits"));
            List<WebElement> maincontainers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("club-option__benefits")));

            List<FitnessWorldMembership> memberships = new ArrayList<>();
            for (WebElement childEle : maincontainers) {
                String title = childEle.findElement(By.tagName("h3")).getText().trim().replaceAll("BI-WEEKLY", "Per month").replaceAll("$", "");
                List<WebElement> texts = childEle.findElements(By.tagName("span"));
                List<String> benefits = new ArrayList<>();
                for (WebElement insideText : texts) {
                    benefits.add(insideText.getText().trim());
                }
                String[] parts = title.split("\\$");

                String membershipType = parts[0].trim();
                String price = "$" + parts[1].trim();

                FitnessWorldMembership obj = new FitnessWorldMembership(membershipType, "Fitness World", price, benefits);
                mongoOps.save(obj, "plans");
                memberships.add(new FitnessWorldMembership(membershipType, "Fitness World", price, benefits));
            }


            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String json = gson.toJson(memberships);

            edgeWebDriver.quit();

            return json;
        } catch (Exception e) {
            System.out.println("Error occurred while scraping Fitness World website:");
            e.printStackTrace();
            edgeWebDriver.quit();
            return null;
        }
    }

    public String webScraperForGL(String location) {
        // Define the system property to specify the path of ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "/Users/rohansethi/Downloads/Assignment_3/chromedriver");

        // Initialize a new instance of ChromeDriver to control the Chrome browser
        WebDriver edgeWebDriver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) edgeWebDriver;

        edgeWebDriver.manage().window().maximize();
        edgeWebDriver.get(GOODLIFE_FITNESS_URL);
        WebDriverWait wait = new WebDriverWait(edgeWebDriver, Duration.ofSeconds(50));
        try {
            WebElement performanceLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for='performance-membership']")));
            WebElement ultimateLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for='ultimate-membership']")));
            WebElement premiumLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for='all-clubs-membership']")));

            js.executeScript("arguments[0].scrollIntoView(true);", performanceLabel);

            WebElement GLMainContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("c-membership-types-container")));
            List<WebElement> prices = GLMainContainer.findElements(By.className("c-card--membership-price__dollar"));
            List<WebElement> features = edgeWebDriver.findElements(By.cssSelector("tr.c-pricing-mobile__row"));

            // Create a map to hold the membership types
            Map<String, Object> glData = new LinkedHashMap<>();
            List<Map<String, Object>> membershipTypes = new ArrayList<>();
            Map<String, Object> glData_Premium = new LinkedHashMap<>();
            glData_Premium.put("membershipType", premiumLabel.getText());
            Map<String, Object> glData_Ultimate = new LinkedHashMap<>();
            glData_Ultimate.put("membershipType", ultimateLabel.getText());
            Map<String, Object> glData_Performance = new LinkedHashMap<>();
            glData_Performance.put("membershipType", performanceLabel.getText());
            List<String> premiumFeatures = new ArrayList<>();
            List<String> ultimateFeatures = new ArrayList<>();
            List<String> performanceFeature = new ArrayList<>();

            for (WebElement feature : features) {
                List<WebElement> unhighlightedCells = feature.findElements(By.cssSelector("td.c-pricing-mobile__available"));
                WebElement highlightedCell = feature.findElement(By.cssSelector("td.col.c-pricing-mobile__available.highlight"));

                Map<String, Object> membershipType = new LinkedHashMap<>();

                // Extract unhighlighted features from the respective cells
                for (WebElement cell : unhighlightedCells.get(4).findElements(By.tagName("li"))) {
                    String text = cell.getText().trim();
                    if (!text.isEmpty()) {
                        premiumFeatures.add(text);
                    }
                }

                for (WebElement cell : unhighlightedCells.get(6).findElements(By.tagName("li"))) {
                    String text = cell.getText().trim();
                    if (!text.isEmpty()) {
                        ultimateFeatures.add(text);
                    }
                }

                // Extract highlighted features from the highlighted cell
                for (WebElement cell : highlightedCell.findElements(By.tagName("li"))) {
                    String text = cell.getText().trim();
                    if (!text.isEmpty()) {
                        performanceFeature.add(text);
                    }
                }
            }
            glData_Premium.put("gymName", "GOODLIFE_FITNESS");
            glData_Ultimate.put("gymName", "GOODLIFE_FITNESS");
            glData_Performance.put("gymName", "GOODLIFE_FITNESS");
            glData_Premium.put("features", premiumFeatures);
            glData_Ultimate.put("features", ultimateFeatures);
            glData_Performance.put("features", performanceFeature);
            List<Integer> priceList = getPrices(prices);
            glData_Premium.put("price", "$" + priceList.get(0) + "/month");
            glData_Ultimate.put("price", "$" + priceList.get(1) + "/month");
            glData_Performance.put("price", "$" + priceList.get(2) + "/month");

            ObjectMapper objectMapper = new ObjectMapper();
            GoodLifeMembership gl1 = objectMapper.convertValue(glData_Premium, GoodLifeMembership.class);
            mongoOps.save(gl1, "plans");
            GoodLifeMembership gl2 = objectMapper.convertValue(glData_Ultimate, GoodLifeMembership.class);
            mongoOps.save(gl2, "plans");
            GoodLifeMembership gl3 = objectMapper.convertValue(glData_Performance, GoodLifeMembership.class);
            mongoOps.save(gl3, "plans");
            membershipTypes.add(glData_Premium);
            membershipTypes.add(glData_Ultimate);
            membershipTypes.add(glData_Performance);

            // Convert the map to JSON using Gson
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(membershipTypes);

            edgeWebDriver.quit(); // Quit the WebDriver

            return json; // Return the JSON string
        } catch (Exception e) {
            e.printStackTrace();
            edgeWebDriver.quit();
            return null;
        }


    }


    public String webScraperForPF(String location) {
        System.setProperty("webdriver.chrome.driver", "/Users/rohansethi/Downloads/Assignment_3/chromedriver");
        WebDriver edgeWebDriver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) edgeWebDriver;

        edgeWebDriver.manage().window().maximize();
        edgeWebDriver.get(PLATNET_FITNESS_URL);
        WebDriverWait wait = new WebDriverWait(edgeWebDriver, Duration.ofSeconds(50));
        try {
            WebElement inputElement = edgeWebDriver.findElement(By.xpath("/html/body/div[3]/div/div/div[1]/div[4]/div/form/div/div/div/input"));
            inputElement.sendKeys(location);
            WebElement joinUsButton = edgeWebDriver.findElement(By.xpath("/html/body/div[3]/div/div/div[1]/div[4]/div/form/div/button"));
            // Click the "Join Us" button
            joinUsButton.click();

            List<WebElement> placeElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[2]/div[2]")));
            List<Map> places = new ArrayList<>();
            String locationName;
            for (WebElement placeElement : placeElements) {
                // Extract information for each place
                locationName = placeElement.findElement(By.cssSelector("b.text-lg.tracking-\\[\\-0\\.015em\\].text-common-black")).getText();
                String detailsLink = placeElement.findElement(By.cssSelector("a[href*='/offers']")).getAttribute("href");
                // Navigate to the URL specified in the href attribute
                edgeWebDriver.get(detailsLink);
                break;
            }
            ;
            List<WebElement> featuresButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div[6]/div/div/div/div/div/div[2]/div/div[6]/button")));

// Iterate over each element and click it
            for (WebElement button : featuresButtons) {
                button.click();
            }
            //After getting to membership details page
            String membershipType1 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[1]/div/div/div[2]/div/div[1]/p/span")).getText();
            String membershipType2 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[2]/div/div/div[2]/div/div[1]/p/span")).getText();
            String price1 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[1]/div/div/div[2]/div/p[1]")).getText();
            String price2 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[2]/div/div/div[2]/div/p[1]")).getText();
            String features1 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[1]/div/div/div[2]/div/div[4]/ul")).getText();
            String[] features1List = features1.split("\\n");
            String features2 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[2]/div/div/div[2]/div/div[4]/ul")).getText();
            String[] features2List = features2.split("\\n");
            PlanetFitnessMembership plmembershipType1 = new PlanetFitnessMembership(membershipType1, "Planet_Fitness", features1List, price1);
            PlanetFitnessMembership plmembershipType2 = new PlanetFitnessMembership(membershipType2, "Planet_Fitness", features2List, price2);
            List<PlanetFitnessMembership> membershipList = new ArrayList<>();
            mongoOps.save(plmembershipType1, "plans");
            mongoOps.save(plmembershipType2, "plans");
            membershipList.add(plmembershipType1);
            membershipList.add(plmembershipType2);
            Gson gson = new Gson();
            String json = gson.toJson(membershipList);
            //mongoOps.save(json);
            edgeWebDriver.quit();
            return json;
        } catch (Exception e) {
            System.out.println(e);
        }

        // edgeWebDriver.quit(); // Quit the WebDriver
        return "Something went wrong";
    }

    private List<Integer> getPrices(List<WebElement> prices) {
        List<Integer> priceList = new ArrayList<>();
        for (WebElement price : prices) {
            String priceText = price.getText().trim();
            if (!priceText.isEmpty()) {
                try {
                    int priceValue = Integer.parseInt(priceText);
                    priceList.add(priceValue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Handle the exception, maybe log it or skip this price
                }
            }
        }
        return priceList;
    }


    private String addressFL(String input, WebDriver edgeWebDriver, JavascriptExecutor js) throws InterruptedException {
        String fullAddress = null;
        WebElement searchElement = edgeWebDriver.findElement(By.id("address"));
        // Scroll to the specific element
        js.executeScript("arguments[0].scrollIntoView(true);", searchElement);
        //String input="vancouver";
        searchElement.sendKeys(input);

        WebElement FtLocationContainer = edgeWebDriver.findElement(By.className("join__locations"));
        List<WebElement> Fit4LessLocations = FtLocationContainer.findElements(By.tagName("a"));

        Queue<String> ftLinkstoVisit = new LinkedList<>();
        int linksCount = 0;
        for (WebElement ele : Fit4LessLocations) {
            String link = ele.getAttribute("href");
            if (linksCount < 3) {
                ftLinkstoVisit.add(link);
                linksCount++;
            }
        }
        Pattern phonePattern = Pattern.compile("\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})");

        while (!ftLinkstoVisit.isEmpty()) {
            edgeWebDriver.get(ftLinkstoVisit.poll());
            WebElement ftTitle = edgeWebDriver.findElement(By.className("title"));
            WebElement ftAddress = edgeWebDriver.findElement(By.className("address"));
            WebElement ftPhoneElement = edgeWebDriver.findElement(By.className("phone"));
            String phoneText = ftPhoneElement.getText();

            // Apply regex to phone number text
            Matcher phoneMatcher = phonePattern.matcher(phoneText);
            String formattedPhone = "";
            if (phoneMatcher.find()) {
                // Assuming you want to format the phone number in a specific way
                formattedPhone = phoneMatcher.group(1) + "-" + phoneMatcher.group(2) + "-" + phoneMatcher.group(3);
            }

            fullAddress = "Title: " + ftTitle.getText() + ", Address: " + ftAddress.getText() + ", Phone: " + formattedPhone;
        }

        edgeWebDriver.get(FITNESS_WORLD_URL);
        Thread.sleep(1000);
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "No address found for this location. But the above price is for all the locations.";
        }
        return fullAddress;

    }

    private boolean isElementPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            //checks whether element is present on page.
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    public List<Map> bestDeals() {

        return mongoOps.findAll(Map.class, "plans");
    }


    public String locationsAvailable(String location) throws InterruptedException {
        List<LocationDetails> listOfGymsWithAddress = new ArrayList<>();
        System.setProperty("webdriver.chrome.edgeWebDriver", "/Users/rohansethi/Downloads/Assignment_3/chromedriver");
        WebDriver edgeWebDriver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) edgeWebDriver;
        WebDriverWait wait = new WebDriverWait(edgeWebDriver, Duration.ofSeconds(60));

        edgeWebDriver.manage().window().maximize();
        edgeWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        edgeWebDriver.get("https://www.fitnessworld.ca/join/");

        WebElement searchElement = edgeWebDriver.findElement(By.id("address"));
        // Scroll to the specific element
        js.executeScript("arguments[0].scrollIntoView(true);", searchElement);
        //String input="vancouver";
        searchElement.sendKeys(location);
        Thread.sleep(10000);
        searchElement.sendKeys(Keys.ENTER);
        Thread.sleep(10000);
        WebElement FtLocationContainer = edgeWebDriver.findElement(By.className("join__locations"));
        List<WebElement> Fit4LessLocations = FtLocationContainer.findElements(By.tagName("a"));
        String fullAddress = null;
        Queue<String> ftLinkstoVisit = new LinkedList<>();
        int linksCount = 0;
        for (WebElement ele : Fit4LessLocations) {
            String link = ele.getAttribute("href");
            if (linksCount < 3) {
                ftLinkstoVisit.add(link);
                linksCount++;
            }
        }
        while (!ftLinkstoVisit.isEmpty()) {

            edgeWebDriver.get(ftLinkstoVisit.poll());
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());

                // Accept the alert (click OK)
                alert.accept();
                System.out.println("Alert was present and accepted.");
            } catch (TimeoutException e) {
                // Alert didn't appear within the wait time
                System.out.println("No alert appeared within the wait time.");
            } catch (NoAlertPresentException e) {
                // In case alert is not present at all
                System.out.println("No alert is present.");
            }
            // Fetch the entire HTML source of the page
            String htmlContent = edgeWebDriver.getPageSource();

// Regex pattern to find the address
            Pattern addressPattern = Pattern.compile("<p class=\"address\">\\s*<b>(.+?)</b>");
            Matcher addressMatcher = addressPattern.matcher(htmlContent);
            String address = null;
            if (addressMatcher.find()) {
                address = addressMatcher.group(1);
                System.out.println("Address: " + address);
            } else {
                System.out.println("Address not found.");
            }

// Regex pattern to find the phone number
            Pattern phonePattern = Pattern.compile("<a href=\"tel:(.+?)\" class=\"phone\">");
            Matcher phoneMatcher = phonePattern.matcher(htmlContent);
            String phone = null;
            if (phoneMatcher.find()) {
                phone = phoneMatcher.group(1);
                System.out.println("Phone: " + phone);
            } else {
                System.out.println("Phone number not found.");
            }
            LocationDetails newLocation = new LocationDetails("Fitness World", address, phone);
            listOfGymsWithAddress.add(newLocation);
        }

        ///-------------------------------------------------------------------------------------------------
        System.out.println("GoodLife Fitness Web Browser Scraping");
        edgeWebDriver.get("https://www.goodlifefitness.com/clubs.html");
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            // Accept the alert (click OK)
            alert.accept();
            System.out.println("Alert was present and accepted.");
        } catch (TimeoutException e) {
            // Alert didn't appear within the wait time
            System.out.println("No alert appeared within the wait time.");
        } catch (NoAlertPresentException e) {
            // In case alert is not present at all
            System.out.println("No alert is present.");
        }

        WebElement LocationSearchInput = edgeWebDriver.findElement(By.id("club-search"));
        LocationSearchInput.sendKeys(location);



        WebElement LocationSearchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/div[2]/div/div[2]/div/div[3]/div/div[4]/div[1]/div[1]/div/div[2]/button")));
        js.executeScript("arguments[0].scrollIntoView(true);", LocationSearchButton);
        Thread.sleep(10000); // Not recommended for production use; use for testing purposes
        LocationSearchButton.click();



        By GLLocationAvailability = By.id("js-card-list__0");
//Email Signup popup is being selected

        if (isElementPresent(edgeWebDriver, GLLocationAvailability)) {
            try {
                // Sleep for 3 seconds (3000 milliseconds)
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // Handle the exception if necessary
                e.printStackTrace();
            }
            //condition checks whether the button is present on screen or not
            WebElement GLLocation1 = edgeWebDriver.findElement(By.id("js-card-list__0"));
            WebElement GLLocation2 = edgeWebDriver.findElement(By.id("js-card-list__1"));
            WebElement GLLocation3 = edgeWebDriver.findElement(By.id("js-card-list__2"));

            String Address1 = GLLocation1.findElement(By.className("c-card__contact")).getText();
            String Address2 = GLLocation2.findElement(By.className("c-card__contact")).getText();
            String Address3 = GLLocation3.findElement(By.className("c-card__contact")).getText();

            String Phone1 = GLLocation1.findElement(By.className("c-card__phone")).getText();
            String Phone2 = GLLocation2.findElement(By.className("c-card__phone")).getText();
            String Phone3 = GLLocation3.findElement(By.className("c-card__phone")).getText();

            Pattern pattern = Pattern.compile("(.+?)\\n");
            Matcher matcher1 = pattern.matcher(Address1);

            if (matcher1.find()) {
                Address1 = matcher1.group(1);

            } else {
                System.out.println("Pattern not found in the input string.");
            }

            Matcher matcher2 = pattern.matcher(Address2);
            if (matcher2.find()) {
                Address2 = matcher2.group(1);
            } else {
                System.out.println("Pattern not found in the input string.");
            }

            Matcher matcher3 = pattern.matcher(Address3);
            if (matcher3.find()) {
                Address3 = matcher3.group(1);
            } else {
                System.out.println("Pattern not found in the input string.");
            }
            LocationDetails newLocation = new LocationDetails("GoodLife Fitness", Address1, Phone1);
            listOfGymsWithAddress.add(newLocation);
            LocationDetails newLocation2 = new LocationDetails("GoodLife Fitness", Address2, Phone2);
            listOfGymsWithAddress.add(newLocation2);
            LocationDetails newLocation3 = new LocationDetails("GoodLife Fitness", Address3, Phone3);
            listOfGymsWithAddress.add(newLocation3);
        }

        ///------------------------------------------------------------------------------------------------------
        System.out.println("Planet Fitness Web Browser Scraping");
        edgeWebDriver.get("https://www.planetfitness.ca/gyms/");
         // Wait for the search input element to be visible and interactable
        WebElement LocationSearchInput1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[1]/div/form/input")));
        js.executeScript("arguments[0].value='" + location + "';", LocationSearchInput1);
        // Scroll to the specific element
        //LocationSearchInput1.sendKeys(location);
        LocationSearchInput1.sendKeys(Keys.ENTER);

        WebElement PFLocationContainer = edgeWebDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div"));
        List<WebElement> Fit4LessLocations1 = PFLocationContainer.findElements(By.tagName("a"));
        String fullAddress1 = null;
        Queue<String> ftLinkstoVisit1 = new LinkedList<>();
        int linksCount1 = 0;
        for (WebElement ele : Fit4LessLocations1) {
            String link = ele.getAttribute("href");
            if (linksCount1 < 3) {
                ftLinkstoVisit1.add(link);
                linksCount1++;
            }
            else{break;}
        }
        while (!ftLinkstoVisit1.isEmpty()) {
            edgeWebDriver.get(ftLinkstoVisit1.poll());

            // Fetch the entire HTML source of the page
            String htmlContent = edgeWebDriver.getPageSource();

// Regex pattern to find the address
            Pattern addressPattern = Pattern.compile("<p class=\"MuiTypography-root club-line MuiTypography-body1 MuiTypography-colorTextSecondary\">(.*?)</p>\\s*<p class=\"MuiTypography-root club-city MuiTypography-body1 MuiTypography-colorTextSecondary\">(.*?)</p>");
            Matcher addressMatcher = addressPattern.matcher(htmlContent);

            Pattern phonePattern = Pattern.compile("<a class=\"MuiTypography-root MuiLink-root MuiLink-underlineAlways MuiTypography-body2 MuiTypography-colorPrimary MuiTypography-gutterBottom\" target=\"_blank\" href=\"tel:(\\d+)\">");
            Matcher phoneMatcher = phonePattern.matcher(htmlContent);

            while (addressMatcher.find() && phoneMatcher.find()) {
                String street = addressMatcher.group(1).trim();
                String cityAndZip = addressMatcher.group(2).trim();
                fullAddress1 = street + ", " + cityAndZip;
                String phone = phoneMatcher.group(1).trim();


                LocationDetails newLocation = new LocationDetails("Planet Fitness", fullAddress1, phone);
                listOfGymsWithAddress.add(newLocation);
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        edgeWebDriver.quit(); // Don't forget to close the browser
        return gson.toJson(listOfGymsWithAddress);
    }

}

