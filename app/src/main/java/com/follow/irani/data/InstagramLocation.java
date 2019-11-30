package com.follow.irani.data;

import android.os.Parcel;
import android.os.Parcelable;

public class InstagramLocation implements Parcelable {

    private String id;
    private String name;
    private double lat;
    private double lng;

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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    protected InstagramLocation(Parcel in) {
        id = in.readString();
        name = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public InstagramLocation() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @SuppressWarnings("unused")
    public static final Creator<InstagramLocation> CREATOR = new Creator<InstagramLocation>() {
        @Override
        public InstagramLocation createFromParcel(Parcel in) {
            return new InstagramLocation(in);
        }

        @Override
        public InstagramLocation[] newArray(int size) {
            return new InstagramLocation[size];
        }
    };
}