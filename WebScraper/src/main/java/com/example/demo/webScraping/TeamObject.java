package com.example.demo.webScraping;

public class TeamObject {
    public String player1;
    public String player2;
    public String teamName;
    public String tournament;
    public void print(){
        System.out.println("Team: " + teamName + " with players: " + player1 + " and " + player2 + " at tournament: " + tournament);
    }
}
