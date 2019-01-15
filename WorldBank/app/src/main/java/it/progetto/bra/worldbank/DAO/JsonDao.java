package it.progetto.bra.worldbank.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class JsonDao {

    private static final String TAG = "JSON_DAO";

    private Context mContext;

    public JsonDao(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isJsonAlreadyIn(String jsonUrl) {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String count = "SELECT count(*) FROM "+Contract.SearchEntry.TABLE_NAME;

        Cursor mCursor = database.rawQuery(count, null);
        mCursor.moveToFirst();
        int icount = mCursor.getInt(0);
        if (icount > 0) {
            Log.d(TAG,"DB NOT EMPTY!");
            // search for occurence
            mCursor = dbHelper.readSearch(database);
            while (mCursor.moveToNext()) {
                String internalJsonUrl = mCursor.getString(mCursor.getColumnIndex(Contract.SearchEntry.URL));
                if (internalJsonUrl.equals(jsonUrl)) {
                    mCursor.close();
                    database.close();
                    return true; // ho trovato l'occorrenza
                }
            }
            mCursor.close();
            database.close();
            return false; // occorrenza non trovata
        } else {
            Log.d(TAG, "DB EMPTY!");
            mCursor.close();
            database.close();
            return false; // occorrenza non trovata
        }
    }
}
