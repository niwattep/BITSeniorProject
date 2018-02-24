package com.watniwat.android.myapplication.Model;

import java.io.Serializable;

/**
 * Created by Niwat on 26-Dec-17.
 */

public class Room implements Serializable {
    private String roomUId;
    private String roomId;
    private String roomName;
    private String roomDescription;
    private String roomPhotoUrl;

    public Room() {}

    public Room(String roomUId, String roomName, String roomId, String roomDescription, String roomPhotoUrl) {
        this.roomUId = roomUId;
        this.roomName = roomName;
        this.roomId = roomId;
        this.roomDescription = roomDescription;
        this.roomPhotoUrl = roomPhotoUrl;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomUId() {
        return roomUId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public String getRoomPhotoUrl() {
        return roomPhotoUrl;
    }

    @Override
    public String toString() {
        return "roomId: " + roomId + ", roomName: " + roomName;
    }
}
