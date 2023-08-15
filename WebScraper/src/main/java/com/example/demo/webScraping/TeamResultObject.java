package com.example.demo.webScraping;

public class TeamResultObject {
    public String teamName;
    public int wins;
    public int losses;
    public int result;
    public String division;
    public String tournament;
    public void print(){
        System.out.println(teamName + " finished at spot: " + result + " with a record " + wins + "W - " + losses + "L at " + tournament + " (" + division + ")");
    }
}
