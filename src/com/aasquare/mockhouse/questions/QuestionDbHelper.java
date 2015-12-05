package com.aasquare.mockhouse.questions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aasquare.mockhouse.category.CategoryDbHelper;
import com.aasquare.mockhouse.database.MockScoopDbHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.aasquare.mockhouse.questions.QuestionContract.QuestionEntry;

/**
 * Created by eropate on 20/6/15.
 */
public class QuestionDbHelper extends MockScoopDbHelper {


    private static final String COMMA_SEP = " ,";

    private static QuestionDbHelper instance = null;

    private static CategoryDbHelper categoryDbHelper = null;

    public static QuestionDbHelper getDBHelper(Context context) {
        if (instance == null) {
            instance = new QuestionDbHelper(context);
        }
        return instance;
    }

    private QuestionDbHelper(Context context) {

        super(context);
        this.categoryDbHelper = CategoryDbHelper.getDBHelper(context);
    }
static String arr=null;

    public List<Question> getQuestionForCategoryFromWeb(String categoryName) {

        List<Question> allQuestions = new ArrayList<>();

        //String responseString = getDataFromWeb("ALL_QUESTIONS_FOR_CATEGORY");

        String responseString = "{" +
                "  \"question\": [" +
                "    {" +
                "    \"id\": \"1\"," +
                "    \"questionText\": \"Who has been appointed as President of International Cricket Council (ICC)?\"," +
                "    \"option1\": \"Zaheer Abbas\"," +
                "    \"option2\": \"Mustafa Kamal\"," +
                "    \"option3\": \"Austin Garden\"," +
                "    \"option4\": \"John Shreff\"," +
                "    \"ans\": \"1\"" +
                "    }," +
                "     {" +
                "    \"id\": \"2\"," +
                "    \"questionText\": \"What's your name?\"," +
                "    \"option1\": \"AB\"," +
                "    \"option2\": \"AK\"," +
                "    \"option3\": \"BH\"," +
                "    \"option4\": \"NK\"," +
                "    \"ans\": \"3\"" +
                "    }" +
                "  ]" +
                "}";

        //now parse the response into Category Object.
        try {
        
            JSONArray allJsonCategories = new JSONArray(arr);


            for (int i = 0; i < allJsonCategories.length(); i++) {
                int id = allJsonCategories.getJSONObject(i).getInt("id");
                String questionText = allJsonCategories.getJSONObject(i).getString("questionText");
                String option1 = allJsonCategories.getJSONObject(i).getString("option1");
                String option2 = allJsonCategories.getJSONObject(i).getString("option2");
                String option3 = allJsonCategories.getJSONObject(i).getString("option3");
                String option4 = allJsonCategories.getJSONObject(i).getString("option4");
                int ans =  allJsonCategories.getJSONObject(i).getInt("ans");

                allQuestions.add(new Question(id,questionText,option1,option2,option3,option4,ans));
            }

            System.out.println(allQuestions);
        }catch (JSONException jex) {
            jex.printStackTrace();
        }

        return allQuestions;
    }


    public List<Question> getQuestionForCategory(String categoryName) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                QuestionEntry._ID,
                QuestionEntry.COLUMN_NAME_QUESTION, QuestionEntry.COLUMN_NAME_OPT1,
                QuestionEntry.COLUMN_NAME_OPT2, QuestionEntry.COLUMN_NAME_OPT3, QuestionEntry.COLUMN_NAME_OPT4, QuestionEntry.COLUMN_NAME_ANS
        };


        Cursor c = db.query(QuestionContract.QuestionEntry.TABLE_NAME, projection, QuestionEntry.COLUMN_NAME_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryDbHelper.getCategoryId(db,categoryName))}, null, null, null);


        List<Question> allQuestions = new ArrayList<>();
        while (c.moveToNext()) {
            allQuestions.add(new Question(
                    c.getInt(c.getColumnIndex(QuestionEntry._ID)),
                    c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_QUESTION)),
                    c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_OPT1)),
                    c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_OPT2)),
                    c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_OPT3)),
                    c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_OPT4)),
                    c.getInt(c.getColumnIndex(QuestionEntry.COLUMN_NAME_ANS))
            ));

        }
        return allQuestions;
    }





    public long createQuestion(Question question, String categoryName) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_NAME_CATEGORY_ID, categoryDbHelper.getCategoryId(db,categoryName));
        values.put(QuestionEntry.COLUMN_NAME_QUESTION, question.getQuestion());
        values.put(QuestionEntry.COLUMN_NAME_OPT1, question.getOption1());
        values.put(QuestionEntry.COLUMN_NAME_OPT2, question.getOption2());
        values.put(QuestionEntry.COLUMN_NAME_OPT3, question.getOption3());
        values.put(QuestionEntry.COLUMN_NAME_OPT4, question.getOption4());
        values.put(QuestionEntry.COLUMN_NAME_ANS, question.getAnswer());

        long newRowId = db.insert(QuestionEntry.TABLE_NAME, null, values);

        return newRowId;
    }


}
