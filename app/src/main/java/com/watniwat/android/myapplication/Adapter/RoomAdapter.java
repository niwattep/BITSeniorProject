package com.watniwat.android.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.watniwat.android.myapplication.ViewHolder.RoomViewHolder;
import com.watniwat.android.myapplication.Model.Room;
import com.watniwat.android.myapplication.R;

import java.util.List;

/**
 * Created by Niwat on 26-Dec-17.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {
    private Context context;
    private List<Room> roomList;
    private OnItemClickListener mListener;

    public RoomAdapter(Context context, List<Room> items) {
        this.context = context;
        this.roomList = items;
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup vg, int type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_room, vg, false);

        final RoomViewHolder viewHolder = new RoomViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(view, viewHolder.getAdapterPosition());
                    }
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RoomViewHolder viewHolder, int position) {
        Room item = roomList.get(position);
        viewHolder.roomNameTextView.setText(item.getRoomName());
        viewHolder.roomIdTextView.setText(item.getRoomId());

        if (item.getRoomPhotoUrl() != null) {
            Glide.with(context)
                    .load(item.getRoomPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.roomImageView);
        } else {
            String letter = String.valueOf(item.getRoomName().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(letter, generator.getRandomColor());
            viewHolder.roomImageView.setImageDrawable(textDrawable);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
