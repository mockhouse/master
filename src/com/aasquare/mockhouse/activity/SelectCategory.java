package com.aasquare.mockhouse.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.aasquare.mockhouse.base.MockScoopBaseActivity;
import com.aasquare.mockhouse.category.Category;
import com.aasquare.mockhouse.global.Globals;
import com.aasquare.mockhouse.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SelectCategory extends MockScoopBaseActivity implements RequestReceiver {
    private Activity currentActivity = null;
    private SelectCategory classObj;
    private List<ToggleButton> allButtons = new ArrayList<>();
    private Map<String,Integer> mapCategoryGroups = new HashMap<>();
    public final static String SELECTED_CATEGORY = "SELECTED_CATEGORY";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	FragmentManager fragmentManager = getFragmentManager();
        
        
 
        currentActivity = getActivity();
        View rootView = inflater.inflate(R.layout.activity_select_category, container, false);
        classObj = this;
        return rootView;
    }
    @Override
    public void onStop() {

        super.onStop();
        Log.e("ajs","stopping");
        //Code to stop quiz when any interruption comes eg home key,call,alarm
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        List<Category> lstCategories = null;
        lstCategories = connector.getOrFetchCategories(null);//get value from cache, if values not found in cache, i.e server is not reachable.
        if (Util.isNullOrEmpty(lstCategories)) {
            //Data not found, move to the error page.
            ErrorPage errorPage = new ErrorPage();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, errorPage);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        //showCategoryGroups(lstCategories);
        ViewGroup categoryGroupLayout = (ViewGroup) currentActivity.findViewById(R.id.quiz_category_group);
        mapCategoryGroups = generateMap(lstCategories);
        generateButtons(mapCategoryGroups, categoryGroupLayout);
    }

    private Map<String,Integer> generateMap(List<Category> lstCategories) {
        int totalCount =0;
        Map<String,Integer> mapCategoryGroups = new HashMap<>();
        for(Category cat : lstCategories) {
        	if((cat.getCategory().equals("myTest")&&!(Globals.userName.equals("Aasquare"))))
        		continue;
            String key  = Util.decorateDisplayText(cat.getType());
            int count = 1;
            if(mapCategoryGroups.containsKey(key)) {
                count = mapCategoryGroups.get(key) + 1;
            }
            totalCount++;
            mapCategoryGroups.put(key, count);
        }
        mapCategoryGroups.put(getString(R.string.all), totalCount);

        return sortByComparator(mapCategoryGroups,false);
    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {

                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private void generateButtons(Map<String,Integer> values, ViewGroup parent) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 10);
        int index = 0;
        for (Map.Entry<String,Integer> category : values.entrySet()) {
            String categoryType = category.getKey() + " (" + category.getValue() +")";

                final ToggleButton mBtn = new ToggleButton(getActivity());
                mBtn.setText(categoryType);
                mBtn.setTextOn(categoryType);
                mBtn.setTextOff(categoryType);
                mBtn.setClickable(true);
                mBtn.setChecked(false);
                mBtn.setTag(category.getKey());
                mBtn.setId(++index);
                mBtn.setPadding(10, 10, 10, 10);
                mBtn.setBackgroundResource(R.drawable.button_transperant);
                allButtons.add(mBtn);
                mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectToggleButton(mBtn);
                    }
                });
                parent.addView(mBtn,params);


            }

    }


/*    private void showCategoryGroups(List<Category> categories) {

        Spinner spinnerCategoryGroups = (Spinner) getActivity().findViewById(R.id.spinnerCategoryGroup);
        List<String> groups = new ArrayList<>();
        groups.add(getString(R.string.all));
        for (Category category : categories) {
            if (!groups.contains(category.getType())) {
                groups.add(category.getType());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), R.layout.spinner_category_names, groups.toArray());
        spinnerCategoryGroups.setAdapter(adapter);
        spinnerCategoryGroups.setOnItemSelectedListener(new FilterCategories());
    }*/

    private void selectToggleButton(ToggleButton selectedButton) {

        if (allButtons != null && !allButtons.isEmpty()) {
            for (ToggleButton btn : allButtons) {
                btn.setChecked(Boolean.FALSE);
                btn.setBackgroundResource(R.drawable.button_transperant);
                btn.setTextColor(getResources().getColor(R.color.appThemeColor));
            }
        }
        selectedButton.setChecked(Boolean.TRUE);
        selectedButton.setTextColor(Color.WHITE);
        selectedButton.setBackgroundColor(getResources().getColor(R.color.appThemeColor));

        SelectQuiz selectQuiz = new SelectQuiz();
        currentActivity.setTitle(R.string.select_quiz);
        Bundle bundle = new Bundle();
        bundle.putString(SELECTED_CATEGORY, String.valueOf(selectedButton.getTag()));
        selectQuiz.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
        fragmentTransaction.replace(R.id.frame_container, selectQuiz).addToBackStack(null).commit();

    }



    @Override
    public void receiveResponse(Object response) {

    }
}
