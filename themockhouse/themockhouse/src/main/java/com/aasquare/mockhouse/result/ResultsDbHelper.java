package com.aasquare.mockhouse.result;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.aasquare.mockhouse.database.MockScoopDbHelper;
import com.aasquare.mockhouse.questions.Question;

/**
 * Created by eropate on 26/6/15.
 */
public class ResultsDbHelper extends MockScoopDbHelper {


    private static ResultsDbHelper db = null;

    public static ResultsDbHelper getDbHelper(Context context) {
        if(db == null) {
            db = new ResultsDbHelper(context);
        }
        return db;
    }
    private ResultsDbHelper(Context context) {
        super(context);
    }

    public Long insertResult(String username,Question[] questionList,int[] attemptedAnswers,long[] startTime,long[] duration) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        int index = 0;
        for(Question question: questionList) {

            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_USERNAME, username);
            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_QUESTIONID,question.getId());
            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_QUESTIONTEXT,question.getQuestion());
            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_ATTEMPTEDANSWER,question.getOptionFromIndex(attemptedAnswers[index]));
            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_CORRECTANSWER,question.getOptionFromIndex(question.getAnswer()));
            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_STARTTIME,startTime[index]);
            values.put(QuizResultContract.QuizResultEntry.COLUMN_NAME_TIMETOANSEWER,duration[index]);

            index++;
        }
        long newRowId = db.insert(QuizResultContract.QuizResultEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    public void saveDataToWeb(String username,Question[] questionList,int[] attemptedAnswers,long[] startTime,long[] duration) {


        StringBuilder response = new StringBuilder("{" +
                "  \"result\": {" +
                "    \"userid\":\"1jduckkdhekfnpwhrlen\"," +
                "    \"user\": \""+username+"\"," +
                "    \"category\": \"General Knowledge\"," +
                "    " +
                "      \"question\": [");

        int index = 0;
        for(Question question : questionList) {
            response.append("{");
            response.append("\"id\": \" " + question.getId() + "\",\"");
            response.append("\"ans\": \" " + attemptedAnswers[index] + "\",\"");
            response.append("\"timetaken\": \" " + duration[index] + "\",\"");

            response.append("}");
            index++;
        }
        response.append("]}}");
       // saveDataToWeb(response.toString());

    }

}
