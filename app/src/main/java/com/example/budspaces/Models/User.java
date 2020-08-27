package com.example.budspaces.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.budspaces.Samples.RandomDateOfBirth;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;
import java.util.List;

/*
 * Created by troy379 on 04.04.17.
 */
public class User implements Parcelable {
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("showGroups")
    @Expose
    private Boolean showGroups;
    @SerializedName("showInterests")
    @Expose
    private Boolean showInterests;

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getShowGroups() {
        return showGroups;
    }

    public void setShowGroups(Boolean showGroups) {
        this.showGroups = showGroups;
    }

    public Boolean getShowInterests() {
        return showInterests;
    }

    public void setShowInterests(Boolean showInterests) {
        this.showInterests = showInterests;
    }

    /* ---------------------------------------------------------------------------------------- */

    protected User(Parcel in) {
//        id = in.readString();
        name = in.readString();
        picture = in.readString();
        email = in.readString();
        country = in.readString();
        gender = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(email);
        dest.writeString(country);
        dest.writeString(picture);
//        dest.writeLong(birthdate);//birthday.getTime());
    }

    @Override
    public String toString() {
        return "User{" +
                "interests=" + interests +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", id='" + id + '\'' +
                ", showGroups=" + showGroups +
                ", showInterests=" + showInterests +
                '}';
    }
}
