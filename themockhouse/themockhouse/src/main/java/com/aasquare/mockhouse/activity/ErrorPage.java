package com.aasquare.mockhouse.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aasquare.mockhouse.base.MockScoopBaseActivity;

public class ErrorPage extends MockScoopBaseActivity {
	final ValueAnimator animator = new ValueAnimator();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_error_page, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Button btnTryAgain = (Button) getActivity().findViewById(R.id.buttonTryAgain);
        btnTryAgain.setOnClickListener(new CheckAgainAction());
        btnTryAgain.setEnabled(false);
        enableButtonAfterPeriod(btnTryAgain,getResources().getInteger(R.integer.connection_cooloff_period));

    }

    private void enableButtonAfterPeriod(final Button button, final int duration) {
        long animationDuration = duration * 1000;
        
        animator.setObjectValues(0, duration);
        animator.setDuration(animationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                int currentValue = duration - (int) animation.getAnimatedValue();
                button.setText(getString(R.string.connecting_in) + " " + String.valueOf(currentValue));

                if(currentValue == 0){
                    button.setEnabled(true);
                    button.performClick();
                }
            }

        });
        animator.start();
    }

    private class CheckAgainAction implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent myIntent = new Intent(getActivity(), HomePage.class);
            getActivity().startActivity(myIntent);
        }
    }
    @Override
	public void onPause(){
        super.onPause();
        animator.pause();
     
    }
    @Override
    public void onResume(){
        super.onResume();
        animator.resume();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify currentActivity parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
