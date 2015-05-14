package com.magicrecipe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.magicrecipe.model.Response;

import java.util.ArrayList;

/**
 * Created by Vineeth on 5/12/2015.
 */
public class RecipeDB extends SQLiteOpenHelper{

    private final String TAG = getClass().getSimpleName();

    private static RecipeDB recipeDB;

    private static SQLiteDatabase db;

    private static Context myContext;

    private static final String DATABASE_NAME = "RecipeDB";

    private static final int DATABASE_VERSION = 1;

    // table name..
    private static final String TABLE_RECIPE_HISTORY   = "RECIPE_HISTORY_TABLE";
    private static final String TABLE_RECIPE_FAVORITES = "RECIPE_FAVORITES_TABLE";

    // table column names..
    public static final String KEY_ID = "_id";

    public static final String KEY_TITLE = "title";

    public static final String KEY_URL = "url";

    public static final String KEY_THUMBURL = "thumbUrl";

    public static final String KEY_HISTORY = "history";

    public static final String KEY_FAVORITES = "favorites";

    // Database tables..
    private static final String CREATE_RECIPE_HISTORY_TABLE = "CREATE TABLE "
            + TABLE_RECIPE_HISTORY + " (" + KEY_ID
            + " integer primary key autoincrement, " + KEY_TITLE + " text not null, "
            + KEY_URL + " text not null, " + KEY_THUMBURL + " text not null);";

    private static final String CREATE_RECIPE_FAVORITE_TABLE = "CREATE TABLE "
            + TABLE_RECIPE_FAVORITES + " (" + KEY_ID
            + " integer primary key autoincrement, " + KEY_TITLE + " text not null, "
            + KEY_URL + " text not null, " + KEY_THUMBURL + " text not null);";

    private RecipeDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_RECIPE_HISTORY_TABLE);
            db.execSQL(CREATE_RECIPE_FAVORITE_TABLE);

        } catch (Exception e) {
            Log.d(TAG, "Exception " + e);

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion	+ ", which will destroy all old data");

        try {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECIPE_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECIPE_FAVORITES);

        } catch (Exception e) {
            Log.v(TAG, "database upgrade =" + e.getMessage().toString());

        }
        onCreate(db);
    }

    public static final RecipeDB getInstance(Context context) {
        myContext = context.getApplicationContext();

        if (recipeDB == null) {

            recipeDB = new RecipeDB(context, DATABASE_NAME, null, DATABASE_VERSION);

            db = recipeDB.getWritableDatabase();
        }

        return recipeDB;
    }

    public void close() {
        if (recipeDB != null) {
            db.close();
            recipeDB = null;
        }
    }

    public void saveHistory(Response response){
            db.delete(TABLE_RECIPE_HISTORY, KEY_TITLE + "=?", new String[]{response.getTitle()});
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, response.getTitle());
            values.put(KEY_URL, response.getUrl());
            values.put(KEY_THUMBURL, response.getThumburl());
            db.insert(TABLE_RECIPE_HISTORY, null, values);
    }

    public long saveFavorites(Response response){
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, response.getTitle());
        values.put(KEY_URL, response.getUrl());
        values.put(KEY_THUMBURL, response.getThumburl());
        return db.insert(TABLE_RECIPE_FAVORITES, null, values);
    }

    private boolean isDuplicate(String title){
        Cursor cursor = db.query(TABLE_RECIPE_HISTORY, new String[]{KEY_TITLE}, KEY_TITLE +"=?", new String[]{title}, null, null, null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void deleteFavorite(String title){
        db.delete(TABLE_RECIPE_FAVORITES, KEY_TITLE + "=?", new String[]{title});
    }


    public ArrayList<Response> getFavorites(){
        ArrayList<Response> responses = new ArrayList<Response>();
        Cursor cursor = db.query(TABLE_RECIPE_FAVORITES, null, null, null, null, null, null);
        while(cursor.moveToNext()){
            Response response = new Response();
            response.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            response.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            response.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
            response.setThumburl(cursor.getString(cursor.getColumnIndex(KEY_THUMBURL)));
            responses.add(response);
        }
        return  responses;
    }

    public ArrayList<String> getFavoritesTitle(){
        ArrayList<String> favoriteTitles = new ArrayList<String>();
        Cursor cursor = db.query(TABLE_RECIPE_FAVORITES, new String[]{KEY_TITLE}, null, null, null, null, null);
        while(cursor.moveToNext()){
            favoriteTitles.add(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        }
        return  favoriteTitles;
    }

    public ArrayList<Response> getHistory(){
        ArrayList<Response> responses = new ArrayList<Response>();
        Cursor cursor = db.query(TABLE_RECIPE_HISTORY, null, null, null, null, null, KEY_ID+" DESC");
        while(cursor.moveToNext()){
            Response response = new Response();
            response.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            response.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            response.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
            response.setThumburl(cursor.getString(cursor.getColumnIndex(KEY_THUMBURL)));
            responses.add(response);
        }
        return  responses;
    }

    public void clearHistory() {
        db.delete(TABLE_RECIPE_HISTORY, null, null);
    }

    public void clearFavorites() {
        db.delete(TABLE_RECIPE_FAVORITES, null, null);
    }

}
