package com.teamig.whatswap.models;

import android.net.Uri;

public class UserProfile {


    private String username;
    private String age;
    private String gender;
    private String community;
    private String profilePhotoUrl;


    public UserProfile(String username, String age, String gender, String community, String profilePhotoUrl) {
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.community = community;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public UserProfile() {
    }

}