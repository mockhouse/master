package com.quick.mockscoop;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.quick.base.MockScoopBaseActivity;
import com.quick.category.Category;
import com.quick.connector.WebConnector;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowToppersPage extends MockScoopBaseActivity implements RequestReceiver {

    Activity a = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        a = getActivity();
        View rootView = inflater.inflate(R.layout.activity_show_leaders_page, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        List<Category> lstCategories = connector.getOrFetchCategories(null);//get categories from cache
        //get the data of user scores from the web
        String[] values = new String[lstCategories.size() + 1];
        values[0] = getString(R.string.selectCategory);
        int index = 1;
        for (Category category : lstCategories) {
            values[index++] = category.getCategory();
            System.out.println("Added :" + values[index - 1]);
        }
        Spinner spinnerCategoryNames = (Spinner) getActivity().findViewById(R.id.spinnerCategoryList);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), R.layout.spinner_category_names, values);
        spinnerCategoryNames.setAdapter(adapter);

        spinnerCategoryNames.setOnItemSelectedListener(new GetTopperForCategory());
    }

    private class GetTopperForCategory implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Spinner spinnerCategoryNames = (Spinner) getActivity().findViewById(R.id.spinnerCategoryList);
            String selectedItem = spinnerCategoryNames.getSelectedItem().toString();
            if (!getString(R.string.selectCategory).equals(selectedItem)) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put(getString(R.string.category_name), selectedItem);

                try {
                    WebConnector.getInstance().getTopperList(ShowToppersPage.this, getActivity(), parameters);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
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

    private void displayToppers(String[][] topperInformation) {

        ViewGroup topperList = (ViewGroup) getActivity().findViewById(R.id.topperList);
        topperList.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        android.widget.TableRow.LayoutParams trParams = new TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT);

        // add table headers
        TableLayout layout = new TableLayout(this.getActivity());
        layout.setLayoutParams(params);

        TableRow headerRow = new TableRow(this.getActivity());
        headerRow.setLayoutParams(params);

        TextView numberOfQuestionsLabel = new TextView(this.getActivity());
        numberOfQuestionsLabel.setLayoutParams(trParams);
        numberOfQuestionsLabel.setGravity(Gravity.LEFT);
        numberOfQuestionsLabel.setText(getString(R.string.numberOfQuestionsLabel));
        numberOfQuestionsLabel.setPadding(0, 5, 0, 5);

        TextView userNameLabel = new TextView(this.getActivity());
        userNameLabel.setLayoutParams(trParams);
        userNameLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        userNameLabel.setText(getString(R.string.userNameLabel));
        userNameLabel.setPadding(15, 5, 0, 5);

        TextView scoreLabel = new TextView(this.getActivity());
        scoreLabel.setText(getString(R.string.scoreLabel));
        scoreLabel.setLayoutParams(trParams);
        scoreLabel.setPadding(15, 5, 0, 5);

        TextView averageTimeLabel = new TextView(this.getActivity());
        averageTimeLabel.setText(getString(R.string.avgTimePerQuestionLabel));
        averageTimeLabel.setLayoutParams(trParams);
        averageTimeLabel.setLayoutParams(trParams);
        averageTimeLabel.setPadding(15, 5, 0, 5);

        headerRow.addView(numberOfQuestionsLabel);
        headerRow.addView(userNameLabel);
        headerRow.addView(scoreLabel);
        headerRow.addView(averageTimeLabel);

        layout.addView(headerRow);

        topperList.addView(layout);

        //now adding data entry

        for (int index = 0; index < topperInformation.length; index++) {

            TableRow tableRow = new TableRow(this.getActivity());
            tableRow.setLayoutParams(params);
            //add date of test,score,avg time as text view here
            TextView numberOfQuestions = new TextView(this.getActivity());
            numberOfQuestions.setText(topperInformation[index][0]);
            numberOfQuestions.setGravity(Gravity.CENTER_HORIZONTAL);
            numberOfQuestions.setLayoutParams(trParams);
            numberOfQuestions.setPadding(0, 5, 0, 5);

            TextView userName = new TextView(this.getActivity());
            userName.setText(topperInformation[index][1]);
            userName.setGravity(Gravity.CENTER_HORIZONTAL);
            userName.setLayoutParams(trParams);
            userName.setPadding(15, 5, 0, 5);

            TextView score = new TextView(this.getActivity());
            score.setText(topperInformation[index][2]);
            score.setGravity(Gravity.CENTER_HORIZONTAL);
            score.setLayoutParams(trParams);
            score.setPadding(15, 5, 0, 5);

            TextView averageTime = new TextView(this.getActivity());
            averageTime.setText(topperInformation[index][3]);
            averageTime.setGravity(Gravity.CENTER_HORIZONTAL);
            averageTime.setLayoutParams(trParams);
            averageTime.setPadding(15, 5, 0, 5);

            tableRow.addView(numberOfQuestions);
            tableRow.addView(userName);
            tableRow.addView(score);
            tableRow.addView(averageTime);

            layout.addView(tableRow);
        }

    }

    @Override
    public void receiveResponse(Object response) {
        System.out.println("Toppers response : " + response);
        /**sample response:
         * [{"num_of_ques":190,"username":"sreemanreddy","score":216,"time_per_ques":5217},
         * {"num_of_ques":228,"username":"nj456","score":196,"time_per_ques":11786},
         * {"num_of_ques":180,"username":"dass","score":191,"time_per_ques":6274},
         * {"num_of_ques":208,"username":"aniket20000","score":133,"time_per_ques":7242},
         * {"num_of_ques":165,"username":"rajeshtalreja","score":128,"time_per_ques":6465}]
         */
        String[][] displayValues = null;
        try {

            JSONArray topperList = new JSONArray(String.valueOf(response));

            displayValues = new String[topperList.length()][4];

            for (int index = 0; index < topperList.length(); index++) {
                int numOfQuestions = topperList.getJSONObject(index).getInt(getString(R.string.num_of_ques));
                String userName = topperList.getJSONObject(index).getString(getString(R.string.userName));
                int score = topperList.getJSONObject(index).getInt(getString(R.string.score));
                int avgTimePerQuestion = topperList.getJSONObject(index).getInt(getString(R.string.time_per_ques));//in milliseconds


                displayValues[index][0] = String.valueOf(numOfQuestions);
                displayValues[index][1] = userName;
                displayValues[index][2] = String.valueOf(score);
                displayValues[index][3] = String.valueOf(avgTimePerQuestion);
            }

            displayToppers(displayValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
