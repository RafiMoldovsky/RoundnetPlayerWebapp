package com.example.demo.webScraping;

public class TeamResultObject {
    String teamName;
    int wins;
    int losses;
    int result;
    public void print(){
        System.out.println(teamName + " finished at spot: " + result + " with a record " + wins + "W - " + losses + "L");
    }
}
