package it.progetto.bra.worldbank.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import it.progetto.bra.worldbank.Entity.Chart;
import it.progetto.bra.worldbank.Entity.Search;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class WriteLocalStoreTask extends AsyncTask<Search, Void, Void> {

    private Context mContext;
    private StringBuilder data = new StringBuilder();

    public WriteLocalStoreTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Search... s) {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (Chart obj : s[0].getData()) {
            data.append(obj.getDate()).append(",").append(obj.getValue()).append("/");
        }

        dbHelper.saveSearch(s[0].getCountry(),
                s[0].getTopic(),s[0].getIndicator(),
                s[0].getURL(),data.toString(),database);

        database.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }
}
