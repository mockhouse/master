package com.quick.mockscoop;

import com.quick.base.MockScoopBaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;



import com.quick.category.Category;
import com.quick.global.Globals;
import com.quick.questions.Question;
import com.quick.util.Util;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SelectQuiz extends MockScoopBaseActivity implements RequestReceiver {
    Activity a = null;
    SelectQuiz classObj;
    public final static String SELECTED_QUIZ = "SELECTED_QUIZ";
    public final static String QUESTION_LIST = "QUESTION_LIST";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        a = getActivity();
        View rootView = inflater.inflate(R.layout.activity_select_quiz, container, false);
        classObj = this;
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewGroup selectAreaButtonLayout = (ViewGroup) a.findViewById(R.id.quiz_radio_group);
        List<Category> lstCategories=null;
    
        lstCategories = connector.getOrFetchCategories(null);//get value from cache, if values not found in cache, i.e server is not reachable.

        if(Util.isNullOrEmpty(lstCategories)) {
            //Data not found, move to the error page.
            ErrorPage errorPage = new ErrorPage();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, errorPage);
            fragmentTransaction.commit();
        }

        showCategoryGroups(lstCategories);
        showCategories(lstCategories, selectAreaButtonLayout);
    }

    private void showCategoryGroups(List<Category> categories) {

        Spinner spinnerCategoryGroups = (Spinner) getActivity().findViewById(R.id.spinnerCategoryGroup);
        List<String> groups = new ArrayList<>();
        groups.add(getString(R.string.all));
        for(Category category : categories) {
            if(!groups.contains(category.getType())) {
                groups.add(category.getType());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), R.layout.spinner_category_names, groups.toArray());
        spinnerCategoryGroups.setAdapter(adapter);
        spinnerCategoryGroups.setOnItemSelectedListener(new FilterCategories());
    }

    private class FilterCategories implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Spinner spinnerCategoryNames = (Spinner) getActivity().findViewById(R.id.spinnerCategoryGroup);
            String selectedItem = spinnerCategoryNames.getSelectedItem().toString();

            ViewGroup selectAreaButtonLayout = (ViewGroup) a.findViewById(R.id.quiz_radio_group);
            selectAreaButtonLayout.removeAllViews();
            List<Category> lstAreas = connector.getOrFetchCategories(null);//get values from cache

            if (!getString(R.string.all).equals(selectedItem)) {
                //filter the list to have only those categories which are for selected category group
                Iterator<Category> iterator = lstAreas.iterator();
                while (iterator.hasNext()) {
                    if(!iterator.next().getType().equals(selectedItem)) {
                        iterator.remove();
                    }
                }
            }
            showCategories(lstAreas, selectAreaButtonLayout);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }

    }
    private void showCategories(List<Category> categories, ViewGroup parent) {

        List<String> categoryInfo = new ArrayList<>();
        for (Category categoryData : categories) {
            categoryInfo.add(categoryData.getStringDescription());

        }
        generateRadioButtons(categoryInfo, parent);

    }

    private void generateRadioButtons(List<String> values, ViewGroup parent) {

        RadioButton radioButton = null;
        int index = 0;
        for (String area : values) {
            radioButton = new RadioButton(a);
            radioButton.setId(++index);
            radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


            radioButton.setText(area.replace("_"," "));
            radioButton.setChecked(false);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) a.findViewById(R.id.select_quiz_button);
                    button.setOnClickListener(ocl);
                    if (!Util.isNull(button)) {
                        button.setEnabled(true);

                    }
                }
            });
            parent.addView(radioButton);
        }
    }

    String field = null;
    OnClickListener ocl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(a);
                View promptsView = li.inflate(R.layout.prompt, null);
                final SelectQuiz sq = classObj;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        a);

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

                                        Intent intent = new Intent(a, SelectQuiz.class);
                                        RadioGroup selectQuizGroup = (RadioGroup) a.findViewById(R.id.quiz_radio_group);
                                        System.out.println("selectQuiz :" + selectQuizGroup + "," + selectQuizGroup.getCheckedRadioButtonId());
                                        // find the radio button by returned id
                                        RadioButton selectedQuiz = (RadioButton) a.findViewById(selectQuizGroup.getCheckedRadioButtonId());

                                        field = selectedQuiz.getText().toString().replace(" ","_");
                                        try {

                                            Map<String, String> parameters = new HashMap<>();
                                            parameters.put(getString(R.string.category_name), field);
                                            parameters.put(getString(R.string.num_of_ques), userInput.getText().toString());
                                            connector.getQuestions(sq, a, parameters);
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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

        RadioGroup selectQuizGroup = (RadioGroup) a.findViewById(R.id.quiz_radio_group);
        RadioButton selectedQuiz = (RadioButton) a.findViewById(selectQuizGroup.getCheckedRadioButtonId());
        String selectedCategory = selectedQuiz.getText().toString();
        List<Question> questions = connector.getOrFetchQuestions(selectedCategory, String.valueOf(response));
        if(Util.isNullOrBlank((String)response) ) {
            //Data not found, move to the error page.
            ErrorPage errorPage = new ErrorPage();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      
            fragmentTransaction.replace(R.id.frame_container, errorPage);
            fragmentTransaction.commit();
            return;
        }
        LaunchQuiz lq = new LaunchQuiz();
       
    
        a.setTitle(R.string.launch_quiz);
        Bundle bundle = new Bundle();
        bundle.putString(SELECTED_QUIZ, field);
        lq.setQuestionList(questions);
        lq.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        fragmentTransaction.replace(R.id.frame_container, lq);
        fragmentTransaction.commit();
    }
}
