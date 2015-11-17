package com.quick.checkyourknowledge;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.quick.base.MockScoopBaseActivity;
import com.quick.category.Category;
import com.quick.connector.WebConnector;
import com.quick.global.Globals;
import com.quick.mockscoop.R;
import com.quick.mockscoop.RequestReceiver;
import com.quick.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowRank extends MockScoopBaseActivity implements RequestReceiver {
    Activity a = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        a = getActivity();
        View rootView = inflater.inflate(R.layout.activity_show_rank, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Category> lstCategories = connector.getOrFetchCategories(null);//get it from cache
        //get the data of user scores from the web

        String[] values = new String[lstCategories.size() + 1];
        values[0] = getString(R.string.selectCategory);
        int index = 1;
        for (Category category : lstCategories) {
         	String category_name=category.getCategory().replace("_"," ");
        	category_name=category_name.replace("plus","+");
            values[index++] = category_name;
            System.out.println("Added :" + values[index - 1]);
        }
        Spinner spinnerCategoryNames = (Spinner) getActivity().findViewById(R.id.spinnerCategoryList);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), R.layout.spinner_category_names, values);
        spinnerCategoryNames.setAdapter(adapter);

        spinnerCategoryNames.setOnItemSelectedListener(new ShowRankInCategory());

    }

    private class ShowRankInCategory implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Spinner spinnerCategoryNames = (Spinner) getActivity().findViewById(R.id.spinnerCategoryList);
            String selectedItem = spinnerCategoryNames.getSelectedItem().toString();
            selectedItem = selectedItem.replace(" ","_");
            selectedItem=selectedItem.replace("+","plus");
            if (!getString(R.string.selectCategory).equals(selectedItem)) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put(getString(R.string.category_name), selectedItem);
                parameters.put(getString(R.string.userName), Globals.userName);

                try {
                    WebConnector.getInstance().getUserRank(ShowRank.this, getActivity(), parameters);
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

    private void displayRankInformation(String[][] rankInformation) {

        final TextView categoryRank = (TextView) getActivity().findViewById(R.id.textViewCategoryRank);
        final TextView score = (TextView) getActivity().findViewById(R.id.textViewCategoryScore);

        if(rankInformation == null) {
            //some error occured while getting the information.
            categoryRank.setText("NA");
            score.setText("NA");
        } else {

            categoryRank.setText(rankInformation[0][0]);
            score.setText(rankInformation[0][1]);

            performTextNumberAnimation(categoryRank, 0, Integer.valueOf(rankInformation[0][0]), 75, 1500);
            performTextNumberAnimation(score, 0, Integer.valueOf(rankInformation[0][1]),75,1500);
        }


    }

    private void performTextNumberAnimation(final TextView textView,int startNumber, int endNumber, final int maxFontSize, int duration) {

        final ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(startNumber, endNumber);
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                textView.setText(String.valueOf(currentValue));
                textView.setTextSize(maxFontSize);
                /*if (currentValue < maxFontSize)
                    textView.setTextSize(currentValue + 1);*/
            }
        });
        animator.start();
    }

        @Override
    public void receiveResponse(Object response) {

            System.out.println("User Rank response : " + response);
            String strResponse = String.valueOf(response);
            String[][] displayValues = null;
            boolean isError = false;
            if (Util.isNullString(strResponse) || Util.isNullOrBlank(strResponse) || getString(R.string.no_record_found).equalsIgnoreCase(strResponse)) {
                isError = true;
            } else {
                /***
                 * Sample Response :
                 * [{"rank":70,"score":20}]
                 */

                try {

                    JSONArray topperList = new JSONArray(String.valueOf(response));
                    displayValues = new String[topperList.length()][2];
                    int categoryRank = topperList.getJSONObject(0).getInt(getString(R.string.rank));
                    int totalScore = topperList.getJSONObject(0).getInt(getString(R.string.score));


                    displayValues[0][0] = String.valueOf(categoryRank);
                    displayValues[0][1] = String.valueOf(totalScore);


                } catch (JSONException e) {
                    e.printStackTrace();
                    isError = true;
                }
            }
            if(isError) {
                displayValues = null;
            }

            displayRankInformation(displayValues);

        }
}
