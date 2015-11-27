/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aasquare.mockhouse.result;

import java.sql.Time;
import java.util.Date;


/**
 *
 * @author super computer
 */
public class RecentResult {

    private String category;
    private Date date;
    private int num_of_ques,  score;
    private float avg;
    Time time;

    public RecentResult() {
        category = "NO TEST AVAILABLE";
        date = null;
        num_of_ques = 0;
        score = 0;
        avg = 0.0f;
    }

    public void setCategory(String cat) {
        category = cat;
    }

    public void setNum_of_ques(int num) {
        num_of_ques = num;

    }

    public void setScore(int scr) {
        score = scr;
    }

    public void setDate(Date d) {
        date = d;
    }
     public void setTime(Time t) {
        time=t;
    }
      public Time getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public int getNum_of_ques() {
        return num_of_ques;

    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return date;
    }

    /**
     * @return the avg
     */
    public float getAvg() {
        return avg;
    }

    /**
     * @param avg the avg to set
     */
    public void setAvg(float avg) {
        this.avg = avg;
    }
}





