package com.aasquare.mockhouse.result;

import android.provider.BaseColumns;

import com.aasquare.mockhouse.base.MockScoopBaseContract;

/**
 * Created by eropate on 26/6/15.
 */
public class QuizResultContract extends MockScoopBaseContract {

    public QuizResultContract(){}

    public static abstract class QuizResultEntry implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_QUESTIONID = "quesitonId";
        public static final String COLUMN_NAME_QUESTIONTEXT = "questionText";
        public static final String COLUMN_NAME_ATTEMPTEDANSWER = "attemptedanswer";
        public static final String COLUMN_NAME_CORRECTANSWER = "correctanswer";
        public static final String COLUMN_NAME_TIMETOANSEWER = "timetoanswer";
        public static final String COLUMN_NAME_STARTTIME = "starttime";

    }
}
