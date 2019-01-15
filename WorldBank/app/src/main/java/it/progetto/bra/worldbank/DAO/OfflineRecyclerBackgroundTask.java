package it.progetto.bra.worldbank.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import it.progetto.bra.worldbank.Adapter.SearchRecyclerAdapter;
import it.progetto.bra.worldbank.Entity.Chart;
import it.progetto.bra.worldbank.Entity.Search;

import java.util.ArrayList;
import java.util.List;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class OfflineRecyclerBackgroundTask extends AsyncTask<Void, Search, Void> {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SearchRecyclerAdapter mAdapter;
    private List<Search> mList;
    private DbHelper dbHelper;
    private Cursor cursor;

    public OfflineRecyclerBackgroundTask(Context context, RecyclerView recyclerView, SearchRecyclerAdapter adapter, List<Search> list) {
        mContext = context;
        mRecyclerView = recyclerView;
        mAdapter = adapter;
        mList = list;
    }

    @Override
    protected void onPreExecute() {
        //adapter = new RecyclerAdapter(mContext, mList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String country, topic, indicator, url, data;

        dbHelper = new DbHelper(mContext);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        cursor = dbHelper.readSearch(database);
        while(cursor.moveToNext()) {
            List<Chart> supportList = new ArrayList<>();
            country = cursor.getString(cursor.getColumnIndex(Contract.SearchEntry.COUNTRY));
            topic = cursor.getString(cursor.getColumnIndex(Contract.SearchEntry.TOPIC));
            indicator = cursor.getString(cursor.getColumnIndex(Contract.SearchEntry.INDICATOR));
            url = cursor.getString(cursor.getColumnIndex(Contract.SearchEntry.URL));;
            data = cursor.getString(cursor.getColumnIndex(Contract.SearchEntry.DATA));

            String[] myData = data.split("/");
            for (String s: myData) {
                String[] myValue = s.split(",");
                supportList.add(new Chart(myValue[0],Float.parseFloat(myValue[1])));
            }

            publishProgress(new Search(country,topic,indicator,url,supportList));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Search... values) {
        mList.add(values[0]);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        cursor.close();
        dbHelper.close();
    }
}
