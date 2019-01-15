package it.progetto.bra.worldbank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class FullScreenLineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // per vedere grafico a shermo intero
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen);

        LineChart lineChart = findViewById(R.id.linechart);
        String description = Objects.requireNonNull(getIntent().getExtras()).getString("text");
        ArrayList<Entry> yValue = new Gson().fromJson(getIntent().getStringExtra("plot"), new TypeToken<ArrayList<Entry>>(){}.getType());
        ArrayList<String> xAxisLabel = new Gson().fromJson(getIntent().getStringExtra("x"), new TypeToken<ArrayList<String>>(){}.getType());

        Collections.sort(yValue,new EntryXComparator());
        Collections.sort(xAxisLabel);

        LineDataSet set1;

        set1 = new LineDataSet(yValue, getString(R.string.click_info));

        set1.setFillAlpha(110);
        set1.setColor(R.color.colorAccent);
        set1.setLineWidth(3f);
        set1.setValueFormatter(new LargeValueFormatter());

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        // VALUE FORMATTER FOR DATE
        lineChart.getDescription().setText(getString(R.string.description));
        XAxis xAxis = lineChart.getXAxis();
        lineChart.setData(data);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new MyAxisValueFormatter(xAxisLabel));
        lineChart.invalidate();
    }

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
}
