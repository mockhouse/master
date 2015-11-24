package com.quick.mockscoop;

import java.io.IOException;








import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quick.connector.WebConnector;

public class Help extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
   
        View rootView = inflater.inflate(R.layout.help_layout, container, false);
        return rootView;
    }
}
