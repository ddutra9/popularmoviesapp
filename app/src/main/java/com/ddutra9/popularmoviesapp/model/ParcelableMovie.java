package com.ddutra9.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ddutra9 on 13/06/17.
 */

public class ParcelableMovie implements Parcelable {

    private String title;
    private String overview;
    private Double voteAverage;
    private String releaseDate;
    private String posterPath;

    public ParcelableMovie(){
    }

    public ParcelableMovie(String title, String overview, Double voteAverage, String releaseDate, String posterPath){
        this.setTitle(title);
        this.setOverview(overview);
        this.setVoteAverage(voteAverage);
        this.setReleaseDate(releaseDate);
        this.setPosterPath(posterPath);
    }

    protected ParcelableMovie(Parcel in) {
        setTitle(in.readString());
        setOverview(in.readString());
        setPosterPath(in.readString());
        setVoteAverage(in.readDouble());
        setReleaseDate(in.readString());
    }

    public static final Creator<ParcelableMovie> CREATOR = new Creator<ParcelableMovie>() {
        @Override
        public ParcelableMovie createFromParcel(Parcel in) {
            return new ParcelableMovie(in);
        }

        @Override
        public ParcelableMovie[] newArray(int size) {
            return new ParcelableMovie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getOverview());
        dest.writeString(getPosterPath());
        dest.writeDouble(getVoteAverage());
        dest.writeString(getReleaseDate());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
