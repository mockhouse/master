package com.aasquare.mockhouse.base;

import android.app.Fragment;

import com.aasquare.mockhouse.connector.WebConnector;

/**
 * Created by eropate on 8/16/2015.
 */
public class MockScoopBaseActivity1   extends Fragment {

    protected WebConnector connector = WebConnector.getInstance();
    public WebConnector getConnector() {
        return connector;
    }

}
