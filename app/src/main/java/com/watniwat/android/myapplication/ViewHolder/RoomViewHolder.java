package com.watniwat.android.myapplication.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.watniwat.android.myapplication.R;

/**
 * Created by Niwat on 26-Dec-17.
 */

public class RoomViewHolder extends RecyclerView.ViewHolder {
    public ImageView roomImageView;
    public TextView roomNameTextView;
    public TextView roomIdTextView;

    public RoomViewHolder(View view) {
        super(view);
        roomImageView = view.findViewById(R.id.img_room_image);
        roomNameTextView = view.findViewById(R.id.tv_room_name);
        roomIdTextView = view.findViewById(R.id.tv_room_id);
    }


}
