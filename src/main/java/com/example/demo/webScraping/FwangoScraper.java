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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;
import java.util.Collections;
import java.util.Comparator;

public class FwangoScraper {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().driverVersion("114.0.5735.90").setup(); // Setup ChromeDriver automatically
        
        WebDriver driver = new ChromeDriver();
        String tourneyName = "philadelphia2023";
        String url = "https://fwango.io/" + tourneyName;
        driver.manage().window().setSize(new Dimension(1200, 800)); // Set window size
        
        //processHomePage(driver, url);
        //processResultsPage(driver, url);
        processPoolPlay(driver, url, tourneyName);

        driver.quit();
    }
    public static void processHomePage(WebDriver driver, String url){
        try {
            driver.get(url);
            Thread.sleep(2000);
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            
            // Get tournament date: 
            WebElement tourneyDateElement = driver.findElement(By.className("date"));
            System.out.println("Tournament date: " + tourneyDateElement.getText());
            // Find and print initially loaded team names
            List<String> newTeamNames = new ArrayList<>();
            List<String> playerNames = new ArrayList<>();
            getTeamNames(driver, newTeamNames,"", playerNames);
            
            // Scroll and load more content
            WebElement container = driver.findElement(By.cssSelector("div.TournamentTeamList__Container-sc-13mkiy8-0.irfjog"));
            String lastLoadedTeamName = "";
            long startTime = System.currentTimeMillis();
            long durationMillis = 9000; // 9 seconds

            while (System.currentTimeMillis() - startTime < durationMillis) {
                int deltaY = 1000; // Adjust the scroll amount as needed
                new Actions(driver)
                    .scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(container), 0, deltaY)
                    .perform();

                getTeamNames(driver, newTeamNames,lastLoadedTeamName, playerNames);
                lastLoadedTeamName = newTeamNames.get(newTeamNames.size() - 1);   
            }
            List<String> uniqueTeamNames = removeDuplicates(newTeamNames);
            List<String> uniquePlayerNames = removeDuplicates(playerNames);
            List<TeamObject> teamObjects = new ArrayList<>();
            for (int i=0; i<uniqueTeamNames.size(); i++) {
                String[] parts = uniquePlayerNames.get(i).split(" and ");
                TeamObject thisTeam = new TeamObject();
                thisTeam.teamName = uniqueTeamNames.get(i);
                if (parts.length == 2) {
                    String firstPlayer = parts[0].toLowerCase();
                    String secondPlayer = parts[1].toLowerCase();
                    thisTeam.player1 = firstPlayer;
                    thisTeam.player2 = secondPlayer;
                    teamObjects.add(thisTeam);
                }
                thisTeam.print();
            }
            System.out.println(uniqueTeamNames.size());

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public static void processResultsPage(WebDriver driver, String url){
        // Now go to results page

           try {
            driver.get(url+"/results");
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(1000);
            // Locate the dropdown button element
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));
            Map<String, List<TeamResultObject>> divisionTeamResults = new HashMap<>();

            resultsProcessingHelper(driver, "Premier", "//div[@class=' css-1olvhr-option' and text()='Premier 5.0+']", dropdownButton, divisionTeamResults);
            resultsProcessingHelper(driver, "Womens", "//div[@class=' css-1aqxqud-option']", dropdownButton, divisionTeamResults);
            resultsProcessingHelper(driver, "Contender", "//div[@class=' css-1aqxqud-option' and text()='Contender 4.5+']", dropdownButton, divisionTeamResults);
            resultsProcessingHelper(driver, "Advanced", "//div[@class=' css-1aqxqud-option' and text()='Advanced 4.0']", dropdownButton, divisionTeamResults);
            resultsProcessingHelper(driver, "Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Intermediate 3.0']", dropdownButton, divisionTeamResults);
            resultsProcessingHelper(driver, "Mixed Advanced", "//div[@class=' css-1aqxqud-option' and text()='Mixed Advanced 4.0+']", dropdownButton, divisionTeamResults);
            resultsProcessingHelper(driver, "Mixed Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Mixed Intermediate 2.0-3.0']", dropdownButton, divisionTeamResults);

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public static void resultsProcessingHelper(WebDriver driver, String division, String xpath, WebElement dropdownButton, Map<String, List<TeamResultObject>> divisionTeamResults){
        try{
            // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Selected option element based on its text
            WebElement selectedOption = driver.findElement(By.xpath(xpath));
            // Click on the selected option option
            selectedOption.click();
            Thread.sleep(1000);
            List<TeamResultObject> results = new ArrayList<>();
            getResultsData(driver, results);
            divisionTeamResults.put(division, results);
            for(TeamResultObject result : results){
                result.print();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } 

    }
    public static void processPoolPlay(WebDriver driver, String url, String tournamentName){
        try{
            driver.get(url);
            Thread.sleep(1000);
            // Locate the pool play button element
            WebElement poolPlayButton = driver.findElement(By.xpath("//*[@id=\"root\"]/span/div[1]/div/div/div[2]/div/div[1]/div[2]/div/div/div/div/nav/ul[2]/li[3]/div/a"));
            poolPlayButton.click();
            Thread.sleep(1000);
            Map<String, List<GameData>> games = new HashMap<>();
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));
            poolPlayHelper(driver, "Premier", "//div[@class=' css-1olvhr-option' and text()='Premier 5.0+']", dropdownButton, games, tournamentName);
            poolPlayHelper(driver, "Womens", "//div[@class=' css-1aqxqud-option']", dropdownButton, games, tournamentName);
            poolPlayHelper(driver, "Contender", "//div[@class=' css-1aqxqud-option' and text()='Contender 4.5+']", dropdownButton, games, tournamentName);
            poolPlayHelper(driver, "Advanced", "//div[@class=' css-1aqxqud-option' and text()='Advanced 4.0']", dropdownButton, games, tournamentName);
            poolPlayHelper(driver, "Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Intermediate 3.0']", dropdownButton, games, tournamentName);
            poolPlayHelper(driver, "Mixed Advanced", "//div[@class=' css-1aqxqud-option' and text()='Mixed Advanced 4.0+']", dropdownButton, games, tournamentName);
            poolPlayHelper(driver, "Mixed Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Mixed Intermediate 2.0-3.0']", dropdownButton, games, tournamentName);

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public static void poolPlayHelper(WebDriver driver, String division, String xpath, WebElement dropdownButton, Map<String, List<GameData>> divisionGameResults, String tournamentName){
        try{
             // Click on the dropdown button
            dropdownButton.click();
            Thread.sleep(100);

            // Locate the Selected option element based on its text
            WebElement selectedOption = driver.findElement(By.xpath(xpath));
            // Click on the selected option 
            selectedOption.click();
            Thread.sleep(1000);
            List<GameData> games = new ArrayList<>();
            // Scroll and load more content
            WebElement container = driver.findElement(By.cssSelector("#body-scroll > div > div > div.infinite-scroll-component__outerdiv"));
            long startTime = System.currentTimeMillis();
            long durationMillis = 4000; // 4 seconds

            while (System.currentTimeMillis() - startTime < durationMillis) {
                // System.out.println("new scroll " + games.size());
                int deltaY = 10000; // Adjust the scroll amount as needed
                new Actions(driver)
                    .scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(container), 0, deltaY)
                    .perform();

            }
            getPoolPlayData(driver, games, tournamentName);
            for(GameData game : games){
                game.print();
            }
            divisionGameResults.put(division, games);
        }catch (Exception e) {
            e.printStackTrace();
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
    public static void getResultsData(WebDriver driver, List<TeamResultObject> teamResults){
        // These will all match in terms of order of items
        List<WebElement> teamNameElements = driver.findElements(By.className("team-name"));
        List<WebElement> recordElements = driver.findElements(By.className("record-column"));
        List<WebElement> rankElements = driver.findElements(By.cssSelector("td.rank-column"));
        if (!recordElements.isEmpty()) {
            recordElements.remove(0); // Remove the first element
        }
        for(int i=0; i<recordElements.size(); i++){
            TeamResultObject thisResult = new TeamResultObject();
            if(i<3 && rankElements.size()!=0){
                thisResult.result = i+1;
            }
            else if(rankElements.size()!=0){
                thisResult.result = Integer.valueOf(rankElements.get(i).getText());
            }
            String teamName = teamNameElements.get(i).getText();
            String record = recordElements.get(i).getText();
            String[] parts = record.split(" - ");
            thisResult.teamName = teamName;
            if (parts.length == 2) {
                int wins = extractNumber(parts[0]);
                int losses = extractNumber(parts[1]);
                thisResult.wins = wins;
                thisResult.losses = losses;
            }
            teamResults.add(thisResult);
        }
    }
    public static int extractNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return 0;
    }

    public static void getPoolPlayData(WebDriver driver, List<GameData> games, String tournamentName){
        // These will all match in terms of order of items
        List<WebElement> teamElements = driver.findElements(By.className("teams-container"));
        List<WebElement> pointElements = driver.findElements(By.className("games-container"));
        List<WebElement> matchNumbers = driver.findElements(By.className("Truncate-sc-1q8foad-0"));
        
        for(int i=0; i<teamElements.size(); i++){
            List<WebElement> nameElements = teamElements.get(i).findElements(By.className("team-name"));
            List<WebElement> scoreElement = pointElements.get(i).findElements(By.cssSelector("[type='number']"));
            GameData thisGame = new GameData();
            thisGame.team1 = nameElements.get(0).getText();
            thisGame.team2 = nameElements.get(1).getText();
            thisGame.t1Points = Integer.parseInt(scoreElement.get(0).getAttribute("value"));
            thisGame.t2Points = Integer.parseInt(scoreElement.get(1).getAttribute("value"));
            thisGame.tournamentStage = "Pool Play";
            thisGame.tournamentName = tournamentName;
            String matchNumberText = matchNumbers.get(i).getText();
            Pattern pattern = Pattern.compile("M(\\d+)");
            Matcher matcher = pattern.matcher(matchNumberText);

            if (matcher.find()) {
                String number = matcher.group(1);
                thisGame.matchNumber = Integer.parseInt(number);
            }
            games.add(thisGame);
        }
    }
}

