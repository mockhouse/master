package com.quick.mockscoop;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.quick.checkyourknowledge.ShowRank;
import com.quick.checkyourknowledge.ShowUserScores;
import com.quick.global.Globals;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private LinearLayout drawerll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        drawerll = (LinearLayout) findViewById(R.id.drawerll);
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {

                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        } else {
            Globals.imageURI = savedInstanceState.getString("Image_URI");
            Globals.fb_id = savedInstanceState.getString("fb_id");
            Globals.userName = savedInstanceState.getString("username");
            Globals.realName = savedInstanceState.getString("realname");
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            return;
        }
        //setting image of profile pic
        ImageView imageView = (ImageView) findViewById(R.id.image_profilepic);
        TextView textViewName = (TextView) findViewById(R.id.txtUserName);
        textViewName.setText(Globals.userName);
        textViewName.setTextColor(Color.WHITE);
        new DownloadImageTask(imageView)
                .execute(Globals.imageURI);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {

            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                URL url = new URL(urldisplay);
                HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                ucon.setInstanceFollowRedirects(false);
                URL secondURL = new URL(ucon.getHeaderField("Location"));
                InputStream in = new java.net.URL(secondURL.toString()).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                mIcon11 = getRoundedCornerBitmap(mIcon11);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 128;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }

        protected void onPostExecute(Bitmap result) {

            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("Image_URI", Globals.imageURI);
        savedInstanceState.putString("fb_id", Globals.fb_id);
        savedInstanceState.putString("username", Globals.userName);
        savedInstanceState.putString("realname", Globals.realName);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerll);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0://home, go to home page
                //fragment = new SelectQuiz();
                fragment = new SelectCategory();
                Log.e("aman", "home");
                break;
            case 1://go to Recent Scores page
                fragment = new ShowUserScores();
                break;
            case 2://go to leaders page
                fragment = new ShowToppersPage();
                break;
            case 3://go to rank page
                fragment = new ShowRank();
                break;
            case 4://go to help page
                fragment = new Help();
                break;
            case 5:
                finish();
                return;
            default:
                break;
        }
        String tag = "LaunchQuiz";
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            LaunchQuiz lq = (LaunchQuiz) (fragmentManager.findFragmentByTag(tag));
            if (!(lq != null && lq.isVisible()))
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(drawerll);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {

        System.out.print("Back button pressed!!!");
        String tag = "LaunchQuiz";
        FragmentManager fragmentManager = getFragmentManager();
        LaunchQuiz lq = (LaunchQuiz) (fragmentManager.findFragmentByTag(tag));
        Fragment frg = fragmentManager.findFragmentById(R.id.frame_container);
        if (lq != null && lq.isVisible()) {
        } else if (fragmentManager.getBackStackEntryCount() > 1) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
            fragmentTransaction.remove(frg);
            fragmentTransaction.commit();
            fragmentManager.popBackStack();
        } else
            finish();
//        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
