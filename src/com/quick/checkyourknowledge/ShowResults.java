package com.quick.checkyourknowledge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.quick.connector.WebConnector;
import com.quick.global.Globals;
import com.quick.mockscoop.MainActivity;
import com.quick.mockscoop.R;
import com.quick.mockscoop.RequestReceiver;
import com.quick.mockscoop.SelectQuiz;
import com.quick.questions.Question;
import com.quick.result.ResultsDbHelper;
import com.quick.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ShowResults extends Fragment implements RequestReceiver {

    public boolean alreadySaved=false;
    private ResultsDbHelper db = null;
    Activity a = null;


    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        a = getActivity();
        db = ResultsDbHelper.getDbHelper(a);
        View rootView = inflater.inflate(R.layout.activity_show_results, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Question[] attemptedQuestions = Globals.attemptedQuestions;
        int[] recordedAnswers = Globals.recordedAnswers;
        long[] startTime = Globals.startTime;
        long[] duration = Globals.duration;

        //  db.insertResult(Globals.userName, attemptedQuestions, recordedAnswers, startTime, duration);
        a.findViewById(R.id.btnRestartQuiz).setOnClickListener(restartQuiz);
        if (attemptedQuestions == null || attemptedQuestions.length <= 0 || recordedAnswers == null ||
                recordedAnswers.length <= 0 || attemptedQuestions.length != recordedAnswers.length) {

            //data not found or data invalid.
            SelectQuiz sq = new SelectQuiz();
            a.setTitle(R.string.home);
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, sq);
            fragmentTransaction.commit();
            return;

        }

        ViewGroup results = (ViewGroup) a.findViewById(R.id.resultList);

        int index = 0;
        int correct = 0;
        long totalQuizTime = 0;
        for (final Question question : attemptedQuestions) {

            TextView questionText = new TextView(a);
            questionText.setText((index + 1) + ". " + question.getQuestion());
            TextView answer = new TextView(a);
            TextView correct_answer = new TextView(a);
//         final Button btnShowCorrectAnswer = new Button(this);
            String answerText = question.getOptionFromIndex(recordedAnswers[index]);
            if (answerText == null || "null".equalsIgnoreCase(answerText))
            {
                answerText = getString(R.string.skipped);
                		correct++;
            }

            else {
                totalQuizTime += Util.timeInSeconds(duration[index], TimeUnit.MILLISECONDS);
                answerText += Util.OPEN_BRACKET + Util.timeInSecondsAsString(duration[index], TimeUnit.MILLISECONDS) + Util.CLOSE_BRACKET;
            }
            answer.setText(answerText);

            //mark answer in green if correct , else red
            Boolean isCorrectAnswer = question.getAnswer() == recordedAnswers[index];
            if (!isCorrectAnswer) {

                answer.setTextColor(Color.RED);
                correct--;
                //correct_answer.setText(question.getOptionFromIndex(question.getAnswer()));
                //correct_answer.setTextColor(Color.GREEN);

            } else {
                answer.setTextColor(Color.GREEN);
                correct+=4;;
            }


            answer.setPadding(10, 0, 0, 10);
            results.addView(questionText);
            results.addView(answer);
            if (!isCorrectAnswer) {
                correct_answer.setPadding(10, 0, 0, 10);
                results.addView(correct_answer);

            }
            index++;

        }
        TextView timeSummary = new TextView(a);
        timeSummary.setPadding(0, 25, 0, 0);
        TextView score = new TextView(a);
        score.setPadding(0, 25, 0, 0);
        score.setText("Scored " + correct + " out of " + index);
        score.setTextColor(Color.GREEN);
        timeSummary.setText(getString(R.string.totalTime) + " : " + Util.timeAsString(totalQuizTime, TimeUnit.SECONDS));
        results.addView(timeSummary);
        results.addView(score);
        if(alreadySaved == false)
        {
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

            WebConnector.getInstance().saveUserScore(this, getActivity(), scoreDetails,true);

            //db.saveDataToWeb(Globals.userName, attemptedQuestions, recordedAnswers, startTime, duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        else
        	alreadySaved=false;


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    OnClickListener restartQuiz = new OnClickListener() {


        @Override
        public void onClick(View v) {
            SelectQuiz sq = new SelectQuiz();
            a.setTitle(R.string.home);
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, sq);
            fragmentTransaction.commit();

        }
    };

    @Override
    public void receiveResponse(Object response) {

        System.out.println("Saving result response : " + response);
    }
}
