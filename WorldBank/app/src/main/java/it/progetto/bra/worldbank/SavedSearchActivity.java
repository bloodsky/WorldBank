package it.progetto.bra.worldbank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import it.progetto.bra.worldbank.Adapter.SearchRecyclerAdapter;
import it.progetto.bra.worldbank.DAO.OfflineRecyclerBackgroundTask;
import it.progetto.bra.worldbank.Entity.Chart;
import it.progetto.bra.worldbank.Entity.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class SavedSearchActivity extends AppCompatActivity implements Callback {

    private List<Search> searchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search);

        android.support.v7.widget.Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.world_bank_logo);
            Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(drawable)).getBitmap();
            Drawable newDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 480, 110, true));
            toolbar.setLogo(newDrawable);
        }

        RecyclerView recyclerView = findViewById(R.id.SearchRecyclerView);
        SearchRecyclerAdapter searchRecyclerAdapter = new SearchRecyclerAdapter(this, searchList);
        new OfflineRecyclerBackgroundTask(this, recyclerView, searchRecyclerAdapter, searchList).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chart_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                finish();
                return true;
            case R.id.informationboutchart:
                Toast.makeText(SavedSearchActivity.this, getString(R.string.no_info),Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void notifyObject(Object obj) {

        if (obj instanceof Search) {
            StringBuilder data = new StringBuilder();
            for (Chart chart : ((Search) obj).getData()) {
                data.append(chart.getDate()).append(",").append(chart.getValue()).append("/");
            }
            Intent intent = new Intent(SavedSearchActivity.this, ChartActivity.class);
            intent.putExtra("onlineornot", 0);
            intent.putExtra("country", ((Search) obj).getCountry());
            intent.putExtra("topic", ((Search) obj).getTopic());
            intent.putExtra("indicator", ((Search) obj).getIndicator());
            intent.putExtra("data", data.toString());
            startActivity(intent);
        }
    }

    @Override
    public void notifyFirstChoice(int id) {
    }
}
