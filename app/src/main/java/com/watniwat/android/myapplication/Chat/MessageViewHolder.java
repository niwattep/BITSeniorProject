package com.watniwat.android.myapplication.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.watniwat.android.myapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Niwat on 06-Jan-18.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView mNameTextView;
    public TextView mMessageTextView;
    public ImageView mImageMessageImageView;
    public CircleImageView mProfileImageView;

    public MessageViewHolder(View view) {
        super(view);
        mNameTextView = view.findViewById(R.id.tv_name);
        mMessageTextView = view.findViewById(R.id.tv_message);
        mImageMessageImageView = view.findViewById(R.id.iv_message);
        mProfileImageView = view.findViewById(R.id.iv_profile);
    }
}
