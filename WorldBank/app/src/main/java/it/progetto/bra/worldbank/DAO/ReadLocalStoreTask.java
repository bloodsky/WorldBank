package it.progetto.bra.worldbank.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import it.progetto.bra.worldbank.ChartActivity;
import it.progetto.bra.worldbank.Entity.Chart;

import java.util.ArrayList;
import java.util.List;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class ReadLocalStoreTask extends AsyncTask<String , List<Chart>, Void> {

    Context mContext;
    ChartActivity ca = new ChartActivity();

    public ReadLocalStoreTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(String... voids) {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor mCursor;
        List<Chart> mList = new ArrayList<>();

        //String country = "";
        //String topic = "";
        //String indicator = "";
        String data = "";

        mCursor = dbHelper.readSearch(database);
        while (mCursor.moveToNext()) {
            String internalJsonUrl = mCursor.getString(mCursor.getColumnIndex(Contract.SearchEntry.URL));
            if (internalJsonUrl.equals(voids[0])) {

                //country = mCursor.getString(mCursor.getColumnIndex(Contract.SearchEntry.COUNTRY));
                //topic = mCursor.getString(mCursor.getColumnIndex(Contract.SearchEntry.TOPIC));
                //indicator = mCursor.getString(mCursor.getColumnIndex(Contract.SearchEntry.INDICATOR));
                data = mCursor.getString(mCursor.getColumnIndex(Contract.SearchEntry.DATA));
            }
        }

        mCursor.close();
        database.close();

        String[] myData = data.split("/");
        for (String s: myData) {
            String[] myValue = s.split(",");
            mList.add(new Chart(myValue[0],Float.parseFloat(myValue[1])));
        }

        publishProgress(mList);
        return null;
    }

    @Override
    protected void onProgressUpdate(List<Chart>... values) {
    }
}

