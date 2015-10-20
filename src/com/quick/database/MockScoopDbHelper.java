package com.quick.database;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.quick.DBTypes;
import com.quick.category.Category;
import com.quick.category.CategoryContract;
import com.quick.mockscoop.RequestReceiver;
import com.quick.questions.Question;
import com.quick.questions.QuestionContract;
import com.quick.result.QuizResultContract;
import com.quick.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eropate on 22/6/15.
 */
public class MockScoopDbHelper extends SQLiteOpenHelper {
    boolean getCategories = false;
    public static final int DATABASE_VERION = 1;
    public static final String DATABASE_NAME = "mockscoop.db";
    private static final String COMMA_SEP = " ,";
    private static final String POST_DATA_URL = "";
    private static final String SERVICE_URL = "http://103.253.175.78:8080/RestWebServiceDemo/rest/person";
    @SuppressLint("NewApi")
int a;
    private static final String TAG = "AndroidRESTClientActivity";
    private static final String SQL_CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + CategoryContract.ContractEntry.TABLE_NAME + " (" +
                    CategoryContract.ContractEntry._ID + " INTEGER PRIMARY KEY," +
                    CategoryContract.ContractEntry.COLUMN_NAME_NAME + DBTypes.TEXT_TYPE + COMMA_SEP +
                    CategoryContract.ContractEntry.COLUMN_NAME_HITS + DBTypes.INTEGER_TYPE + ")";

    private static final String SQL_DELETE_CATEGORY_TABLE = "DROP TABLE IF EXISTS " + CategoryContract.ContractEntry.TABLE_NAME;

    private static final String SQL_CREATE_QUESTIONS_TABLE =
            "CREATE TABLE " + QuestionContract.QuestionEntry.TABLE_NAME + " (" +
                    QuestionContract.QuestionEntry._ID + " INTEGER PRIMARY KEY," +
                    QuestionContract.QuestionEntry.COLUMN_NAME_CATEGORY_ID + DBTypes.INTEGER_TYPE + COMMA_SEP +
                    QuestionContract.QuestionEntry.COLUMN_NAME_QUESTION + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuestionContract.QuestionEntry.COLUMN_NAME_OPT1 + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuestionContract.QuestionEntry.COLUMN_NAME_OPT2 + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuestionContract.QuestionEntry.COLUMN_NAME_OPT3 + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuestionContract.QuestionEntry.COLUMN_NAME_OPT4 + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuestionContract.QuestionEntry.COLUMN_NAME_ANS + DBTypes.INTEGER_TYPE
                    + ")";

    private static final String SQL_DELETE_QUESTIONS_TABLE = "DROP TABLE IF EXISTS " + QuestionContract.QuestionEntry.TABLE_NAME;

    private static final String SQL_CREATE_RESULTS_TABLE =
            "CREATE TABLE " + QuizResultContract.QuizResultEntry.TABLE_NAME + " (" +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_USERNAME + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_QUESTIONID + DBTypes.INTEGER_TYPE + COMMA_SEP +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_QUESTIONTEXT + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_ATTEMPTEDANSWER + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_CORRECTANSWER + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_STARTTIME + DBTypes.TEXT_TYPE + COMMA_SEP +
                    QuizResultContract.QuizResultEntry.COLUMN_NAME_TIMETOANSEWER + DBTypes.TEXT_TYPE +
                    ")";

    private static final String SQL_DELETE_RESULTS_TABLE = "DROP TABLE IF EXISTS " + QuizResultContract.QuizResultEntry.TABLE_NAME;

    public MockScoopDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("Creating Database.....");
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        //import default categories
        createCategories(db);

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        //import default categories
        loadQuestions(db);


        db.execSQL(SQL_CREATE_RESULTS_TABLE);
        System.out.println("DB Creation complete.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //right now on upgrade it would clean old data & create new data, if
        //data to be preserved, code need to be changed
        db.execSQL(SQL_DELETE_QUESTIONS_TABLE);
        db.execSQL(SQL_DELETE_CATEGORY_TABLE);
        db.execSQL(SQL_DELETE_RESULTS_TABLE);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static List<Category> getAllCategoriesFromWeb() {

        //String responseString = getDataFromWeb("ALL_CATEGORIES");
        List<Category> allCategories = new ArrayList<>();
        String responseString = arr;

        //now parse the response into Category Object.
        try {

            JSONArray allJsonCategories = new JSONArray(responseString);
            ;


            for (int i = 0; i < allJsonCategories.length(); i++) {
                int id = i + 1;
                String name = allJsonCategories.getJSONObject(i).getString("name");
                int hits = allJsonCategories.getJSONObject(i).getInt("hits");
                allCategories.add(new Category(id, name, (long) hits));
            }

            System.out.println(allCategories);
        } catch (JSONException jex) {
            jex.printStackTrace();
        }
        return allCategories;
    }

    private void createCategories(SQLiteDatabase db) {


        System.out.println("Creating categories.....");

        ContentValues values = new ContentValues();


        List<Category> lst = new ArrayList<>();
        lst.add(new Category("Current Affairs", 0l));
        lst.add(new Category("General Knowledge", 0l));
        lst.add(new Category("TCS_Antonyms", 116l));
        lst.add(new Category("TCS_Synonyms_Test", 79l));
        lst.add(new Category("cplusplus", 85l));
        lst.add(new Category("C_Test", 322l));
        lst.add(new Category("SoftwareTesting", 50l));
        lst.add(new Category("HCL_MOCK", 75l));
        lst.add(new Category("wipro_mock", 65l));
        lst.add(new Category("DataStructures", 115l));
        lst.add(new Category("DBMS", 53l));
        lst.add(new Category("programmingIQ", 69l));
        lst.add(new Category("infosys", 55l));
        lst.add(new Category("IBM", 30l));
        lst.add(new Category("wipro_science", 12l));
        lst.add(new Category("Tech_mahindra", 40l));
        lst.add(new Category("linux", 26l));
        lst.add(new Category("TCS_Maths", 32l));
        lst.add(new Category("Satyam_Antonyms", 4l));
        lst.add(new Category("Satyam_Aptitude", 14l));
        lst.add(new Category("infosys_aptitude", 42l));
        lst.add(new Category("Aptitude_time_distance_problems", 8l));
        lst.add(new Category("tilak", 0l));
        lst.add(new Category("General_Aptitude", 58l));
        lst.add(new Category("jyoti", 0l));
        lst.add(new Category("cts", 0l));
        lst.add(new Category("ds", 0l));
        lst.add(new Category("FLEX", 0l));
        ;
        for (Category category : lst) {
            values = new ContentValues();
            values.put(CategoryContract.ContractEntry.COLUMN_NAME_NAME, category.getCategory());
            values.put(CategoryContract.ContractEntry.COLUMN_NAME_HITS, category.getHits());

            db.insert(CategoryContract.ContractEntry.TABLE_NAME, null, values);

        }


    }

    public void saveDataToWeb(String requestData) {

        HttpClient httpClient = new DefaultHttpClient(); //Deprecated

        try {
            HttpPost request = new HttpPost(POST_DATA_URL);
            StringEntity params = new StringEntity(requestData);
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            // handle response here...
        } catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }

    public void getDataFromWeb(String resource) {

        //from resource generate web request and get the data.
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet("http://jsonplaceholder.typicode.com/posts/1"));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                System.out.println("---responseString : " + responseString);
                out.close();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException iex) {
            iex.printStackTrace();
        }

    }

    public void addQuestionToCategory(SQLiteDatabase db, String categoryName, List<Question> lstQuestions) {

        if (Util.isNullOrEmpty(lstQuestions))
            return;

        ContentValues values = new ContentValues();
        for (Question question : lstQuestions) {
            values = new ContentValues();
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_CATEGORY_ID, getCategoryId(db, categoryName));
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_QUESTION, question.getQuestion());
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_OPT1, question.getOption1());
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_OPT2, question.getOption2());
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_OPT3, question.getOption3());
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_OPT4, question.getOption4());
            values.put(QuestionContract.QuestionEntry.COLUMN_NAME_ANS, question.getAnswer());

            db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, values);

        }

    }

    //temp method for importing some questions for testing
    private void loadQuestions(SQLiteDatabase db) {


        List<Question> currentAffairsQuestions = new ArrayList<>();
        currentAffairsQuestions.add(new Question(0, "Who has been appointed as President of International Cricket Council (ICC)? ",
                "Zaheer Abbas", "Mustafa Kamal", "Austin Garden", "John Shreff", 1));

        currentAffairsQuestions.add(new Question(0, "Hollywood composer, James Horner, who died recently, had won two academic awards for his work in which movie? ",
                "The Karate Kid ", "Aliens", "Titanic", "Avatar", 3));


        addQuestionToCategory(db, "Current Affairs", currentAffairsQuestions);

    }

    public int getCategoryId(SQLiteDatabase db, String category) {


        String[] projection = {
                CategoryContract.ContractEntry._ID
        };


        Cursor c = db.query(CategoryContract.ContractEntry.TABLE_NAME, projection, CategoryContract.ContractEntry.COLUMN_NAME_NAME + "=?", new String[]{category}, null, null, null);
        c.moveToNext();
        int id = c.getInt(c.getColumnIndex(CategoryContract.ContractEntry._ID));


        return id;

    }

    static RequestReceiver Obj = null;

    public static void postData(RequestReceiver obj) throws IOException {

        Obj = obj;


        WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, "Posting data...");
 
       /* wst.addNameValuePair("date", date);
        wst.addNameValuePair("time", time);
        wst.addNameValuePair("date1", date1);
        wst.addNameValuePair("time1", time1);
        wst.addNameValuePair("type", type);
        wst.addNameValuePair("charge", charge1);
        wst.addNameValuePair("time1", time1);
        wst.addNameValuePair("desc", description);
        wst.addNameValuePair("image", ba1);
        wst.addNameValuePair("RequestType", "MyCarPool");
        wst.addNameValuePair("email", "test@yahoo.com");*/
        wst.addNameValuePair("RequestType", "ALL_CATEGORIES");


        // the passed String is the URL we will POST to
        wst.execute(new String[]{SERVICE_URL});

    }

    static String arr = null;

    public static void handleResponse(String response) {


        try {

            arr = response;
            Message m = new Message();
            Obj.receiveResponse(m);


        } catch (Exception e) {
            Log.e("aman", e.getLocalizedMessage(), e);
        }

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
        private Context mContext = null;
        private String processMessage = "Processing...";

        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        private ProgressDialog pDlg = null;

        public WebServiceTask(int taskType, String processMessage) {

            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;
        }

        public void addNameValuePair(String name, String value) {

            params.add(new BasicNameValuePair(name, value));
        }

        private void showProgressDialog() {


        }

        @Override
        protected void onPreExecute() {

            // showProgressDialog();

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

            handleResponse(response);
            //pDlg.dismiss();

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

