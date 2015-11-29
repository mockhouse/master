package com.aasquare.mockhouse.util;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by eropate on 11/28/2015.
 */
public abstract class GuiUtil extends  Util {
    private GuiUtil(){}

    public static Button createOptionButton(int id, String text, int backgroundResource, View.OnClickListener listener,Activity currentActivity) {

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
}
