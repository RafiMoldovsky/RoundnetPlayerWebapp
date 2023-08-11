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
        String tourneyName = "richmond2023";
        String url = "https://fwango.io/" + tourneyName;
        driver.manage().window().setSize(new Dimension(1200, 1000)); // Set window size
        ArrayList<TeamObject> teamObjects = new ArrayList<>();
        processHomePage(driver, url, tourneyName, teamObjects);
        Map<String, List<TeamResultObject>> divisionTeamResults = new HashMap<>();
        processResultsPage(driver, url, divisionTeamResults, tourneyName);
        Map<String, List<GameData>> games = new HashMap<>();
        processPoolPlay(driver, url, tourneyName, games);
        Map<String, List<SeriesData>> series = new HashMap<>();
        processBracketPlay(driver, url, tourneyName, games, series);
        printData(teamObjects, divisionTeamResults, games, series);
        driver.quit();
    }
    public static void printData(ArrayList<TeamObject> teamObjects, Map<String, List<TeamResultObject>> divisionTeamResults, Map<String, List<GameData>> games, Map<String, List<SeriesData>> series){
        for(TeamObject team : teamObjects){
            team.print();
        }
        for(String division : divisionTeamResults.keySet()){
            for(TeamResultObject result : divisionTeamResults.get(division)){
                result.print();
            }
        }
        for(String division : games.keySet()){
            for(GameData game : games.get(division)){
                game.print();
            }
        }
        for(String division : series.keySet()){
            for(SeriesData thisSeries : series.get(division)){
                thisSeries.print();
            }
        }
    }
    public static void processHomePage(WebDriver driver, String url, String tourneyName, ArrayList<TeamObject> teamObjects){
        try {
            driver.get(url);
            Thread.sleep(2000);
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            
            // Get tournament date: 
            WebElement tourneyDateElement = driver.findElement(By.className("date"));
            System.out.println("Tournament date: " + tourneyDateElement.getText());
            TournamentData thisTournament = new TournamentData();
            thisTournament.date = tourneyDateElement.getText();
            thisTournament.name = tourneyName;
            thisTournament.url = url;
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
            for (int i=0; i<uniqueTeamNames.size(); i++) {
                String[] parts = uniquePlayerNames.get(i).split(" and ");
                TeamObject thisTeam = new TeamObject();
                thisTeam.teamName = uniqueTeamNames.get(i);
                if (parts.length == 2) {
                    String firstPlayer = parts[0].toLowerCase();
                    String secondPlayer = parts[1].toLowerCase();
                    thisTeam.player1 = firstPlayer;
                    thisTeam.player2 = secondPlayer;
                    thisTeam.tournament = tourneyName;
                    teamObjects.add(thisTeam);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public static void processResultsPage(WebDriver driver, String url, Map<String, List<TeamResultObject>> divisionTeamResults, String tournament){
        // Now go to results page

           try {
            driver.get(url+"/results");
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(1000);
            // Locate the dropdown button element
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));

            resultsProcessingHelper(driver, "Premier", "//div[@class=' css-1olvhr-option' and text()='Premier 5.0+']", dropdownButton, divisionTeamResults, tournament);
            resultsProcessingHelper(driver, "Womens", "//div[@class=' css-1aqxqud-option']", dropdownButton, divisionTeamResults, tournament);
            resultsProcessingHelper(driver, "Contender", "//div[@class=' css-1aqxqud-option' and text()='Contender 4.5+']", dropdownButton, divisionTeamResults, tournament);
            resultsProcessingHelper(driver, "Advanced", "//div[@class=' css-1aqxqud-option' and text()='Advanced 4.0']", dropdownButton, divisionTeamResults, tournament);
            resultsProcessingHelper(driver, "Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Intermediate 3.0']", dropdownButton, divisionTeamResults, tournament);
            resultsProcessingHelper(driver, "Mixed Advanced", "//div[@class=' css-1aqxqud-option' and text()='Mixed Advanced 4.0+']", dropdownButton, divisionTeamResults, tournament);
            resultsProcessingHelper(driver, "Mixed Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Mixed Intermediate 2.0-3.0']", dropdownButton, divisionTeamResults, tournament);

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public static void resultsProcessingHelper(WebDriver driver, String division, String xpath, WebElement dropdownButton, Map<String, List<TeamResultObject>> divisionTeamResults, String tournament){
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
            getResultsData(driver, results, division, tournament);
            divisionTeamResults.put(division, results);
            // for(TeamResultObject result : results){
            //     result.print();
            // }
        }
        catch (Exception e) {
            e.printStackTrace();
        } 

    }
    public static void getResultsData(WebDriver driver, List<TeamResultObject> teamResults, String division, String tournament){
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
                thisResult.division = division;
                thisResult.tournament = tournament;
            }
            teamResults.add(thisResult);
        }
    }
    public static void processPoolPlay(WebDriver driver, String url, String tournamentName, Map<String, List<GameData>> games){
        try{
            driver.get(url);
            Thread.sleep(1000);
            // Locate the pool play button element
            WebElement poolPlayButton = driver.findElement(By.xpath("//*[@id=\"root\"]/span/div[1]/div/div/div[2]/div/div[1]/div[2]/div/div/div/div/nav/ul[2]/li[3]/div/a"));
            poolPlayButton.click();
            Thread.sleep(1000);
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
            getPoolPlayData(driver, games, tournamentName, division);
            // for(GameData game : games){
            //     game.print();
            // }
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
    public static int extractNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }

        return 0;
    }

    public static void getPoolPlayData(WebDriver driver, List<GameData> games, String tournamentName, String division){
        // These will all match in terms of order of items
        List<WebElement> teamElements = driver.findElements(By.className("teams-container"));
        List<WebElement> pointElements = driver.findElements(By.className("games-container"));
        
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
            thisGame.division=division;
            games.add(thisGame);
        }
    }
    public static void processBracketPlay(WebDriver driver, String url, String tournamentName, Map<String, List<GameData>> games,  Map<String, List<SeriesData>> series){
        try{
            // Going to add a gameData object for each game in the bracket and a series object for each series
            driver.get(url);
            Thread.sleep(1000);
            // Locate the bracket play button element
            WebElement bracketPlayButton = driver.findElement(By.xpath("//*[@id=\"root\"]/span/div[1]/div/div/div[2]/div/div[1]/div[2]/div/div/div/div/nav/ul[2]/li[4]/div/a/span/i"));
            bracketPlayButton.click();
            Thread.sleep(1000);            
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));
            bracketPlayHelper(driver, "Premier", "//div[@class=' css-1olvhr-option' and text()='Premier 5.0+']", dropdownButton, games, series, tournamentName);
            bracketPlayHelper(driver, "Womens", "//div[@class=' css-1aqxqud-option']", dropdownButton, games, series, tournamentName);
            bracketPlayHelper(driver, "Contender", "//div[@class=' css-1aqxqud-option' and text()='Contender 4.5+']", dropdownButton, games, series, tournamentName);
            bracketPlayHelper(driver, "Advanced", "//div[@class=' css-1aqxqud-option' and text()='Advanced 4.0']", dropdownButton, games, series, tournamentName);
            bracketPlayHelper(driver, "Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Intermediate 3.0']", dropdownButton, games, series, tournamentName);
            bracketPlayHelper(driver, "Mixed Advanced", "//div[@class=' css-1aqxqud-option' and text()='Mixed Advanced 4.0+']", dropdownButton, games, series, tournamentName);
            bracketPlayHelper(driver, "Mixed Intermediate", "//div[@class=' css-1aqxqud-option' and text()='Mixed Intermediate 2.0-3.0']", dropdownButton, games, series, tournamentName);
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
        
    }
    public static void bracketPlayHelper(WebDriver driver, String division, String xpath, WebElement dropDownButton, Map<String, List<GameData>> allGames, Map<String, List<SeriesData>> allSeries, String tournamentName){
        try{
            // Click on the dropdown button
            dropDownButton.click();
            Thread.sleep(100);

            // Locate the Selected option element based on its text
            WebElement selectedOption = driver.findElement(By.xpath(xpath));
            // Click on the selected option 
            selectedOption.click();
            Thread.sleep(1000);
            // Need to scroll out a bit to see all series
            long startTime = System.currentTimeMillis();
            long durationMillis = 1000; // 1 second
            WebElement container = driver.findElement(By.cssSelector("#body-scroll > div > div > div > div.react-transform-component.TransformComponent-module_container__3NwNd > div > div > div > div"));
            while (System.currentTimeMillis() - startTime < durationMillis) {
                int deltaY = 1000; // Adjust the scroll amount as needed
                new Actions(driver)
                    .scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(container), 0, deltaY)
                    .perform();
            }
            List<GameData> games = new ArrayList<>();
            List<SeriesData> series = new ArrayList<>();
            List<WebElement> rounds = driver.findElements(By.className("OneSidedBracketstyle__BracketDrawColumnWrapper-sc-1fhx3vb-1"));
            for(WebElement round : rounds){
                String currentRound = round.findElement(By.className("title")).getText();
                List<WebElement> seriesElements = round.findElements(By.className("Matchstyle__BracketMatchContainer-sc-18us5a1-2"));
                boolean isfinal = true;
                for(WebElement seriesElement : seriesElements){
                    if(currentRound.equals("Final") && !isfinal){
                        currentRound = "Third Place";
                    }
                    else{
                        isfinal = false;
                    }
                    List<WebElement> teamNameElements = seriesElement.findElements(By.className("team-name"));
                    List<WebElement> scoreElements = new ArrayList<>();
                    try{
                        scoreElements = seriesElement.findElement(By.className("games-container")).findElements(By.cssSelector("[type='number']"));
                    } catch(Exception e){
                        // this might result in an exception, in the case that there's a bye - in which case skip this series
                        continue; 
                    }
                    String team1 = teamNameElements.get(0).getText();
                    String team2 = teamNameElements.get(1).getText();
                    ArrayList<Integer> t1Scores = new ArrayList<>();
                    ArrayList<Integer> t2Scores = new ArrayList<>();
                    for(int i=0; i<scoreElements.size(); i++){
                        if(i%2==0){
                            t1Scores.add(Integer.parseInt(scoreElements.get(i).getAttribute("value")));
                        } 
                        else{
                            t2Scores.add(Integer.parseInt(scoreElements.get(i).getAttribute("value")));
                        }
                    }
                    SeriesData thisSeries = new SeriesData();
                    thisSeries.team1 = team1;
                    thisSeries.team2 = team2;
                    thisSeries.round = currentRound;
                    thisSeries.tournament = tournamentName;
                    thisSeries.t1Scores = t1Scores;
                    thisSeries.t2Scores = t2Scores;
                    thisSeries.tournament = tournamentName;
                    thisSeries.division = division;
                    series.add(thisSeries);
                    // Now I am going to add each individual game from the series
                    for(int i=0; i<t1Scores.size(); i++){
                        GameData thisGameData = new GameData();
                        thisGameData.team1 = team1;
                        thisGameData.team2 = team2;
                        thisGameData.t1Points = t1Scores.get(i);
                        thisGameData.t2Points = t2Scores.get(i);
                        thisGameData.tournamentName = tournamentName;
                        thisGameData.tournamentStage = "Bracket play round of " + currentRound + " game " + (i+1);
                        thisGameData.division = division;
                        games.add(thisGameData);
                    }
                    // thisSeries.print();
                }
            }
            allSeries.put(division, series);
            allGames.put(division, games);
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
    }
}

