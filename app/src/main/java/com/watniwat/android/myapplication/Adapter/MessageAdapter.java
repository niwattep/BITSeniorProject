package com.watniwat.android.myapplication.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.watniwat.android.myapplication.ViewHolder.MessageViewHolder;
import com.watniwat.android.myapplication.Model.Message;
import com.watniwat.android.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Niwat on 06-Jan-18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private Context context;
    private List<Message> messageList;
    private String courseUId;
    private String uId;

    public MessageAdapter(Context context, List<Message> messageList, String courseUId, String uId) {
        this.context = context;
        this.messageList = messageList;
        this.courseUId = courseUId;
        this.uId = uId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Message.TYPE_MY_MESSAGE) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.my_message_view_layout, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.other_message_view_layout, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        Log.d("HELLO", message.getDataType() + ", data: " + message.getData());

        if (message.getDataType().equals(Message.DATA_TYPE_IMAGE)) {
            holder.mImageMessageImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(message.getData())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.mImageMessageImageView);
        }
        if (message.getDataType().equals(Message.DATA_TYPE_TEXT)) {
            Glide.with(holder.itemView.getContext()).clear(holder.mImageMessageImageView);
            holder.mImageMessageImageView.setVisibility(View.GONE);
            holder.mMessageTextView.setText(message.getData());
        }
        holder.mNameTextView.setText(message.getUserName());

        if (message.getPhotoUrl() == null) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_002_boy_1).into(holder.mProfileImageView);
        } else Glide.with(holder.itemView.getContext()).load(message.getPhotoUrl()).into(holder.mProfileImageView);

        Date dateTime = new Date(message.getTimeStamp());
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        holder.mTimeStampTextView.setText(formatter.format(dateTime));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getUserUId() != null && message.getUserUId().equals(uId)) {
            return Message.TYPE_MY_MESSAGE;
        } else {
            return Message.TYPE_OTHER_MESSAGE;
        }
    }
}
