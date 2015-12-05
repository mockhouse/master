package com.aasquare.mockhouse.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aasquare.mockhouse.questions.Question;

/**
 * Created by eropate on 11/28/2015.
 */
public abstract class GuiUtil extends Util {

    private GuiUtil() {

    }

    public static Button createAnswerOptionButton(int id, String text, int backgroundResource, View.OnClickListener listener, Activity currentActivity) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 10, 20);
        Button optionButton = new Button(currentActivity);
        optionButton.setId(id);
        optionButton.setText(text);
        optionButton.setBackgroundResource(backgroundResource);
        optionButton.setOnClickListener(listener);
        optionButton.setLayoutParams(params);
        optionButton.setMinWidth(250);
        return optionButton;
    }

    public static void createAnswerOptionButtons(Question question, int backgroundResource, View.OnClickListener listener, Activity currentActivity, ViewGroup optionList,String invalidOptions) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 10, 20);
        int index = 0;
        String[] options = new String[]{question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4()};
        for (String option : options) {

            if(Util.isValidOption(option,invalidOptions)) {
                Button optionButton = new Button(currentActivity);
                optionButton.setId(++index);
                optionButton.setText(option);
                optionButton.setBackgroundResource(backgroundResource);
                optionButton.setOnClickListener(listener);
                optionButton.setLayoutParams(params);
                optionButton.setMinWidth(250);
                optionList.addView(optionButton);
            }


        }
    }
}
