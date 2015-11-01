package com.quick.mockscoop;

import java.io.IOException;

import com.quick.connector.WebConnector;
import com.quick.global.Globals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HomePage extends Activity implements RequestReceiver {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            connector.getOrFetchCategories(this, this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_homepage);
    }

    protected WebConnector connector = WebConnector.getInstance();

    public WebConnector getConnector() {
        return connector;
    }

    @Override
    public void receiveResponse(Object response) {
   
        connector.getOrFetchCategories(String.valueOf(response));
        
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
