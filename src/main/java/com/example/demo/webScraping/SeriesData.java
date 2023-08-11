package com.example.demo.webScraping;
import java.util.ArrayList;
import java.util.List;
public class SeriesData {
    public String team1;
    public String team2;
    public String round;
    public String tournament;
    ArrayList<Integer> t1Scores;
    ArrayList<Integer> t2Scores;
    public void print(){
        System.out.println("Series between " + team1 + " and " + team2 + " in round of " + round + " at " + tournament + ":");
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
