package com.ddutra9.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ddutra9 on 13/06/17.
 */

public class Review implements Parcelable {

    private Long id;
    private Long moveId;
    private String author;
    private String content;
    private String language;

    public Review(){
    }

    protected Review(Parcel in) {
        setId(in.readLong());
        setMoveId(in.readLong());
        setAuthor(in.readString());
        setContent(in.readString());
        setLanguage(in.readString());
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
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
        dest.writeString(getAuthor());
        dest.writeString(getContent());
        dest.writeString(getLanguage());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
