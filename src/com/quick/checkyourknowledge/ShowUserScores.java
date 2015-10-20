package com.quick.checkyourknowledge;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.quick.base.MockScoopBaseActivity;
import com.quick.connector.WebConnector;
import com.quick.global.Globals;
import com.quick.mockscoop.R;
import com.quick.mockscoop.RequestReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowUserScores extends MockScoopBaseActivity implements RequestReceiver {

    Activity a = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        a = getActivity();
        View rootView = inflater.inflate(R.layout.activity_show_user_scores, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        //get the data of user scores from the web
        Map<String, String> parameters = new HashMap<>();
        parameters.put(getString(R.string.fb_id), Globals.fb_id);

        try {
            WebConnector.getInstance().getUserScore(this, getActivity(), parameters);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void displayScoreInformation(String[] group, String[][] values) {

        ViewGroup viewGroup = (ViewGroup) getActivity().findViewById(R.id.linearLayout1);
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        android.widget.TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT);

        for (int index = 0; index < group.length;) {

            TextView textView = new TextView(getActivity());
            textView.setText(group[index]);
            textView.setTextSize(20);
            textView.setPadding(0, 10, 0, 0);
            viewGroup.addView(textView);

            //adding the headers
            TableLayout layout = new TableLayout(this.getActivity());
            layout.setLayoutParams(params);

            TableRow headerRow = new TableRow(this.getActivity());
            headerRow.setLayoutParams(params);

            TextView dateOfTestLabel = new TextView(this.getActivity());
            dateOfTestLabel.setLayoutParams(trparams);
            dateOfTestLabel.setText(getString(R.string.dateOfTestLabel));
            dateOfTestLabel.setPadding(0, 5, 0, 5);

            TextView scoreLabel = new TextView(this.getActivity());
            scoreLabel.setText(getString(R.string.scoreLabel));
            scoreLabel.setLayoutParams(trparams);
            scoreLabel.setPadding(30, 5, 30, 5);

            TextView averageTimeLabel = new TextView(this.getActivity());
            averageTimeLabel.setText(getString(R.string.avgTimePerQuestionLabel));
            averageTimeLabel.setLayoutParams(trparams);
            averageTimeLabel.setPadding(0, 5, 0, 5);

            headerRow.addView(dateOfTestLabel);
            headerRow.addView(scoreLabel);
            headerRow.addView(averageTimeLabel);

            layout.addView(headerRow);
            //check if the next category in the array is same, add next row here so that
            //in display duplicate data is not shown
            do {
                TableRow tableRow = new TableRow(this.getActivity());
                tableRow.setLayoutParams(params);
                //add date of test,score,avg time as text view here
                TextView dateOfTest = new TextView(this.getActivity());
                dateOfTest.setText(values[index][0]);
                dateOfTest.setGravity(Gravity.CENTER_HORIZONTAL);
                dateOfTest.setLayoutParams(trparams);
                dateOfTest.setPadding(0, 5, 0, 5);

                TextView score = new TextView(this.getActivity());
                score.setText(values[index][1]);
                score.setGravity(Gravity.CENTER_HORIZONTAL);
                score.setLayoutParams(trparams);
                score.setPadding(30, 5, 30, 5);

                TextView averageTime = new TextView(this.getActivity());
                averageTime.setText(values[index][2]);
                averageTime.setGravity(Gravity.CENTER_HORIZONTAL);
                averageTime.setLayoutParams(trparams);
                averageTime.setPadding(0, 5, 0, 5);

                tableRow.addView(dateOfTest);
                tableRow.addView(score);
                tableRow.addView(averageTime);


                layout.addView(tableRow);
                index++;
            } while ((index < group.length) && group[index - 1].equals(group[index]));

            View lineView = new View(this.getActivity());
            lineView.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,10
            ));
            lineView.setBackgroundColor(Color.parseColor("#B3B3B3"));
            viewGroup.addView(lineView);
            viewGroup.addView(layout);
        }

    }

    @Override
    public void receiveResponse(Object response) {

        System.out.println("Recent scores : " + response);

        /*
        sample response :
        [{"dateOfTest":"2015-09-12","categoryName":"C_Test","numOfQuestions":3,"timeOFTest":"14:11:34","score":0,"avgTimePerQuestion":1},
        {"dateOfTest":"2015-09-12","categoryName":"C_Test","numOfQuestions":2,"timeOFTest":"14:08:31","score":2,"avgTimePerQuestion":3},
        {"dateOfTest":"2015-09-12","categoryName":"null","numOfQuestions":0,"timeOFTest":"14:06:01","score":2,"avgTimePerQuestion":4},
        {"dateOfTest":"2010-03-28","categoryName":"SoftwareTesting","numOfQuestions":10,"timeOFTest":"04:37:02","score":6,"avgTimePerQuestion":10},
        {"dateOfTest":"2010-03-28","categoryName":"programmingIQ","numOfQuestions":15,"timeOFTest":"04:29:27","score":4,"avgTimePerQuestion":20}]
         */

        String[] displayGroups = null;
        String[][] displayValues = null;
        try {

            JSONArray recentScoresList = new JSONArray(String.valueOf(response));

            displayGroups = new String[recentScoresList.length()];
            displayValues = new String[recentScoresList.length()][3];

            for (int index = 0; index < recentScoresList.length(); index++) {

                String dateOfTest = recentScoresList.getJSONObject(index).getString(getString(R.string.dateOfTest));
                String categoryName = recentScoresList.getJSONObject(index).getString(getString(R.string.category));
                int numOfQuestions = recentScoresList.getJSONObject(index).getInt(getString(R.string.numberOfQuestions));
                String timeOfTest = recentScoresList.getJSONObject(index).getString(getString(R.string.timeOfTest));
                int score = recentScoresList.getJSONObject(index).getInt(getString(R.string.score));
                int avgTimePerQuestion = recentScoresList.getJSONObject(index).getInt(getString(R.string.avgTimePerQuestion));//in milliseconds

                displayGroups[index] = categoryName;
                displayValues[index][0] = dateOfTest;
                displayValues[index][1] = score + "/" + numOfQuestions;
                displayValues[index][2] = String.valueOf(avgTimePerQuestion);
            }

            displayScoreInformation(displayGroups, displayValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
