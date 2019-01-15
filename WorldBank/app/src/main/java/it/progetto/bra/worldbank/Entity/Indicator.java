package it.progetto.bra.worldbank.Entity;

import android.os.Parcel;
import android.os.Parcelable;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class Indicator implements Parcelable {
    private String id;
    private String name;
    private String sourceNote;
    private boolean clicked = false;

    public Indicator(String id, String name, String sourceNote) {
        this.id = id;
        this.name = name;
        this.sourceNote = sourceNote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceNote() {
        return sourceNote;
    }

    public void setSourceNote(String sourceNote) {
        this.sourceNote = sourceNote;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public Indicator() {
    }

    protected Indicator(Parcel in) {
        name = in.readString();
        id = in.readString();
        sourceNote = in.readString();
    }

    public static final Creator<Indicator> CREATOR = new Creator<Indicator>() {
        @Override
        public Indicator createFromParcel(Parcel in) {
            return new Indicator(in);
        }

        @Override
        public Indicator[] newArray(int size) {
            return new Indicator[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(sourceNote);
    }
}
