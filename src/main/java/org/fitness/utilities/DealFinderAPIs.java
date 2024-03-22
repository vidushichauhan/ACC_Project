package org.fitness.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClients;
import org.fitness.classes.FitnessWorldMembership;
import org.fitness.classes.GoodLifeMembership;
import org.fitness.classes.PlanetFitnessMembership;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;



import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DealFinderAPIs {
    private static final String CHROME_DRIVER_PATH = "/Users/vidushichauhan/IdeaProjects/FitnessTrack_Pro/src/main/resources/WebDriver/msedgedriver";
    private static final String FITNESS_WORLD_URL = "https://www.fitnessworld.ca/explore-memberships/";
    private static final String GOODLIFE_FITNESS_URL = "https://www.goodlifefitness.com/membership.html";
    private static final String PLATNET_FITNESS_URL = "https://www.planetfitness.ca/";
    MongoOperations mongoOps = new MongoTemplate(MongoClients.create(), "ACC_PROJECT");

    public String  webScraperForFT(String cityName) {
        System.setProperty("webdriver.edge.driver", CHROME_DRIVER_PATH);
        WebDriver edgeWebDriver = new EdgeDriver();
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
        }
        catch (Exception e) {
            System.out.println("Element not found within the specified time.");
        }
try{
        WebElement parentElement = edgeWebDriver.findElement(By.className("club-container"));;

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
                FitnessWorldMembership obj = new FitnessWorldMembership(membershipType,"Fitness World",price, benefits);
                mongoOps.save(obj,"plans");
                memberships.add(new FitnessWorldMembership(membershipType,"Fitness World",price, benefits));
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
            System.setProperty("webdriver.edge.driver", CHROME_DRIVER_PATH);
            WebDriver edgeWebDriver = new EdgeDriver();
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
                glData_Premium.put("price", "$"+priceList.get(0)+"/month");
                glData_Ultimate.put("price","$"+priceList.get(1)+"/month");
                glData_Performance.put("price", "$"+priceList.get(2)+"/month");

                ObjectMapper objectMapper = new ObjectMapper();
                GoodLifeMembership gl1=objectMapper.convertValue(glData_Premium, GoodLifeMembership.class);
                mongoOps.save(gl1,"plans");
                GoodLifeMembership gl2=objectMapper.convertValue(glData_Ultimate, GoodLifeMembership.class);
                mongoOps.save(gl2,"plans");
                GoodLifeMembership gl3=objectMapper.convertValue(glData_Performance, GoodLifeMembership.class);
                mongoOps.save(gl3,"plans");
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
        System.setProperty("webdriver.edge.driver", CHROME_DRIVER_PATH);
        WebDriver edgeWebDriver = new EdgeDriver();
        JavascriptExecutor js = (JavascriptExecutor) edgeWebDriver;

        edgeWebDriver.manage().window().maximize();
        edgeWebDriver.get(PLATNET_FITNESS_URL);
        WebDriverWait wait = new WebDriverWait(edgeWebDriver, Duration.ofSeconds(50));
        try{
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
            };
            List<WebElement> featuresButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div[6]/div/div/div/div/div/div[2]/div/div[6]/button")));

// Iterate over each element and click it
            for (WebElement button : featuresButtons) {
                button.click();
            }
            //After getting to membership details page
            String membershipType1 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[1]/div/div/div[2]/div/div[1]/p/span")).getText();
            String membershipType2 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[2]/div/div/div[2]/div/div[1]/p/span")).getText();
            String price1 =  edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[1]/div/div/div[2]/div/p[1]")).getText();
            String price2 = edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[2]/div/div/div[2]/div/p[1]")).getText();
            String features1= edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[1]/div/div/div[2]/div/div[4]/ul")).getText();
            String[] features1List = features1.split("\\n");
            String features2= edgeWebDriver.findElement(By.xpath("/html/body/div[1]/div[6]/div/div/div[2]/div/div/div[2]/div/div[4]/ul")).getText();
            String[] features2List = features2.split("\\n");
            PlanetFitnessMembership plmembershipType1 = new PlanetFitnessMembership(membershipType1, "Planet_Fitness",features1List, price1);
            PlanetFitnessMembership plmembershipType2 = new PlanetFitnessMembership(membershipType2, "Planet_Fitness",features2List, price2);
            List<PlanetFitnessMembership> membershipList = new ArrayList<>();
            mongoOps.save(plmembershipType1,"plans");
            mongoOps.save(plmembershipType2,"plans");
            membershipList.add(plmembershipType1);
            membershipList.add(plmembershipType2);
            Gson gson = new Gson();
            String json = gson.toJson(membershipList);
            //mongoOps.save(json);
            return json;
        }
        catch(Exception e){
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


    private boolean isElementPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            //checks whether element is present on page.
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }


    public List<Map> bestDeals() {

        return mongoOps.findAll(Map.class,"plans");
    }


}
