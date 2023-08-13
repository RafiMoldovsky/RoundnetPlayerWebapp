package com.example.demo.webScraping;
import java.util.ArrayList;
public class SeriesData {
    public String team1;
    public String team2;
    public String round;
    public String tournament;
    public ArrayList<Integer> t1Scores;
    public ArrayList<Integer> t2Scores;
    public String division;
    public void print(){
        System.out.println("Series between " + team1 + " and " + team2 + " in round of " + round + " at " + tournament + " (" + division + "):");
        System.out.print(team1 + ": ");
        for(int i=0; i<t1Scores.size(); i++){
            if(i==t1Scores.size()-1){
                System.out.println(t1Scores.get(i));
            }
            else{
                System.out.print(t1Scores.get(i) + ", ");
            }
        }
        System.out.print(team2 + ": ");
        for(int i=0; i<t2Scores.size(); i++){
            if(i==t2Scores.size()-1){
                System.out.println(t2Scores.get(i));
            }
            else{
                System.out.print(t2Scores.get(i) + ", ");
            }
        }
    }
}
