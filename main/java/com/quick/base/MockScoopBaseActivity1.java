package com.quick.base;

import android.app.Activity;
import android.app.Fragment;

import com.quick.connector.WebConnector;

/**
 * Created by eropate on 8/16/2015.
 */
public class MockScoopBaseActivity1   extends Fragment {

    protected WebConnector connector = WebConnector.getInstance();
    public WebConnector getConnector() {
        return connector;
    }

}
