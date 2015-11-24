package com.quick.mockscoop;

import java.io.IOException;

import com.quick.connector.WebConnector;
import com.quick.global.Globals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class HomePage extends Activity implements RequestReceiver {
	WebConnector.WebServiceTask wst=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        setContentView(R.layout.activity_homepage);
        try {
        	wst=connector.getOrFetchCategories(this, this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    @Override
    public void onPause() {
        super.onPause();  // 
        if( wst.pDlg!=null)
        {
        	Log.e("amanjot","dismiss dialog");
       wst.pDlg.dismiss();
       wst.pDlg=null;
        }
    
       
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
