package com.watniwat.android.myapplication.Model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Niwat on 04-Jan-18.
 */

public class Message {
    public static final int TYPE_MY_MESSAGE = 1;
    public static final int TYPE_OTHER_MESSAGE = 2;
    public static final String DATA_TYPE_TEXT = "text";
    public static final String DATA_TYPE_IMAGE = "image";
    public static final String DATA_TYPE_FILE = "file";

    private String userUId;
    private String userName;
    private String dataType;
    private String data;
    private String photoUrl;
    private long timeStamp;

    public Message() {}

    public Message(String userUId, String userName, String dataType, String data, String photoUrl, long timeStamp) {
        this.userUId = userUId;
        this.userName = userName;
        this.dataType = dataType;
        this.data = data;
        this.photoUrl = photoUrl;
        this.timeStamp = timeStamp;
    }

    public String getUserUId() {
        return userUId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getData() {
        return data;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "userName: " + userName + ", data: " + data + ", type: " + dataType;
    }
}
