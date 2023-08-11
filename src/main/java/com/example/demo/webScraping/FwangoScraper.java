package com.example.demo.webScraping;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FwangoScraper {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().driverVersion("114.0.5735.90").setup(); // Setup ChromeDriver automatically
        
        WebDriver driver = new ChromeDriver();
        String url = "https://fwango.io/philadelphia2023";
        driver.manage().window().setSize(new Dimension(1200, 800)); // Set window size
        
        // try {
        //     driver.get("url);
        //     Thread.sleep(2000);
            
        //     // Wait for the page to fully load
        //     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        //     wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            
        //     // Find and print initially loaded team names
        //     List<String> newTeamNames = new ArrayList<>();
        //     List<String> playerNames = new ArrayList<>();
        //     getTeamNames(driver, newTeamNames,"", playerNames);
            
        //     // Scroll and load more content
        //     WebElement container = driver.findElement(By.cssSelector("div.TournamentTeamList__Container-sc-13mkiy8-0.irfjog"));
        //     String lastLoadedTeamName = "";
        //     long startTime = System.currentTimeMillis();
        //     long durationMillis = 6000; // 6 seconds - this is a little too much (even for the largest tournaments), but better safe than sorry
        //     // Make sure to test this duration for richmond

        //     while (System.currentTimeMillis() - startTime < durationMillis) {
        //         int deltaY = 1620; // Adjust the scroll amount as needed
        //         new Actions(driver)
        //             .scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(container), 0, deltaY)
        //             .perform();

        //         getTeamNames(driver, newTeamNames,lastLoadedTeamName, playerNames);
        //         lastLoadedTeamName = newTeamNames.get(newTeamNames.size() - 1);   
        //     }
        //     List<String> uniqueTeamNames = removeDuplicates(newTeamNames);
        //     List<String> uniquePlayerNames = removeDuplicates(playerNames);
        
        //     for (int i=0; i<uniqueTeamNames.size(); i++) {
        //         System.out.println(uniqueTeamNames.get(i));
        //         System.out.println(uniquePlayerNames.get(i));
        //     }

        // } catch (Exception e) {
        //     e.printStackTrace();
        // } finally {
        //     driver.quit();
        // }

        // Now go to results page

           try {
            driver.get(url+"/results");
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(1000);
            // Locate the dropdown button element
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));

            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Premier option element based on its text
            WebElement premierOption = driver.findElement(By.xpath("//div[@class=' css-1olvhr-option' and text()='Premier 5.0+']"));

            // Click on the Premier option
            premierOption.click();
            Thread.sleep(1000);
            List<String> premierTeams = new ArrayList<>();
            List<String> premierRecords = new ArrayList<>();
            List<String> premierPlacements = new ArrayList<>();
            getResultsData(driver, premierTeams, premierRecords, premierPlacements);
            for(int i=0; i<premierTeams.size(); i++){
                System.out.println(premierTeams.get(i));
                System.out.println(premierRecords.get(i));
                System.out.println(premierPlacements.get(i));
            }
            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Womens option element based on its text
            WebElement womensOption = driver.findElement(By.xpath("//div[@class=' css-1aqxqud-option']"));

            // Click on the Womens option
            womensOption.click();
            Thread.sleep(1000);
            List<String> womensTeams = new ArrayList<>();
            List<String> womensRecords = new ArrayList<>();
            List<String> womensPlacements = new ArrayList<>();
            getResultsData(driver, womensTeams, womensRecords, womensPlacements);
            for(int i=0; i<womensTeams.size(); i++){
                System.out.println(womensTeams.get(i));
                System.out.println(womensRecords.get(i));
                System.out.println(womensPlacements.get(i));
            }

            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Contender option element based on its text
            WebElement contenderOption = driver.findElement(By.xpath("//div[@class=' css-1aqxqud-option' and text()='Contender 4.5+']"));

            // Click on the Contender option
            contenderOption.click();
            Thread.sleep(1000);
            List<String> contenderTeams = new ArrayList<>();
            List<String> contenderRecords = new ArrayList<>();
            List<String> contenderPlacements = new ArrayList<>();
            getResultsData(driver, contenderTeams, contenderRecords, contenderPlacements);
            for(int i=0; i<contenderTeams.size(); i++){
                System.out.println(contenderTeams.get(i));
                System.out.println(contenderRecords.get(i));
                System.out.println(contenderPlacements.get(i));
            }

            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Advanced option element based on its text
            WebElement advancedOption = driver.findElement(By.xpath("//div[@class=' css-1aqxqud-option' and text()='Advanced 4.0']"));

            // Click on the Advanced option
            advancedOption.click();
            Thread.sleep(1000);
            List<String> advancedTeams = new ArrayList<>();
            List<String> advancedRecords = new ArrayList<>();
            List<String> advancedPlacements = new ArrayList<>();
            System.out.println(advancedTeams.size());
            System.out.println(advancedRecords.size());
            System.out.println(advancedPlacements.size());
            getResultsData(driver, advancedTeams, advancedRecords, advancedPlacements);
            for(int i=0; i<advancedTeams.size(); i++){
                System.out.println(advancedTeams.get(i));
                System.out.println(advancedRecords.get(i));
                System.out.println(advancedPlacements.get(i));
            }

            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Intermediate option element based on its text
            WebElement intermediateOption = driver.findElement(By.xpath("//div[@class=' css-1aqxqud-option' and text()='Intermediate 3.0']"));

            // Click on the Intermediate option
            intermediateOption.click();
            Thread.sleep(1000);
            List<String> intermediateTeams = new ArrayList<>();
            List<String> intermediateRecords = new ArrayList<>();
            List<String> intermediatePlacements = new ArrayList<>();
            getResultsData(driver, intermediateTeams, intermediateRecords, intermediatePlacements);
            for(int i=0; i<intermediateTeams.size(); i++){
                System.out.println(intermediateTeams.get(i));
                System.out.println(intermediateRecords.get(i));
                System.out.println(intermediatePlacements.get(i));
            }

            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Mixed Advanced option element based on its text
            WebElement mixedAdvancedOption = driver.findElement(By.xpath("//div[@class=' css-1aqxqud-option' and text()='Mixed Advanced 4.0+']"));

            // Click on the Mixed Advanced option
            mixedAdvancedOption.click();
            Thread.sleep(1000);
            List<String> mixedAdvancedTeams = new ArrayList<>();
            List<String> mixedAdvancedRecords = new ArrayList<>();
            List<String> mixedAdvancedPlacements = new ArrayList<>();
            getResultsData(driver, mixedAdvancedTeams, mixedAdvancedRecords, mixedAdvancedPlacements);
            for(int i=0; i<mixedAdvancedTeams.size(); i++){
                System.out.println(mixedAdvancedTeams.get(i));
                System.out.println(mixedAdvancedRecords.get(i));
                System.out.println(mixedAdvancedPlacements.get(i));
            }

            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Mixed Intermediate option element based on its text
            WebElement mixedIntermediateOption = driver.findElement(By.xpath("//div[@class=' css-1aqxqud-option' and text()='Mixed Intermediate 2.0-3.0']"));

            // Click on the Mixed Intermediate option
            mixedIntermediateOption.click();
            Thread.sleep(1000);
            List<String> mixedIntermediateTeams = new ArrayList<>();
            List<String> mixedIntermediateRecords = new ArrayList<>();
            List<String> mixedIntermediatePlacements = new ArrayList<>();
            getResultsData(driver, mixedIntermediateTeams, mixedIntermediateRecords, mixedIntermediatePlacements);
            for(int i=0; i<mixedIntermediateTeams.size(); i++){
                System.out.println(mixedIntermediateTeams.get(i));
                System.out.println(mixedIntermediateRecords.get(i));
                System.out.println(mixedIntermediatePlacements.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    
    private static List<String> getTeamNames(WebDriver driver, List<String> teamNames, String lastTeamName, List<String> playerNames) {
        List<WebElement> teamNameElements = driver.findElements(By.cssSelector("button.team-name-clickable"));
        List<WebElement> playersElements = driver.findElements(By.className("players"));
            
        for (int i=0; i<teamNameElements.size(); i++) {
            String teamName = teamNameElements.get(i).getText();
            WebElement playerNameElement = playersElements.get(i).findElement(By.tagName("span"));
            String names = playerNameElement.getText();
            teamNames.add(teamName);
            playerNames.add(names);
        }
        return teamNames;
    }
    public static List<String> removeDuplicates(List<String> inputList) {
        Set<String> uniqueSet = new HashSet<>();
        List<String> uniqueList = new ArrayList<>();

        for (String item : inputList) {
            if (uniqueSet.add(item)) {
                uniqueList.add(item);
            }
        }

        return uniqueList;
    }
    public static void getResultsData(WebDriver driver, List<String> teams, List<String> records, List<String> placements){
        // These will all match in terms of order of items
        List<WebElement> teamNameElements = driver.findElements(By.className("team-name"));
        System.out.println(teamNameElements.size());
        List<WebElement> recordElements = driver.findElements(By.className("record-column"));
        List<WebElement> placementsElements = driver.findElements(By.className("rank-column"));
        for(int i=0; i<teamNameElements.size(); i++){
                placements.add(String.valueOf(i));
            String teamName = teamNameElements.get(i).getText();
            String record = recordElements.get(i).getText();
            teams.add(teamName);
            records.add(record);
        }
    }
}

