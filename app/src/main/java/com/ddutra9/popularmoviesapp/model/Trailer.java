package com.ddutra9.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ddutra9 on 13/06/17.
 */

public class Trailer implements Parcelable {

    private Long id;
    private Long moveId;
    private String source;
    private String name;
    private String language;

    public Trailer(){
    }

    protected Trailer(Parcel in) {
        setId(in.readLong());
        setMoveId(in.readLong());
        setSource(in.readString());
        setName(in.readString());
        setLanguage(in.readString());
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeLong(getMoveId());
        dest.writeString(getSource());
        dest.writeString(getName());
        dest.writeString(getLanguage());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getMoveId() {
        return moveId;
    }

    public void setMoveId(Long moveId) {
        this.moveId = moveId;
    }
}
