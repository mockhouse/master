package com.quick.category;

import android.provider.BaseColumns;

import com.quick.base.MockScoopBaseContract;

/**
 * Created by eropate on 20/6/15.
 */
public class CategoryContract extends MockScoopBaseContract {
    public CategoryContract(){}

    public static abstract class ContractEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_HITS = "hits";
    }
}
