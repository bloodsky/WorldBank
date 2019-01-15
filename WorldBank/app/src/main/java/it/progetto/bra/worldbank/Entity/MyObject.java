package it.progetto.bra.worldbank.Entity;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import java.util.List;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class MyObject implements Parcelable {

    private Country country;
    private Topic topic;
    private Indicator indicator;
    private List<Chart> chart;

    public MyObject(Country extern_country, Topic extern_topic, Indicator extern_indicator, List<Chart> extern_chart) {
        country = extern_country;
        topic = extern_topic;
        indicator = extern_indicator;
        chart = extern_chart;
    }

    public MyObject(Country country) {
        this.country = country;
    }

    public MyObject(Topic topic) {
        this.topic = topic;
    }

    public MyObject(Indicator indicator) { this.indicator = indicator; }

    public Country getCountry() {
        return country;
    }

    public Topic getTopic() {
        return topic;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected MyObject(Parcel in) {
        country = in.readTypedObject(Country.CREATOR);
        topic = in.readTypedObject(Topic.CREATOR);
        indicator = in.readTypedObject(Indicator.CREATOR);
    }

    public static final Creator<MyObject> CREATOR = new Creator<MyObject>() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public MyObject createFromParcel(Parcel in) {
            return new MyObject(in);
        }

        @Override
        public MyObject[] newArray(int size) {
            return new MyObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(country, flags);
        dest.writeParcelable(topic, flags);
        dest.writeParcelable(indicator, flags);
    }

    public List<Chart> getChart() {
        return chart;
    }

    public void setChart(List<Chart> chart) {
        this.chart = chart;
    }
}
