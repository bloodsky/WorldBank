package it.progetto.bra.worldbank.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class DbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WORLD_BANK_OPEN_DB";

    private final String CREATE_SEARCH_TABLE = "create table "+ Contract.SearchEntry.TABLE_NAME+
            "(id integer primary key autoincrement,"+Contract.SearchEntry.COUNTRY+" text,"+
            Contract.SearchEntry.TOPIC+" text,"+Contract.SearchEntry.INDICATOR+" text,"+
            Contract.SearchEntry.URL+" text,"+Contract.SearchEntry.DATA+" text);";

    private final String DROP_SEARCH_TABLE = "drop table if exists "+Contract.SearchEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEARCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SEARCH_TABLE);
        onCreate(db);
    }


    public void saveSearch(String country, String topic, String indicator, String url, String data, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.SearchEntry.COUNTRY, country);
        contentValues.put(Contract.SearchEntry.TOPIC, topic);
        contentValues.put(Contract.SearchEntry.INDICATOR, indicator);
        contentValues.put(Contract.SearchEntry.URL, url);
        contentValues.put(Contract.SearchEntry.DATA, data);
        database.insertWithOnConflict(Contract.SearchEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor readSearch(SQLiteDatabase database) {
        String[] projections = {Contract.SearchEntry.COUNTRY,Contract.SearchEntry.TOPIC,Contract.SearchEntry.INDICATOR,Contract.SearchEntry.URL,Contract.SearchEntry.DATA};
        return database.query(Contract.SearchEntry.TABLE_NAME,projections,null,null,null,null,null);
    }
}
