package com.example.shihab.tourmanagement;

/**
 * Created by Kumu on 5/9/2017.
 */

public class TravelEvents {

    private String Budget;
    private String Destination;
    private String From;
    private String To;


    public TravelEvents(String budget, String destination, String from, String to) {
        Budget = budget;
        Destination = destination;
        From = from;
        To = to;
    }

    public TravelEvents(){

    }

    public String getBudget() {
        return Budget;
    }

    public void setBudget(String budget) {
        Budget = budget;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }
}
