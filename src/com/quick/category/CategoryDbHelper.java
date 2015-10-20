package com.quick.category;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import com.quick.DBTypes;
import com.quick.database.MockScoopDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.quick.category.CategoryContract.ContractEntry;

/**
 * Created by eropate on 20/6/15.
 */

public class CategoryDbHelper extends MockScoopDbHelper {


    private static CategoryDbHelper instance = null;

    public static CategoryDbHelper getDBHelper(Context context) {
        if (instance == null) {
            instance = new CategoryDbHelper(context);
        }
        return instance;
    }

    private CategoryDbHelper(Context context) {
        super(context);

    }


    public long createCategory(Category category) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContractEntry.COLUMN_NAME_NAME, category.getCategory());
        values.put(ContractEntry.COLUMN_NAME_HITS, "0");
        long newRowId = db.insert(ContractEntry.TABLE_NAME, null, values);

        return newRowId;
    }

 
    public List<Category> getAllCategories() {


        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ContractEntry.COLUMN_NAME_NAME, ContractEntry.COLUMN_NAME_HITS
        };
        //sorting the result
        String sortOrder = ContractEntry.COLUMN_NAME_HITS + " DESC";


        Cursor c = db.query(ContractEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);


        List<Category> allCategories = new ArrayList<>();
        while (c.moveToNext()) {
            allCategories.add(new Category(c.getString(c.getColumnIndex(ContractEntry.COLUMN_NAME_NAME)),
                    c.getLong(c.getColumnIndex(ContractEntry.COLUMN_NAME_HITS))));

        }
        return allCategories;

    }

    public void deleteCategory(String categoryName) {

        SQLiteDatabase db = getWritableDatabase();
        String selection = ContractEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selecionArgs = {categoryName};
        db.delete(ContractEntry.TABLE_NAME, selection, selecionArgs);
    }

    public int updateCategory(Category category, String newName) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContractEntry.COLUMN_NAME_NAME, newName);

        String selection = ContractEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = {category.getCategory()};
        int count = db.update(ContractEntry.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }

}
