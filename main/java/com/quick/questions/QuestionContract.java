package com.quick.questions;

import android.provider.BaseColumns;

import com.quick.base.MockScoopBaseContract;

/**
 * Created by eropate on 21/6/15.
 */
public class QuestionContract extends MockScoopBaseContract {
    public QuestionContract(){}

    public static abstract class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "Questions";
        public static final String COLUMN_NAME_CATEGORY_ID = "categoryId";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_OPT1 = "opt1";
        public static final String COLUMN_NAME_OPT2 = "opt2";
        public static final String COLUMN_NAME_OPT3 = "opt3";
        public static final String COLUMN_NAME_OPT4 = "opt4";
        public static final String COLUMN_NAME_ANS = "answer";
    }
}
