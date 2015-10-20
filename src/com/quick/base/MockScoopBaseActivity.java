package com.quick.base;

import android.app.Activity;
import android.app.Fragment;

import com.quick.connector.WebConnector;

/**
 * Created by eropate on 8/16/2015.
 */
public class MockScoopBaseActivity   extends Fragment {
    protected static final MockScoopCache cache = MockScoopCache.getInstance();
    protected WebConnector connector = WebConnector.getInstance();
    public WebConnector getConnector() {
        return connector;
    }

}
