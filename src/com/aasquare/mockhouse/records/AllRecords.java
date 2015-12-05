package com.aasquare.mockhouse.records;

import java.sql.Time;
import java.util.Date;


public class AllRecords {
    private Date date;
    private int num_of_ques,  score;
    private float avg;
    Time time;
  

     public AllRecords() {
        date = null;
        num_of_ques = 0;
        score = 0;
        avg = 0.0f;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

           public void setTime(Time t) {
        time=t;
    }
      public Time getTime() {
        return time;
    }
    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the num_of_ques
     */
    public int getNum_of_ques() {
        return num_of_ques;
    }

    /**
     * @param num_of_ques the num_of_ques to set
     */
    public void setNum_of_ques(int num_of_ques) {
        this.num_of_ques = num_of_ques;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
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
