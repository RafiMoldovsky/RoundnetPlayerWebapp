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
import java.util.regex.*;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

public class FwangoScraper {
    public static int thisTournamentsReactNumber = 0;
    public static boolean quiet = false;
    public static void main(String[] args) {
        WebDriverManager.chromedriver().driverVersion("114.0.5735.90").setup(); // Setup ChromeDriver automatically
        
        WebDriver driver = new ChromeDriver();
        ArrayList<String> tournamentNames = new ArrayList<>();
        // tournamentNames.add("saltlakecity2023");
        // tournamentNames.add("richmond2023");
        // tournamentNames.add("philadelphia2023");
        // tournamentNames.add("scgrandslam2023");
        // tournamentNames.add("thepeopleschampionship");  
        // tournamentNames.add("heatwavevi");
        // tournamentNames.add("rivercup");
         //tournamentNames.add("ers23md");
        // tournamentNames.add("nashvillecup2023");
        // tournamentNames.add("etslondon2023");
        // tournamentNames.add("windy-city-classic-23");
        // tournamentNames.add("coupeestivale");
        // tournamentNames.add("long-island-classic-2023");
        // tournamentNames.add("sts23portlandopen");
        // tournamentNames.add("toulouse2023");
         //tournamentNames.add("ers23nova");
        // tournamentNames.add("sdgrandslam2023");
        // tournamentNames.add("queencityclassic2023");
        // tournamentNames.add("stockholm");
        // tournamentNames.add("atlslam23");
        // tournamentNames.add("etsprague2023");
         tournamentNames.add("tograndslam2023");
        ArrayList<TeamObject> teamObjects = new ArrayList<>();
        ArrayList<TeamResultObject> divisionTeamResults = new ArrayList<>();
        ArrayList<GameData> games = new ArrayList<>();
        ArrayList<SeriesData> series = new ArrayList<>();
        ArrayList<TournamentData> tournamentObjects = new ArrayList<>();
        long programStartTime = System.currentTimeMillis();
        for(int i=0; i<tournamentNames.size(); i++){
            long startTime = System.currentTimeMillis();
            System.out.println("Currently working on: " + tournamentNames.get(i) + " (" + (i+1) + " out of " + tournamentNames.size() + ")");
            String tourneyName = tournamentNames.get(i);
            String url = "https://fwango.io/" + tourneyName;
            driver.manage().window().setSize(new Dimension(1200, 1400)); // Set window size
            System.out.println("Working on home page");
           // processHomePage(driver, url, tourneyName, teamObjects, tournamentObjects);
            System.out.println("Working on results page");
            processResultsPage(driver, url, divisionTeamResults, tourneyName);
            System.out.println("Working on pool play page");
            processPoolPlay(driver, url, tourneyName, games);
            System.out.println("Working on bracket play page");
            processBracketPlay(driver, url, tourneyName, games, series);
            long endTime = System.currentTimeMillis(); // Capture end time
            long elapsedTime = endTime - startTime; // Calculate elapsed time
            System.out.println(teamObjects.size() + " teams");
            System.out.println(divisionTeamResults.size() + " records");
            System.out.println(games.size() + " games");
            System.out.println(series.size() + " series");
            System.out.println("Iteration took " + elapsedTime + " milliseconds");
        }  
        //printData(teamObjects, divisionTeamResults, games, series);
        writeDataToCSV("teamObjects", "teamObjects.csv", new ArrayList<>(teamObjects));
        writeDataToCSV("divisionTeamResults", "divisionTeamResults.csv", divisionTeamResults);
        writeDataToCSV("games", "games.csv", games);
        writeDataToCSV("series", "series.csv", series);
        writeDataToCSV("tournaments", "tournaments.csv", tournamentObjects);
        long programEndTime = System.currentTimeMillis(); // Capture end time
        long programElapsedTime = programEndTime - programStartTime; // Calculate elapsed time
        System.out.println(tournamentNames.size() + " tournaments took " + programElapsedTime + " milliseconds");
        driver.quit();
    }
    
    public static void printData(ArrayList<TeamObject> teamObjects, ArrayList<TeamResultObject> divisionTeamResults, ArrayList<GameData> games, ArrayList<SeriesData> series){
        for(TeamObject team : teamObjects){
            team.print();
        }
        for(TeamResultObject result : divisionTeamResults){
            result.print();
        }
        for(GameData game : games){
            game.print();
        }
        for(SeriesData thisSeries : series){
            thisSeries.print();
        }
    }
    public static void writeDataToCSV(String datasetName, String csvFilePath, List<?> dataset) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {
            // Write header row based on dataset type
            if (datasetName.equals("teamObjects")) {
                csvWriter.writeNext(new String[]{"Player 1", "Player 2", "Team Name", "Tournament"});
            } else if (datasetName.equals("divisionTeamResults")) {
                csvWriter.writeNext(new String[]{"Team Name", "Wins", "Losses", "Result", "Division", "Tournament"});
            } else if (datasetName.equals("games")) {
                csvWriter.writeNext(new String[]{"Team 1", "Team 2", "Players 1", "Players 2", "Score 1", "Score 2", "Tournament Stage", "Tournament Name", "Division"});
            } else if (datasetName.equals("series")) {
                csvWriter.writeNext(new String[]{"Team 1", "Team 2", "Round", "Tournament", "Team 1 Scores", "Team 2 Scores", "Division"});
            }
            else if (datasetName.equals("tournaments")) {
                csvWriter.writeNext(new String[]{"Tournament Name", "URL", "Date"});
            }

            // Write data rows
            for (Object data : dataset) {
                String[] row = null;
                if (data instanceof TeamObject) {
                    row = new String[]{((TeamObject) data).player1, ((TeamObject) data).player2,
                                      ((TeamObject) data).teamName, ((TeamObject) data).tournament};
                } else if (data instanceof TeamResultObject) {
                    row = new String[]{((TeamResultObject) data).teamName, String.valueOf(((TeamResultObject) data).wins),
                                      String.valueOf(((TeamResultObject) data).losses), String.valueOf(((TeamResultObject) data).result),
                                      ((TeamResultObject) data).division, ((TeamResultObject) data).tournament};
                } else if (data instanceof GameData) {
                    row = new String[]{((GameData) data).team1, ((GameData) data).team2,
                                      ((GameData) data).t1p1 + ", " + ((GameData) data).t1p2,
                                      ((GameData) data).t2p1 + ", " + ((GameData) data).t2p2,
                                      String.valueOf(((GameData) data).t1Points), String.valueOf(((GameData) data).t2Points),
                                      ((GameData) data).tournamentStage, ((GameData) data).tournamentName, ((GameData) data).division};
                } else if (data instanceof SeriesData) {
                    row = new String[]{((SeriesData) data).team1, ((SeriesData) data).team2,
                                      ((SeriesData) data).round, ((SeriesData) data).tournament,
                                      ((SeriesData) data).t1Scores.toString(), ((SeriesData) data).t2Scores.toString(),
                                      ((SeriesData) data).division};
                } else if (data instanceof TournamentData) {
                    row = new String[]{((TournamentData) data).name, String.valueOf(((TournamentData) data).url),
                                      String.valueOf(((TournamentData) data).date)};
                    }

                csvWriter.writeNext(row);
            }

            csvWriter.flush();
        } catch (IOException e) {
            if(!quiet){e.printStackTrace();}
        }
    }
    public static void processHomePage(WebDriver driver, String url, String tourneyName, ArrayList<TeamObject> teamObjects, ArrayList<TournamentData> tournamentObjects){
        try {
            driver.get(url);
            Thread.sleep(2000);
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            
            // Get tournament date: 
            WebElement tourneyDateElement = driver.findElement(By.className("date"));
            TournamentData thisTournament = new TournamentData();
            thisTournament.date = tourneyDateElement.getText();
            thisTournament.name = tourneyName;
            thisTournament.url = url;
            tournamentObjects.add(thisTournament);
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
            List<String> uniquePlayerNamesAndTeamNames = removeNonUniqueCombinations(playerNames, newTeamNames);
            for (int i=0; i<uniquePlayerNamesAndTeamNames.size(); i++) {
                String[] parts1 = uniquePlayerNamesAndTeamNames.get(i).split("UNIQUESPLITTERDONOTREPEAT");
                String[] parts2 = parts1[0].split(" and ");
                TeamObject thisTeam = new TeamObject();
                thisTeam.teamName = parts1[1];
                if (parts2.length == 2) {
                    String firstPlayer = parts2[0].toLowerCase();
                    String secondPlayer = parts2[1].toLowerCase();
                    thisTeam.player1 = firstPlayer;
                    thisTeam.player2 = secondPlayer;
                    thisTeam.tournament = tourneyName;
                    teamObjects.add(thisTeam);
                }
            }

        } catch (Exception e) {
            if(!quiet){e.printStackTrace();}
        } 
    }
    private static List<String> getTeamNames(WebDriver driver, List<String> teamNames, String lastTeamName, List<String> playerNames) {
        List<WebElement> teamNameElements = driver.findElements(By.cssSelector("button.team-name-clickable"));
        List<WebElement> playersElements = driver.findElements(By.className("players"));
            
        for (int i=0; i<teamNameElements.size() && i<playersElements.size(); i++) {
            String teamName = teamNameElements.get(i).getText();
            WebElement playerNameElement = playersElements.get(i).findElement(By.tagName("span"));
            String names = playerNameElement.getText();
            teamNames.add(teamName);
            playerNames.add(names);
        }
        return teamNames;
    }
    public static void processResultsPage(WebDriver driver, String url, ArrayList<TeamResultObject> divisionTeamResults, String tournament){
        // Now go to results page

           try {
            driver.get(url+"/results");
            
            // Wait for the page to fully load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            Thread.sleep(1000);
            // Locate the dropdown button element
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));

            resultsProcessingHelper(driver, dropdownButton, divisionTeamResults, tournament);

        } catch (Exception e) {
            if(!quiet){e.printStackTrace();}
        } 
    }
    public static void resultsProcessingHelper(WebDriver driver, WebElement dropdownButton, ArrayList<TeamResultObject> divisionTeamResults, String tournament){
        try{
            boolean foundReactNumber = false;
            for(int j=0; j<10; j++){ // the j number changes depending on the tournament
                boolean elementFound = true;
                if(foundReactNumber){
                    break;
                }
                int i=0;
                while (elementFound) {
                    String elementId = "react-select-"+ j +"-option-" + i;
                    try {
                        // Click on the dropdown button
                        dropdownButton.click();
                        Thread.sleep(100);
                        WebElement optionElement = driver.findElement(By.id(elementId));
                        String division = optionElement.getText();
                        if(division.toLowerCase().equals("free agent")){
                            continue;
                        }
                        optionElement.click();
                        thisTournamentsReactNumber = j;
                        foundReactNumber = true;
                        Thread.sleep(1000);
                        List<TeamResultObject> results = new ArrayList<>();
                        getResultsData(driver, results, division, tournament);
                        divisionTeamResults.addAll(results);

                        i++;
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        elementFound = false;
                    }
                }
            }

        }
        catch (Exception e) {
            if(!quiet){e.printStackTrace();}
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
                try{
                    thisResult.result = Integer.valueOf(rankElements.get(i).getText());
                }
                catch(Exception e){}
            }
            String teamName = teamNameElements.get(i).getText();
            String record = recordElements.get(i).getText();
            thisResult.teamName = teamName;
            try{
                String[] parts = record.split(" - ");
                if (parts.length == 2) {
                int wins = extractNumber(parts[0]);
                int losses = extractNumber(parts[1]);
                thisResult.wins = wins;
                thisResult.losses = losses; 
                }
            } catch(Exception e){}
            
            thisResult.division = division;
            thisResult.tournament = tournament;
            teamResults.add(thisResult);
        }
    }
    public static void processPoolPlay(WebDriver driver, String url, String tournamentName, ArrayList<GameData> games){
        try{
            driver.get(url);
            Thread.sleep(1000);
            // Locate the pool play button element
            WebElement poolPlayButton = driver.findElement(By.xpath("//*[@id=\"root\"]/span/div[1]/div/div/div[2]/div/div[1]/div[2]/div/div/div/div/nav/ul[2]/li[3]/div/a"));
            poolPlayButton.click();
            Thread.sleep(1000);
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));
            poolPlayHelper(driver, dropdownButton, games, tournamentName);

        } catch (Exception e) {
            if(!quiet){e.printStackTrace();}
        } 
    }
    public static void poolPlayHelper(WebDriver driver, WebElement dropdownButton, ArrayList<GameData> divisionGameResults, String tournamentName){
        try{            
            int i=0;
            boolean elementFound = true;
                while (elementFound) {
                    String elementId = "react-select-"+ thisTournamentsReactNumber +"-option-" + i;
                    try {
                        // Click on the dropdown button
                        dropdownButton.click();
                        Thread.sleep(100);
                        WebElement optionElement = driver.findElement(By.id(elementId));
                        String division = optionElement.getText();
                        if(division.toLowerCase().equals("free agent")){
                            continue;
                        }
                        optionElement.click();
                        Thread.sleep(1000);
                        List<GameData> games = new ArrayList<>();
                        // Scroll and load more content
                        WebElement container = driver.findElement(By.cssSelector("#body-scroll > div > div > div.infinite-scroll-component__outerdiv"));
                        long startTime = System.currentTimeMillis();
                        long durationMillis = 4000; // 4 seconds

                        while (System.currentTimeMillis() - startTime < durationMillis) {
                            int deltaY = 10000; // Adjust the scroll amount as needed
                            new Actions(driver)
                                .scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(container), 0, deltaY)
                                .perform();

                        }
                        getPoolPlayData(driver, games, tournamentName, division);
                        divisionGameResults.addAll(games);

                        i++;
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        elementFound = false;
                    }
                }
        }catch (Exception e) {
            if(!quiet){e.printStackTrace();}
        } 
       
    }
    public static List<String> removeNonUniqueCombinations(List<String> playerNames, List<String> teamNames) {
        Set<String> uniqueCombinations = new HashSet<>();
        List<String> nonUniqueCombinations = new ArrayList<>();
    
        for (int i = 0; i < playerNames.size(); i++) {
            String combination = playerNames.get(i) + "UNIQUESPLITTERDONOTREPEAT" + teamNames.get(i);
            if (uniqueCombinations.add(combination)) {
                nonUniqueCombinations.add(combination);
            }
        }
    
        return nonUniqueCombinations;
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
            try{
                thisGame.team1 = nameElements.get(0).getText();            }
            catch(Exception e){

            }
            try{
                thisGame.team2 = nameElements.get(1).getText();
            }
            catch(Exception e){}
            try{
                thisGame.t1Points = Integer.parseInt(scoreElement.get(0).getAttribute("value"));
                thisGame.t2Points = Integer.parseInt(scoreElement.get(1).getAttribute("value"));
            }
            catch(Exception e){} 
            thisGame.tournamentStage = "Pool Play";
            thisGame.tournamentName = tournamentName;
            thisGame.division=division;
            games.add(thisGame);
        }
    }
    public static void processBracketPlay(WebDriver driver, String url, String tournamentName, ArrayList<GameData> games,  ArrayList<SeriesData> series){
        try{
            // Going to add a gameData object for each game in the bracket and a series object for each series
            driver.get(url);
            Thread.sleep(1000);
            // Locate the bracket play button element
            WebElement bracketPlayButton = driver.findElement(By.xpath("//*[@id=\"root\"]/span/div[1]/div/div/div[2]/div/div[1]/div[2]/div/div/div/div/nav/ul[2]/li[4]/div/a/span/i"));
            bracketPlayButton.click();
            Thread.sleep(1000);            
            WebElement dropdownButton = driver.findElement(By.className("select-input-container"));
            bracketPlayHelper(driver, dropdownButton, games, series, tournamentName);
        }
        catch (Exception e) {
            if(!quiet){e.printStackTrace();}
        } 
        
    }
    public static void bracketPlayHelper(WebDriver driver, WebElement dropDownButton, ArrayList<GameData> allGames, ArrayList<SeriesData> allSeries, String tournamentName){
        try{
            int i=0;
            boolean elementFound = true;
                while (elementFound) {
                    String elementId = "react-select-"+ thisTournamentsReactNumber +"-option-" + i;
                    try {
                        // Click on the dropdown button
                        dropDownButton.click();
                        Thread.sleep(100);
                        WebElement optionElement = driver.findElement(By.id(elementId));
                        String division = optionElement.getText();
                        if(division.toLowerCase().equals("free agent")){
                            continue;
                        }
                        optionElement.click();
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
                                for(int k=0; k<scoreElements.size(); k++){
                                    if(k%2==0){
                                        try{
                                            t1Scores.add(Integer.parseInt(scoreElements.get(k).getAttribute("value")));
                                        }catch(Exception e){
                                            t1Scores.add(-1); // Not sure what this should be
                                        }
                                    } 
                                    else{
                                        try{
                                            t2Scores.add(Integer.parseInt(scoreElements.get(k).getAttribute("value")));
                                        }catch(Exception e){
                                            t2Scores.add(-1); // Not sure what this should be
                                        }
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
                                for(int k=0; k<t1Scores.size(); k++){
                                    GameData thisGameData = new GameData();
                                    thisGameData.team1 = team1;
                                    thisGameData.team2 = team2;
                                    thisGameData.t1Points = t1Scores.get(k);
                                    thisGameData.t2Points = t2Scores.get(k);
                                    thisGameData.tournamentName = tournamentName;
                                    thisGameData.tournamentStage = "Bracket play round of " + currentRound + " game " + (k+1);
                                    thisGameData.division = division;
                                    games.add(thisGameData);
                                }
                                // thisSeries.print();
                            }
                        }
                        allSeries.addAll(series);
                        allGames.addAll(games);
                        i++;
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        elementFound = false;
                    }
                }
        }
        catch (Exception e) {
            if(!quiet){e.printStackTrace();}
        } 
    }
}

