package com.quick.connector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.quick.base.MockScoopBaseActivity;
import com.quick.base.MockScoopCache;
import com.quick.category.Category;
import com.quick.mockscoop.R;
import com.quick.mockscoop.RequestReceiver;
import com.quick.questions.Question;
import com.quick.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebConnector implements BaseConnector {

    private static final String SERVICE_URL = "http://103.253.174.51:8080/mockScoopServer/rest/person";
    static String latestResponse = null;
    private static final MockScoopCache cache = MockScoopCache.getInstance();

    private static class ConnectorHolder {
        public static final WebConnector instance = new WebConnector();
    }

    public static WebConnector getInstance() {
        return ConnectorHolder.instance;
    }

    private WebConnector() {
    }

    public List<Category> getOrFetchCategories(String jsonResponse) {

        if (cache.containsKey(MockScoopCache.CacheKeys.ALL_CATEGORIES)) {
            return new ArrayList<>((List<Category>) cache.get(MockScoopCache.CacheKeys.ALL_CATEGORIES));
        }

        if(Util.isNullOrBlank(jsonResponse) || Util.isNullString(jsonResponse))
            return new ArrayList<>();

        List<Category> allCategories = new ArrayList<>();
        try {

            JSONArray allJsonCategories = new JSONArray(jsonResponse);
            for (int i = 0; i < allJsonCategories.length(); i++) {
                int id = i + 1;
                String name = allJsonCategories.getJSONObject(i).getString("name");
                int hits = allJsonCategories.getJSONObject(i).getInt("hits");
                String categoryType = allJsonCategories.getJSONObject(i).getString("categoryType");
                allCategories.add(new Category(id, name, (long) hits,categoryType));
            }

            System.out.println(allCategories);
        } catch (JSONException jex) {
            jex.printStackTrace();
        }
        if (allCategories != null && !allCategories.isEmpty()) {
            cache.put(MockScoopCache.CacheKeys.ALL_CATEGORIES, allCategories);
        }
        return allCategories;
    }

    public List<Question> getOrFetchQuestions(String categoryName, String jsonResponse) {

        //validate parameters
        if(Util.isNullOrBlank(categoryName) || Util.isNullString(categoryName))
            return new ArrayList<>();

        //validate cache entry exists
        if (cache.containsKey(categoryName)) {
            return new ArrayList<>((List<Question>) cache.get(categoryName));
        }

        if(Util.isNullOrBlank(jsonResponse) || Util.isNullString(jsonResponse))
            return new ArrayList<>();

        List<Question> allQuestions = new ArrayList<>();
        //now parse the response into Category Object.
        try {

            JSONArray allJsonCategories = new JSONArray(jsonResponse);

            for (int i = 0; i < allJsonCategories.length(); i++) {
                int id = allJsonCategories.getJSONObject(i).getInt("number");
                String questionText = allJsonCategories.getJSONObject(i).getString("ques");
                String option1 = allJsonCategories.getJSONObject(i).getString("opt1");
                String option2 = allJsonCategories.getJSONObject(i).getString("opt2");
                String option3 = allJsonCategories.getJSONObject(i).getString("opt3");
                String option4 = allJsonCategories.getJSONObject(i).getString("opt4");
                int ans = allJsonCategories.getJSONObject(i).getInt("ans");

                allQuestions.add(new Question(id, questionText, option1, option2, option3, option4, ans));
            }

            if (allQuestions != null && !allQuestions.isEmpty()) {
                cache.put(categoryName, allQuestions);
            }

        } catch (JSONException jex) {
            jex.printStackTrace();
        }
        return allQuestions;
    }

    public void saveUserScore(RequestReceiver receiver, Activity activity, Map<String, String> valuePair) throws IOException {

        WebServiceTask wst = new WebServiceTask(receiver, WebServiceTask.POST_TASK, "Saving Scores", activity);

        wst.addNameValuePair("RequestType", "SAVE_SCORES");
        wst.addNameValuePair(valuePair);
        wst.execute(new String[]{SERVICE_URL});
    }


    public void getUserScore(RequestReceiver receiver, Activity activity, Map<String, String> valuePair) throws IOException {

        WebServiceTask wst = new WebServiceTask(receiver, WebServiceTask.POST_TASK, "Getting Scores", activity);

        wst.addNameValuePair("RequestType", "RECENT_RESULTS");
        wst.addNameValuePair(valuePair);
        wst.execute(new String[]{SERVICE_URL});
    }

    public void getTopperList(RequestReceiver receiver, Activity activity, Map<String, String> valuePair) throws IOException {

        WebServiceTask wst = new WebServiceTask(receiver, WebServiceTask.POST_TASK, "Getting Top Scorers", activity);

        wst.addNameValuePair("RequestType", "TOPPERS");
        wst.addNameValuePair(valuePair);
        wst.execute(new String[]{SERVICE_URL});

    }

    public void getUserRank(RequestReceiver receiver, Activity activity, Map<String, String> valuePair) throws IOException {

        WebServiceTask wst = new WebServiceTask(receiver, WebServiceTask.POST_TASK, "Getting Rank", activity);

        wst.addNameValuePair("RequestType", "RANK");
        wst.addNameValuePair(valuePair);
        wst.execute(new String[]{SERVICE_URL});

    }

    public void getOrFetchCategories(RequestReceiver receiver, Activity activity, Map<String, String> valuePair) throws IOException {

        WebServiceTask wst = new WebServiceTask(receiver, WebServiceTask.POST_TASK, "Getting Categories", activity);
        wst.addNameValuePair("RequestType", "ALL_CATEGORIES");
        wst.addNameValuePair(valuePair);
        wst.execute(new String[]{SERVICE_URL});

    }

    public void getQuestions(RequestReceiver receiver, Activity activity, Map<String, String> valuePair) throws IOException {

        WebServiceTask wst = new WebServiceTask(receiver, WebServiceTask.POST_TASK, "Load Questions", activity);
        wst.addNameValuePair("RequestType", "QUESTIONS");
        wst.addNameValuePair(valuePair);
        wst.execute(new String[]{SERVICE_URL});
    }

    public void receiveResponse(RequestReceiver obj, String response) {
        latestResponse = response;
        obj.receiveResponse(response);
    }

    @SuppressLint("NewApi")
    private static class WebServiceTask extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;

        private static final String TAG = "WebServiceTask";

        // connection timeout, in milliseconds (waiting to connect)
        @SuppressLint("NewApi")
        private static final int CONN_TIMEOUT = 3000;

        // socket timeout, in milliseconds (waiting for data)
        private static final int SOCKET_TIMEOUT = 5000;

        private int taskType = GET_TASK;
        private Activity mContext = null;
        private String processMessage = "Processing...";
        private RequestReceiver requestReceiver;
        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        private ProgressDialog pDlg = null;

        public WebServiceTask(RequestReceiver receiver, int taskType, String processMessage, Activity c) {

            this.requestReceiver = receiver;
            this.taskType = taskType;
            mContext = c;
            this.processMessage = processMessage;
        }

        public void addNameValuePair(String name, String value) {

            params.add(new BasicNameValuePair(name, value));
        }

        public void addNameValuePair(Map<String, String> valuePair) {

            if (valuePair != null && !valuePair.isEmpty()) {
                for (Map.Entry<String, String> entry : valuePair.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }

            }

        }

        private void showProgressDialog() {

            pDlg = new ProgressDialog(mContext);
            pDlg.setMessage(processMessage);
            pDlg.setProgressDrawable(mContext.getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();

        }

        @Override
        protected void onPreExecute() {

            showProgressDialog();

        }

        protected String doInBackground(String... urls) {

            String url = urls[0];
            String result = "";

            HttpResponse response = doResponse(url);
            System.out.println("Aman");
            if (response == null) {
                return result;
            } else {

                try {

                    result = inputStreamToString(response.getEntity().getContent());

                } catch (IllegalStateException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);

                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(String response) {

            ConnectorHolder.instance.receiveResponse(requestReceiver, response);
            pDlg.dismiss();

        }

        // Establish connection and socket (data retrieval) timeouts
        private HttpParams getHttpParams() {

            HttpParams htpp = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

            return htpp;
        }

        private HttpResponse doResponse(String url) {

            // Use our connection and data timeouts as parameters for our
            // DefaultHttpClient
            HttpClient httpclient = new DefaultHttpClient(getHttpParams());

            HttpResponse response = null;

            try {
                switch (taskType) {

                    case POST_TASK:
                        HttpPost httppost = new HttpPost(url);
                        // Add parameters
                        httppost.setEntity(new UrlEncodedFormEntity(params));
                        response = httpclient.execute(httppost);
                        break;
                    case GET_TASK:
                        HttpGet httpget = new HttpGet(url);
                        response = httpclient.execute(httpget);
                        break;
                }
            } catch (Exception e) {

                Log.e(TAG, e.getLocalizedMessage(), e);

            }

            return response;
        }

        private String inputStreamToString(InputStream is) {

            String line = "";
            StringBuilder total = new StringBuilder();

            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                // Read response until the end
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            // Return full string
            return total.toString();
        }

    }

}
