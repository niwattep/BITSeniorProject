package com.watniwat.android.myapplication.Chat;

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

    private String userUId;
    private String userName;
    private String dataType;
    private String data;

    public Message() {}

    public Message(String userUId, String userName, String dataType, String data) {
        this.userUId = userUId;
        this.userName = userName;
        this.dataType = dataType;
        this.data = data;
    }

    public String getUserUId() {
        return userUId;
    }

    public String getUserName() {
        return userName;
    }

    public String getType() {
        return dataType;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "userName: " + userName + ", data: " + data + ", type: " + dataType;
    }
}
