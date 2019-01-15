package it.progetto.bra.worldbank;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import it.progetto.bra.worldbank.DAO.JsonDao;
import it.progetto.bra.worldbank.DAO.WriteLocalStoreTask;
import it.progetto.bra.worldbank.Entity.Chart;
import it.progetto.bra.worldbank.Entity.Country;
import it.progetto.bra.worldbank.Entity.Indicator;
import it.progetto.bra.worldbank.Entity.Search;
import it.progetto.bra.worldbank.Entity.Topic;
import it.progetto.bra.worldbank.Pattern.MySingleton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {


    private final static String TAG = "CHART_ACTIVITY";

    ArrayList<Entry> yValue = new ArrayList<>();
    ArrayList<String> xAxisLabel = new ArrayList<>();

    private String JSON;
    private List<Chart> mList = new ArrayList<>();

    // CHART
    private LineChart mLineChart;
    private BarChart mBarChart;

    // COMING FROM ONLINE
    private Country country;
    private Topic topic;
    private Indicator indicator;

    // COMING FROM OFFLINE
    private String countryFromDB;
    private String topicFromDB;
    private String indicatorFromDB;

    // PERMESSI
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private int RQCODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        verifyStoragePermissions(this);

        RQCODE = Objects.requireNonNull(getIntent().getExtras()).getInt("onlineornot");

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Drawable drawable = ContextCompat.getDrawable(this,R.drawable.world_bank_logo);
            assert drawable != null;
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newDrawable = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap, 480, 110, true));
            toolbar.setLogo(newDrawable);
        }

        mLineChart = findViewById(R.id.linechart);
        mBarChart = findViewById(R.id.barchart);
        ImageButton dl = findViewById(R.id.DL);
        ImageButton sl = findViewById(R.id.SL);
        ImageButton db = findViewById(R.id.DB);
        ImageButton sb = findViewById(R.id.SB);
        ImageButton fullScreen = findViewById(R.id.FULL_LINE);
        ImageButton fullBar = findViewById(R.id.FULL_BAR);
        dl.setOnClickListener(this);
        sl.setOnClickListener(this);
        db.setOnClickListener(this);
        sb.setOnClickListener(this);
        fullScreen.setOnClickListener(this);
        fullBar.setOnClickListener(this);

        // online
        if (RQCODE == 1) {
            country = Objects.requireNonNull(getIntent().getExtras()).getParcelable("country");
            topic = Objects.requireNonNull(getIntent().getExtras()).getParcelable("topic");
            indicator = Objects.requireNonNull(getIntent().getExtras()).getParcelable("indicator");
            onlineJob();
        // offline
        } else {
            List<Chart> mListOffline = new ArrayList<>();
            countryFromDB = Objects.requireNonNull(getIntent().getExtras()).getString("country");
            topicFromDB = Objects.requireNonNull(getIntent().getExtras()).getString("topic");
            indicatorFromDB = Objects.requireNonNull(getIntent().getExtras()).getString("indicator");
            String dataFromDB = Objects.requireNonNull(getIntent().getExtras()).getString("data");

            assert dataFromDB != null;
            String[] myData = dataFromDB.split("/");
            for (String s: myData) {
                String[] myValue = s.split(",");
                mListOffline.add(new Chart(myValue[0],Float.parseFloat(myValue[1])));
            }
            drawLineChart(mListOffline);
            drawBarChart(mListOffline);
        }
    }

    /*
    * Predefined value-formatter that formats large numbers in a pretty way.
     * Outputs: 856 = 856; 1000 = 1k; 5821 = 5.8k; 10500 = 10k; 101800 = 102k;
     * 2000000 = 2m; 7800000 = 7.8m; 92150000 = 92m; 123200000 = 123m; 9999999 =
     * 10m; 1000000000 = 1b; Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     *
     * @author Philipp Jahoda
     * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
    *
    * */

    public void drawLineChart(final List<Chart> chart) {

        xAxisLabel = new ArrayList<>();
        yValue = new ArrayList<>();

        int i = 0;
        for (Chart obj : chart) {
            xAxisLabel.add(obj.getDate());
            yValue.add(new Entry(i++,obj.getValue()));
        }

        Collections.sort(yValue,new EntryXComparator());
        Collections.sort(xAxisLabel);


        LineDataSet set1;
        set1 = new LineDataSet(yValue,getString(R.string.click_info));
        set1.setFillAlpha(110);
        set1.setColor(R.color.colorAccent);
        set1.setLineWidth(3f);

        //
        set1.setValueFormatter(new LargeValueFormatter());

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        YAxis rightYAxis = mLineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());

        // VALUE FORMATTER FOR DATE
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mLineChart.getDescription().setText(getString(R.string.description));
        mLineChart.setData(data);
        xAxis.setValueFormatter(new MyAxisValueFormatter(xAxisLabel));
        mLineChart.invalidate();
    }

    // CUSTOM FORMATTER!
    public class MyAxisValueFormatter implements IAxisValueFormatter {

        private ArrayList<String> mValues;

        public MyAxisValueFormatter(ArrayList<String> values) {
            mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues.get((int) value);
        }
    }

    /*
    * Predefined value-formatter that formats large numbers in a pretty way.
     * Outputs: 856 = 856; 1000 = 1k; 5821 = 5.8k; 10500 = 10k; 101800 = 102k;
     * 2000000 = 2m; 7800000 = 7.8m; 92150000 = 92m; 123200000 = 123m; 9999999 =
     * 10m; 1000000000 = 1b; Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     *
     * @author Philipp Jahoda
     * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
    * */


    public void drawBarChart(final List<Chart> chart) {

        ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<BarEntry> yValue = new ArrayList<>();
        int i = 0;
        for (Chart obj : chart) {
            xAxisLabel.add(obj.getDate());
            yValue.add(new BarEntry(i++,obj.getValue()));
        }
        Collections.sort(yValue,new EntryXComparator());
        Collections.sort(xAxisLabel);

        BarDataSet set1;
        set1 = new BarDataSet(yValue, getString(R.string.click_info));
        set1.setColor(R.color.colorAccent);
        set1.setValueFormatter(new LargeValueFormatter());

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        // VALUE FORMATTER FOR DATE
        XAxis xAxis = mBarChart.getXAxis();

        YAxis rightYAxis = mBarChart.getAxisRight();
        rightYAxis.setEnabled(false);

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mBarChart.getDescription().setText(getString(R.string.description));
        mBarChart.setData(data);
        xAxis.setValueFormatter(new MyAxisValueFormatter(xAxisLabel));
        mBarChart.invalidate();
    }

    private void onlineJob() {

        /*
         *  VOLLEY - onResponse is a callback method , quindi viene richiamato su un altro thread
         *  jsonArrayRequest non è bloccante quindi il thread UI continua --> rimane vuoto mList
         *  FUORI da onResponse!
         * */

        JSON = "http://api.worldbank.org/v2/countries/" + country.getIso2Code() + "/indicators/" + indicator.getId() + "?per_page=1000&format=json";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.get(1).toString().equals("null")) {
                        Toast.makeText(ChartActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {

                        JSONArray array = (JSONArray) response.get(1);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject chart = array.getJSONObject(i);
                            final Gson gson = new Gson();
                            mList.add(gson.fromJson(chart.toString(), Chart.class));
                        }
                        Search search = new Search(country.getName(), topic.getValue(), indicator.getName(), JSON, mList);
                        JsonDao jsonDao = new JsonDao(ChartActivity.this);
                        if (jsonDao.isJsonAlreadyIn(JSON)) {
                            Log.d("CHART_ACTIVITY","json already in, not adding!");
                        } else {
                            new WriteLocalStoreTask(ChartActivity.this).execute(search);
                        }
                        drawBarChart(mList);
                        drawLineChart(mList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(ChartActivity.this).addToRequestQueue(jsonArrayRequest);
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

                if (RQCODE == 1) {
                    Dialog mDialog = new Dialog(ChartActivity.this);
                    mDialog.setContentView(R.layout.information_dialog);
                    TextView title = mDialog.findViewById(R.id.titleText);
                    TextView text = mDialog.findViewById(R.id.objectText);
                    ImageView img = mDialog.findViewById(R.id.img);
                    String info = country.getName()+"\n"+topic.getValue()+"\n";
                    title.setText(info);
                    text.setText(indicator.getSourceNote());
                    img.setBackgroundResource(R.drawable.ic_chart);
                    mDialog.show();
                    return true;
                } else {
                    Dialog mDialog = new Dialog(ChartActivity.this);
                    mDialog.setContentView(R.layout.information_dialog);
                    TextView title = mDialog.findViewById(R.id.titleText);
                    TextView text = mDialog.findViewById(R.id.objectText);
                    ImageView img = mDialog.findViewById(R.id.img);
                    String info = countryFromDB+"\n"+topicFromDB+"\n";
                    title.setText(info);
                    text.setText(indicatorFromDB);
                    img.setBackgroundResource(R.drawable.ic_chart);
                    mDialog.show();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"CLICK");

        if (verifyStoragePermissions(ChartActivity.this)) {
            switch (v.getId()) {

                case R.id.FULL_BAR:
                    Intent intentBar = new Intent(ChartActivity.this,FullScreenBarChartActivity.class);
                    if (RQCODE == 1) {
                        intentBar.putExtra("text",indicator.getName());
                    }else{
                        intentBar.putExtra("text",indicatorFromDB);
                    }
                    intentBar.putExtra("x",new Gson().toJson(xAxisLabel));
                    intentBar.putExtra("plot",new Gson().toJson(yValue));
                    startActivity(intentBar);
                    break;

                case R.id.FULL_LINE:
                    Intent intent = new Intent(ChartActivity.this,FullScreenLineChartActivity.class);
                    if (RQCODE == 1) {
                        intent.putExtra("text",indicator.getName());
                    }else{
                        intent.putExtra("text",indicatorFromDB);
                    }
                    intent.putExtra("x",new Gson().toJson(xAxisLabel));
                    intent.putExtra("plot",new Gson().toJson(yValue));
                    startActivity(intent);
                    break;

                case R.id.DL:

                    Date objL = new Date();
                    long currentTimeL = objL.getTime();
                    String currentDayL = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(objL);
                    String filenameL = "Linechart "+currentTimeL+currentDayL;
                    String URL;
                    if (mLineChart.saveToGallery(filenameL,50)) {
                        Toast.makeText(ChartActivity.this, R.string.success_download,Toast.LENGTH_SHORT).show();
                        URL = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/"+filenameL;
                    }
                    break;

                case R.id.DB:

                    Date objB = new Date();
                    long currentTimeB = objB.getTime();
                    String currentDayB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(objB);
                    String filenameB = "Barchart "+currentTimeB+currentDayB;
                    if (mBarChart.saveToGallery("Barchart "+currentTimeB+currentDayB,50)) {
                        Toast.makeText(ChartActivity.this,getString(R.string.success_download),Toast.LENGTH_SHORT).show();
                        URL = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/"+filenameB;
                    }
                    break;
                case R.id.SL:
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    mLineChart.getChartBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory()+File.separator+"temp_file.jpg");
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+Environment.getExternalStorageDirectory().getPath()+"/temp_file.jpg"));
                    startActivity(Intent.createChooser(share, getString(R.string.share_title)));
                    break;

                case R.id.SB:
                    Intent shareB = new Intent(Intent.ACTION_SEND);
                    shareB.setType("image/jpeg");
                    ByteArrayOutputStream bytesB = new ByteArrayOutputStream();
                    mBarChart.getChartBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bytesB);
                    File fB = new File(Environment.getExternalStorageDirectory()+File.separator+"temp_file.jpg");
                    try {
                        fB.createNewFile();
                        FileOutputStream fo = new FileOutputStream(fB);
                        fo.write(bytesB.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    shareB.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+Environment.getExternalStorageDirectory().getPath()+"/temp_file.jpg"));
                    startActivity(Intent.createChooser(shareB, getString(R.string.share_title)));
                    break;
            }
        } else {
            Toast.makeText(ChartActivity.this, R.string.click_again,Toast.LENGTH_SHORT).show();
        }
    }
}
