package it.progetto.bra.worldbank.Entity;

import android.os.Parcel;
import android.os.Parcelable;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

//PARCELABLE per serializzazione oggetti
public class Country implements Parcelable {

    private String name, capitalCity, iso2Code;
    private boolean clicked = false;

    public Country(String name, String capitalCity, String iso2Code) {
        this.name = name;
        this.capitalCity = capitalCity;
        this.iso2Code = iso2Code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    }

    public String getIso2Code() {
        return iso2Code;
    }

    public void setIso2Code(String iso2Code) {
        this.iso2Code = iso2Code;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public Country() {
    }

    protected Country(Parcel in) {
        name = in.readString();
        capitalCity = in.readString();
        iso2Code = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(capitalCity);
        dest.writeString(iso2Code);
    }
}
