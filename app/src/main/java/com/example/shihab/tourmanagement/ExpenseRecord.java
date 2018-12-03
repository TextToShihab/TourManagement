package com.example.shihab.tourmanagement;

/**
 * Created by Kumu on 5/10/2017.
 */

public class ExpenseRecord {

    private String Expense_detail;
    private String cost;
    private String current_date;
    private String current_time;

    public ExpenseRecord(String expense_detail, String cost, String current_date, String current_time) {
        Expense_detail = expense_detail;
        this.cost = cost;
        this.current_date = current_date;
        this.current_time = current_time;
    }

    public ExpenseRecord(){

    }

    public String getExpense_detail() {
        return Expense_detail;
    }

    public void setExpense_detail(String expense_detail) {
        Expense_detail = expense_detail;
    }



    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }


    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
