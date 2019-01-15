package it.progetto.bra.worldbank.Entity;


import android.os.Parcel;
import android.os.Parcelable;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class Chart implements Parcelable{
    private String date;
    private float value;


    public Chart(String date, Float value) {
        this.date = date;
        this.value = value;
    }

    protected Chart(Parcel in) {
        date = in.readString();
        value = in.readFloat();
    }

    public static final Creator<Chart> CREATOR = new Creator<Chart>() {
        @Override
        public Chart createFromParcel(Parcel in) {
            return new Chart(in);
        }

        @Override
        public Chart[] newArray(int size) {
            return new Chart[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeFloat(value);
    }
}
