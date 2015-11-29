package com.aasquare.mockhouse.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aasquare.mockhouse.base.MockScoopBaseActivity;
import com.aasquare.mockhouse.checkyourknowledge.ShowResults;
import com.aasquare.mockhouse.connector.WebConnector;
import com.aasquare.mockhouse.global.Globals;
import com.aasquare.mockhouse.questions.Question;
import com.aasquare.mockhouse.questions.QuestionDbHelper;
import com.aasquare.mockhouse.util.GuiUtil;
import com.aasquare.mockhouse.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LaunchQuiz extends MockScoopBaseActivity implements RequestReceiver {

    public static final int ID_ONE = 1;
    public boolean test_interuppted = false;
    public boolean intentional_end = false;
    QuestionDbHelper questionDbHelper = null;
    Question[] lstQuestions;
    int[] recordedAnswers;
    long[] startTime;
    long[] duration;
    ViewGroup questionListLayout;
    ViewGroup optionListLayout;
    int nextQuestionIndex = 1;
    TextView header;
    Activity a = null;
    String selectedCategory;
    List<Question> allQuestions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        a = getActivity();
        View rootView = inflater.inflate(R.layout.activity_launch_quiz, container, false);
        return rootView;
    }

    public void setQuestionList(List<Question> allQuestions) {

        this.allQuestions = allQuestions;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        selectedCategory = getArguments().getString(SelectQuiz.SELECTED_QUIZ);
        questionListLayout = (ViewGroup) a.findViewById(R.id.questionList);
        // questionAnswerRadioGroup = (RadioGroup) a.findViewById(R.id.questionAnswerOptions);
        optionListLayout = (LinearLayout) a.findViewById(R.id.optionList);
        header = (TextView) a.findViewById(R.id.textViewHeader);
        questionDbHelper = QuestionDbHelper.getDBHelper(a);
        //List<Question> allQuestions = connector.getOrFetchQuestions(selectedCategory, null);//get questions for the category from the cache
        a.findViewById(R.id.btnSkipQuestions).setOnClickListener(skipQuestion);
        //a.findViewById(R.id.btnNextQuestion).setOnClickListener(nextQuestion);
        a.findViewById(R.id.btnEndQuiz).setOnClickListener(endQuiz);
        if (allQuestions == null || allQuestions.isEmpty()) {
            header.setText(R.string.noQuestionsAvailable);
            //a.findViewById(R.id.btnNextQuestion).setOnClickListener(nextQuestion);
            a.findViewById(R.id.btnEndQuiz).setOnClickListener(endQuiz);
            a.findViewById(R.id.btnSkipQuestions).setVisibility(View.INVISIBLE);
            a.findViewById(R.id.btnSkipQuestions).setOnClickListener(skipQuestion);
            return;
        }
        lstQuestions = allQuestions.toArray(new Question[allQuestions.size()]);
        startTime = new long[allQuestions.size()];
        duration = new long[allQuestions.size()];
        recordedAnswers = new int[lstQuestions.length];
        header.setText("Questions " + nextQuestionIndex + "/" + lstQuestions.length);
        showQuestion(lstQuestions, questionListLayout, optionListLayout, nextQuestionIndex++);
    }

    public void endQuiz() {

        Globals.attemptedQuestions = lstQuestions;
        Globals.recordedAnswers = recordedAnswers;
        Globals.selectedCategory = selectedCategory;
        Globals.attemptedQuestions = lstQuestions;
        Globals.recordedAnswers = recordedAnswers;
        Globals.startTime = startTime;
        Globals.duration = duration;
        String msg = "";
        Globals.attemptedQuestions = lstQuestions;
        Globals.recordedAnswers = recordedAnswers;
        Globals.selectedCategory = selectedCategory;
        Globals.attemptedQuestions = lstQuestions;
        Globals.recordedAnswers = recordedAnswers;
        Globals.startTime = startTime;
        Globals.duration = duration;
        Question[] attemptedQuestions = Globals.attemptedQuestions;
        int[] recordedAnswers = Globals.recordedAnswers;
        long[] startTime = Globals.startTime;
        long[] duration = Globals.duration;
        if (attemptedQuestions == null || attemptedQuestions.length <= 0 || recordedAnswers == null ||
                recordedAnswers.length <= 0 || attemptedQuestions.length != recordedAnswers.length) {
            //data not found or data invalid.
            return;
        }
        int index = 0;
        int correct = 0;
        long totalQuizTime = 0;
        for (final Question question : attemptedQuestions) {
            String answerText = question.getOptionFromIndex(recordedAnswers[index]);
            if (answerText == null || "null".equalsIgnoreCase(answerText)) {
                answerText = getString(R.string.skipped);
                correct++;
            } else {
                totalQuizTime += Util.timeInSeconds(duration[index], TimeUnit.MILLISECONDS);
                answerText += Util.OPEN_BRACKET + Util.timeInSecondsAsString(duration[index], TimeUnit.MILLISECONDS) + Util.CLOSE_BRACKET;
            }
            //mark answer in green if correct , else red
            Boolean isCorrectAnswer = question.getAnswer() == recordedAnswers[index];
            if (!isCorrectAnswer) {
                correct--;
                //correct_answer.setText(question.getOptionFromIndex(question.getAnswer()));
                //correct_answer.setTextColor(Color.GREEN);
            } else {
                correct += 4;
                ;
            }
        }
        index++;
        Map<String, String> scoreDetails = new HashMap<>();
        try {
            long avgTimePerQuestion = totalQuizTime / recordedAnswers.length;// total time in milliseconds/total number of questions attempted
            scoreDetails.put(getString(R.string.userName), Globals.userName);
            scoreDetails.put(getString(R.string.fb_id), Globals.fb_id);
            scoreDetails.put(getString(R.string.category), Globals.selectedCategory);
            scoreDetails.put(getString(R.string.numberOfQuestions), Integer.toString(recordedAnswers.length));
            scoreDetails.put(getString(R.string.score), Integer.toString(correct));
            scoreDetails.put(getString(R.string.avgTimePerQuestion), Long.toString(avgTimePerQuestion));//in milliseconds
            scoreDetails.put(getString(R.string.dateOfTest), Util.getOnlyDateFromLong(startTime[0]));
            scoreDetails.put(getString(R.string.timeOfTest), Util.getOnlyTimeFromLong(startTime[0]));
            WebConnector.getInstance().saveUserScore(LaunchQuiz.this, a, scoreDetails, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify currentActivity parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    OnClickListener skipQuestion = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            showNextQuestion(v, 0, true, optionListLayout);
        }
    };

    public void endQuiz(View view) {

        ShowResults sr = new ShowResults();
        sr.alreadySaved = true;
        a.setTitle(R.string.quiz_results);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
        fragmentTransaction.replace(R.id.frame_container, sr);
        fragmentTransaction.commit();
    }

    public void quizFinished(View view) {

        ShowResults sr = new ShowResults();
        a.setTitle(R.string.quiz_results);
        Globals.attemptedQuestions = lstQuestions;
        Globals.recordedAnswers = recordedAnswers;
        Globals.selectedCategory = selectedCategory;
        Globals.startTime = startTime;
        Globals.duration = duration;
        intentional_end = true;
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
        fragmentTransaction.replace(R.id.frame_container, sr);
        fragmentTransaction.commit();
    }

    OnClickListener endQuiz = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //ending the quiz in between as opted by the user
            ShowResults sr = new ShowResults();
            a.setTitle(R.string.quiz_results);
            Globals.attemptedQuestions = lstQuestions;
            Globals.recordedAnswers = recordedAnswers;
            Globals.selectedCategory = selectedCategory;
            Globals.startTime = startTime;
            Globals.duration = duration;
            intentional_end = true;
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
            fragmentTransaction.replace(R.id.frame_container, sr);
            fragmentTransaction.commit();
        }
    };
    OnClickListener nextQuestion = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //showNextQuestion(v, false);
        }
    };

    public void showNextQuestion(View view, int selectedAnswer, boolean skipCurrentQuestion, ViewGroup optionList) {

        if (!skipCurrentQuestion) {
            //first record the current answer & then load,show the next question
            recordedAnswers[nextQuestionIndex - 2] = selectedAnswer;
            duration[nextQuestionIndex - 2] = System.currentTimeMillis() - startTime[nextQuestionIndex - 2];
            System.out.println("Ans " + (nextQuestionIndex - 1) + " : " + selectedAnswer);
        }
        //since the skip flag is on, don't record current question answers
        //remove the previous options , question text would be replaced when next question would be displayed
        optionList.removeAllViews();
        System.out.println("lstQuestions.length :" + lstQuestions.length);
        System.out.println("nextQuestionIndex:" + nextQuestionIndex);
        if (lstQuestions.length < nextQuestionIndex) {
            //now move to the next activity and display the results
            quizFinished(view);
            return;
        }
        header.setText("Questions " + nextQuestionIndex + "/" + lstQuestions.length);
        showQuestion(lstQuestions, questionListLayout, optionList, nextQuestionIndex++);
        // Button nextQuestions = (Button) a.findViewById(R.id.btnNextQuestion);
        //nextQuestions.setEnabled(false);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onStop() {

        super.onStop();
        //Code to stop quiz when any interruption comes eg home key,call,alarm
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.e("aman", "saving scores");
        if (!intentional_end)
            endQuiz();
        else
            intentional_end = false;
    }



    private void showQuestion(Question[] questionList, ViewGroup questionListLayout, ViewGroup optionList, int questionIndex) {

        TextView questionText = null;
        Button optionButton = null;
        EnableNextButton enableButton = new EnableNextButton(a);
        Question question = questionList[questionIndex - 1];
        System.out.println("Current Questions : " + question);
        ((TextView) a.findViewById(R.id.questionText)).setText(question.getQuestion());
        GuiUtil.createAnswerOptionButtons(question, R.drawable.button_transperant, enableButton, a, optionList, getString(R.string.invalid_options));
        //start the timers
        startTime[questionIndex - 1] = System.currentTimeMillis();
    }

    private class EnableNextButton implements View.OnClickListener {

        private Activity activity;

        public EnableNextButton(Activity activity) {

            this.activity = activity;
        }

        public void onClick(View v) {

            Button clickedButton = (Button) activity.findViewById(v.getId());
            if (!Util.isNull(clickedButton)) {
                int selectedAnswer = clickedButton.getId();
                clickedButton.setTextColor(getResources().getColor(R.color.white));
                //clickedButton.setTextColor(Color.WHITE);
                //if correct answer, show the green ,else red
                if(lstQuestions[nextQuestionIndex - 2].getAnswer() == selectedAnswer) {
                    clickedButton.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    clickedButton.setBackgroundColor(getResources().getColor(R.color.red));
                }
                //showNextQuestion(v, clickedButton.getId(), false, optionListLayout);
                showNextQuestionWithDelay(v, selectedAnswer, false, optionListLayout);
            }
        }
    }


    public void showNextQuestionWithDelay(final View view, final int selectedAnswer, boolean skipCurrentQuestion, ViewGroup optionList) {

        Runnable r = new Runnable() {
            @Override
            public void run(){
                showNextQuestion(view, selectedAnswer, false, optionListLayout);
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, Integer.valueOf(getString(R.string.fixed_delay_1000))); // <-- the "1000" is the delay time in milliseconds.
    }

    @Override
    public void receiveResponse(Object response) {
    }
}
