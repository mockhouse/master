

package com.quick.questions;

/**
 *
 * @author Administrator
 */
public class Question {

    private Integer id;
    private String ques;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int answer;
    private int type;

    public Question(){}
    public Question(Integer id,String ques, String option1, String option2, String option3, String option4, int answer,int type) {
        this.id = id;
        this.ques = ques;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.type=type;
    }
  public Question(Integer id,String ques, String option1, String option2, String option3, String option4, int answer) {
      this.id = id;
        this.ques = ques;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.type=0;
    }
    /**
     * Get the value of ques
     *
     * @return the value of ques
     */
    public String getQuestion() {
        return ques;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Set the value of ques
     *
     * @param ques new value of ques
     */
    public void setQuestion(String ques) {
        this.ques = ques;
    }

    /**
     * @return the option1
     */
    public String getOption1() {
        return option1;
    }

    /**
     * @param option1 the option1 to set
     */
    public void setOption1(String option1) {
        this.option1 = option1;
    }

    /**
     * @return the option2
     */
    public String getOption2() {
        return option2;
    }

    /**
     * @param option2 the option2 to set
     */
    public void setOption2(String option2) {
        this.option2 = option2;
    }

    /**
     * @return the option3
     */
    public String getOption3() {
        return option3;
    }

    /**
     * @param option3 the option3 to set
     */
    public void setOption3(String option3) {
        this.option3 = option3;
    }

    /**
     * @return the option4
     */
    public String getOption4() {
        return option4;
    }

    /**
     * @param option4 the option4 to set
     */
    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public void setType(int type) {
        this.type = type;
    }
    /**
     * @return the answer
     */
    public int getAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String toString()
    {
        return(this.getQuestion());
    }

    public int getType() {
        return type;
    }
    public String getOptionFromIndex(int indexId) {

        switch(indexId) {
            case 1 : return getOption1();
            case 2 : return getOption2();
            case 3 : return getOption3();
            case 4 : return getOption4();
            default: break;
        }
        return null;
    }
}
