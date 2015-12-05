package com.aasquare.mockhouse.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aasquare.mockhouse.base.MockScoopBaseActivity;
import com.aasquare.mockhouse.checkyourknowledge.ShowResults;
import com.aasquare.mockhouse.global.Globals;
import com.aasquare.mockhouse.questions.Question;
import com.aasquare.mockhouse.util.GuiUtil;
import com.aasquare.mockhouse.util.Util;

public class ReviewQuiz extends MockScoopBaseActivity {

    private Activity currentActivity = null;
    private int nextQuestionIndex = 1;
    private Question[] lstQuestions;
    private int[] recordedAnswers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentActivity = getActivity();
        View rootView = inflater.inflate(R.layout.activity_review_quiz, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        lstQuestions = Globals.attemptedQuestions;
        recordedAnswers = Globals.recordedAnswers;
        ViewGroup reviewQuestionListLayout = (ViewGroup) currentActivity.findViewById(R.id.reviewQuestionList);
        LinearLayout reviewOptionListLayout = (LinearLayout) currentActivity.findViewById(R.id.reviewOptionList);
        showQuestion(lstQuestions, reviewQuestionListLayout, reviewOptionListLayout, nextQuestionIndex);
        setEndReviewButton();
    }

    public void setEndReviewButton(){
        //End Quiz Button Event
        ((Button) currentActivity.findViewById(R.id.btnEndReview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reviewFinished();
            }
        });
    }
    public void reviewFinished() {

        ShowResults sr = new ShowResults(false);///Don't save the results as this is just the review.
        getActivity().setTitle(R.string.quiz_results);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
        fragmentTransaction.replace(R.id.frame_container, sr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showQuestion(Question[] questionList, final ViewGroup reviewQuestionListLayout, final ViewGroup reviewOptionListLayout, int questionIndex) {

        if (lstQuestions.length < nextQuestionIndex) {
            //now move to the next activity and display the results
        	nextQuestionIndex--;
            reviewFinished();
            return;
        }
        TextView questionText = null;
        Button optionButton = null;
        int index = 0;
        //Next Question Button Event
        ((Button) currentActivity.findViewById(R.id.btnNextQuestionReview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showQuestion(lstQuestions, reviewQuestionListLayout, reviewOptionListLayout, ++nextQuestionIndex);
            }
        });


        reviewOptionListLayout.removeAllViews();
        Question question = questionList[questionIndex - 1];
        System.out.println("Current Questions : " + question);
        ((TextView) currentActivity.findViewById(R.id.questionText)).setText(question.getQuestion());
        GuiUtil.createAnswerOptionButtons(question, R.drawable.button_transperant, null, currentActivity, reviewOptionListLayout, getString(R.string.invalid_options));
        //set the formatting of the selected option
        int checkIndex = nextQuestionIndex -1;
        Button selectedButton = (Button) currentActivity.findViewById(recordedAnswers[checkIndex]);
        if(!Util.isNull(selectedButton)){
            selectedButton.setTextColor(getResources().getColor(R.color.white));
            if (lstQuestions[checkIndex].getAnswer() == recordedAnswers[checkIndex]) {
                //if the selected button is correct answer - show it as green background
                selectedButton.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                //the selected option was incorrect, hence red background
                selectedButton.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }

    }
}
