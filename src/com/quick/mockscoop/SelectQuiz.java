package com.quick.mockscoop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.quick.base.MockScoopBaseActivity;
import com.quick.category.Category;
import com.quick.questions.Question;
import com.quick.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SelectQuiz extends MockScoopBaseActivity implements RequestReceiver {

    Activity currentActivity = null;
    SelectQuiz classObj;
    public final static String SELECTED_QUIZ = "SELECTED_QUIZ";
    public final static String QUESTION_LIST = "QUESTION_LIST";
    private List<ToggleButton> allButtons = new ArrayList<>();
    private String selectQuizGroupText = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentActivity = getActivity();
        View rootView = inflater.inflate(R.layout.activity_select_quiz, container, false);
        classObj = this;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        //ViewGroup selectAreaButtonLayout = (ViewGroup) currentActivity.findViewById(R.id.quiz_radio_group);
        ViewGroup selectAreaButtonLayout = (ViewGroup) currentActivity.findViewById(R.id.quiz_button_group);

        String selectedCategory = getArguments().getString(SelectCategory.SELECTED_CATEGORY);

        List<Category> lstCategories = null;
        lstCategories = connector.getOrFetchCategories(null);//get value from cache, if values not found in cache, i.e server is not reachable.
        if (Util.isNullOrEmpty(lstCategories)) {
            //Data not found, move to the error page.
            ErrorPage errorPage = new ErrorPage();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, errorPage);
            fragmentTransaction.commit();
        }

        //showCategoryGroups(lstCategories);
        lstCategories = filterCategories(lstCategories,selectedCategory);
        showCategories(lstCategories, selectAreaButtonLayout);
    }

    private List<Category> filterCategories(List<Category> categories,String selectedCategory)  {

        if(Util.isNullOrBlank(selectedCategory)){
            return new ArrayList<>();
        }

        if (!getString(R.string.all).equals(selectedCategory)) {
            //filter the list to have only those categories which are for selected category group
            Iterator<Category> iterator = categories.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().getType().equals(selectedCategory)) {
                    iterator.remove();
                }
            }
        }
        return categories;
    }
    private void showCategories(List<Category> categories, ViewGroup parent) {

        List<String> categoryInfo = new ArrayList<>();
        for (Category categoryData : categories) {
            categoryInfo.add(categoryData.getStringDescription());
        }
        Collections.sort(categoryInfo, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {

                return (lhs.length() - rhs.length());
            }
        });

        generateButtons(categoryInfo, parent);
    }

    private void generateButtons(List<String> values, ViewGroup parent) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 10, 20);
        LinearLayout mNewLayout = new LinearLayout(getActivity()); // Horizontal layout which I am using to add my buttons.
        mNewLayout.setOrientation(LinearLayout.HORIZONTAL);
        int mButtonsSize = 0;
        Rect bounds = new Rect();
        int index = 0;
        for (String area : values) {
            final ToggleButton mBtn = new ToggleButton(getActivity());
            String buttonText = Util.decorateDisplayText(area);
            mBtn.setText(buttonText);
            mBtn.setTextOn(buttonText);
            mBtn.setTextOff(buttonText);
            mBtn.setClickable(true);
            mBtn.setChecked(false);
            mBtn.setId(++index);
            mBtn.setPadding(10, 10, 10, 10);
            mBtn.setBackgroundResource(R.drawable.button_transperant);
            allButtons.add(mBtn);
            mBtn.setOnClickListener(ocl);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point mScreenSize = new Point();
            display.getSize(mScreenSize);
            Paint textPaint = mBtn.getPaint();
            textPaint.getTextBounds(buttonText, 0, buttonText.length(), bounds);
            // size of all buttons in current horizontal layout
            // i am using +45 because of extra padding which is set in xml for this layout
            mButtonsSize += bounds.width() + 50;
            if (mButtonsSize < (mScreenSize.x - 100)) { // -32 because of extra padding in main layout.
                mNewLayout.addView(mBtn, params);
            } else {
                parent.addView(mNewLayout);
                mNewLayout = new LinearLayout(getActivity());
                mNewLayout.setOrientation(LinearLayout.HORIZONTAL);
                mButtonsSize = bounds.width();
                mNewLayout.addView(mBtn, params); // add button to currentActivity new layout so it won't be stretched because of it's width.
            }
        }
        parent.addView(mNewLayout); // add the last layout/ button.
    }

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
        selectQuizGroupText = selectedButton.getText().toString();
    }


    String field = null;
    OnClickListener ocl = new OnClickListener() {
        @Override
        public void onClick(View v) {

            {
                // get prompts.xml view
                final ToggleButton clickedButton = (ToggleButton)currentActivity.findViewById(v.getId());
                selectToggleButton(clickedButton);
                LayoutInflater li = LayoutInflater.from(currentActivity);
                View promptsView = li.inflate(R.layout.prompt, null);
                final SelectQuiz sq = classObj;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        currentActivity);
                // set prompts.xml to alert dialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        Intent intent = new Intent(currentActivity, SelectQuiz.class);
                                        //RadioGroup selectQuizGroup = (RadioGroup) currentActivity.findViewById(R.id.quiz_radio_group);
                                        //System.out.println("selectQuiz :" + selectQuizGroup + "," + selectQuizGroup.getCheckedRadioButtonId());
                                        // find the radio button by returned id
                                        //RadioButton selectedQuiz = (RadioButton) currentActivity.findViewById(selectQuizGroup.getCheckedRadioButtonId());
                                        System.out.println("selectQuizGroupText :" + selectQuizGroupText);
                                        field = Util.unDecorateDisplayText(selectQuizGroupText);
                                        try {
                                            Map<String, String> parameters = new HashMap<>();
                                            parameters.put(getString(R.string.category_name), field);
                                            parameters.put(getString(R.string.num_of_ques), userInput.getText().toString());
                                            connector.getQuestions(sq, currentActivity, parameters);
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        clickedButton.setChecked(Boolean.FALSE);
                                        clickedButton.setBackgroundResource(R.drawable.button_transperant);
                                        clickedButton.setTextColor(getResources().getColor(R.color.appThemeColor));
                                        dialog.cancel();

                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }
    };

    @Override
    public void receiveResponse(Object response) {

        /*RadioGroup selectQuizGroup = (RadioGroup) currentActivity.findViewById(R.id.quiz_radio_group);
        RadioButton selectedQuiz = (RadioButton) currentActivity.findViewById(selectQuizGroup.getCheckedRadioButtonId());
        String selectedCategory = selectedQuiz.getText().toString();
        List<Question> questions = connector.getOrFetchQuestions(selectedCategory, String.valueOf(response));*/
        List<Question> questions = connector.getOrFetchQuestions(selectQuizGroupText, String.valueOf(response));
        if (Util.isNullOrBlank((String) response)) {
            //Data not found, move to the error page.
            ErrorPage errorPage = new ErrorPage();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, errorPage);
            fragmentTransaction.commit();
            return;
        }
        LaunchQuiz lq = new LaunchQuiz();
        currentActivity.setTitle(R.string.launch_quiz);
        Bundle bundle = new Bundle();
        bundle.putString(SELECTED_QUIZ, field);
        lq.setQuestionList(questions);
        lq.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);

        fragmentTransaction.replace(R.id.frame_container, lq,"LaunchQuiz");
        fragmentTransaction.commit();
    }


}
