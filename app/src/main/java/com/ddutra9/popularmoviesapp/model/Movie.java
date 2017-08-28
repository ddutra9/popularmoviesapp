package com.ddutra9.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ddutra9 on 13/06/17.
 */

public class Movie implements Parcelable {

    private String title;
    private String overview;
    private Double voteAverage;
    private Long releaseDate;
    private String posterPath;

    public Movie(){
    }

    public Movie(String title, String overview, Double voteAverage, Long releaseDate, String posterPath){
        this.setTitle(title);
        this.setOverview(overview);
        this.setVoteAverage(voteAverage);
        this.setReleaseDate(releaseDate);
        this.setPosterPath(posterPath);
    }

    protected Movie(Parcel in) {
        setTitle(in.readString());
        setOverview(in.readString());
        setPosterPath(in.readString());
        setVoteAverage(in.readDouble());
        setReleaseDate(in.readLong());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
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
        dest.writeLong(getReleaseDate());
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

    public Long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
