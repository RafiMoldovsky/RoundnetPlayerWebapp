package com.example.demo.webScraping;

public class GameData {
    // This class will hold info on a singular game
    // This will include the teams, the players names on each team, the score, what stage of the tournament this takes place in, and what tournament this occurs in
    public String team1;
    public String team2;
    public String t1p1;
    public String t1p2;
    public String t2p1;
    public String t2p2;
    public int t1Points;
    public int t2Points;
    public String tournamentStage;
    public String tournamentName;
    public String division;
    GameData(){

    }
    public void print(){
        System.out.println("Tournament: " + tournamentName + " (" + division + ")");
        System.out.println("Stage: " + tournamentStage);
        System.out.println("Team 1: " + team1);
        System.out.println("Players 1: " + t1p1 + ", " + t1p2);
        System.out.println("Score 1: " + t1Points);
        System.out.println("Team 2: " + team2);
        System.out.println("Players 2: " + t2p1 + ", " + t2p2);
        System.out.println("Score 2: " + t2Points);
        System.out.println();
    }
}
