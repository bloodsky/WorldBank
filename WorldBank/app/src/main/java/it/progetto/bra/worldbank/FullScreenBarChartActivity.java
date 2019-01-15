package it.progetto.bra.worldbank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

public class FullScreenBarChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // per vedere grafico a schermo intero
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_bar_chart);

        BarChart barChart = findViewById(R.id.barchart);
        String description = Objects.requireNonNull(getIntent().getExtras()).getString("text");
        ArrayList<BarEntry> yValue = new Gson().fromJson(getIntent().getStringExtra("plot"), new TypeToken<ArrayList<BarEntry>>(){}.getType());
        ArrayList<String> xAxisLabel = new Gson().fromJson(getIntent().getStringExtra("x"), new TypeToken<ArrayList<String>>(){}.getType());

        Collections.sort(yValue,new EntryXComparator());
        Collections.sort(xAxisLabel);

        BarDataSet set1 = new BarDataSet(yValue, getString(R.string.click_info));

        set1.setColor(R.color.colorAccent);
        set1.setValueFormatter(new LargeValueFormatter());
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        // VALUE FORMATTER FOR DATE
        XAxis xAxis = barChart.getXAxis();
        barChart.getDescription().setText(getString(R.string.description));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.setData(data);
        xAxis.setValueFormatter(new MyAxisValueFormatter(xAxisLabel));
        barChart.invalidate();

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
