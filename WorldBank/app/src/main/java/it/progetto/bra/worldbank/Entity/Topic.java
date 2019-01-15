package it.progetto.bra.worldbank.Entity;

import android.os.Parcel;
import android.os.Parcelable;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class Topic implements Parcelable {
    private int id;
    private String value;
    private String sourceNote;
    private boolean clicked = false;

    public Topic(int id, String value, String sourceNote) {
        this.id = id;
        this.value = value;
        this.sourceNote = sourceNote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() { return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Topic() {
    }

    protected Topic(Parcel in) {
        value = in.readString();
        id = in.readInt();
        sourceNote = in.readString();
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeInt(id);
        dest.writeString(sourceNote);
    }
}
