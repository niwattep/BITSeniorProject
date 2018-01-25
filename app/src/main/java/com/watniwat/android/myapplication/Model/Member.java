package com.watniwat.android.myapplication.Model;

/**
 * Created by Niwat on 23-Jan-18.
 */
public class Member {
    private String name;
    private String photoUrl;

    public Member() { }

    public Member(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
