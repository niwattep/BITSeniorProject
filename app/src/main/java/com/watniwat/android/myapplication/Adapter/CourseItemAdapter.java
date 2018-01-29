package com.watniwat.android.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.watniwat.android.myapplication.ViewHolder.CourseItemViewHolder;
import com.watniwat.android.myapplication.Model.CourseItem;
import com.watniwat.android.myapplication.R;

import java.util.List;

/**
 * Created by Niwat on 26-Dec-17.
 */

public class CourseItemAdapter extends RecyclerView.Adapter<CourseItemViewHolder> {
    private Context context;
    private List<CourseItem> courseItemList;
    private OnItemClickListener mListener;
    private final int COURSE_ITEM_KEY = 1;

    public CourseItemAdapter(Context context, List<CourseItem> items) {
        this.context = context;
        this.courseItemList = items;
    }

    @Override
    public int getItemCount() {
        return courseItemList.size();
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
    public CourseItemViewHolder onCreateViewHolder(ViewGroup vg, int type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.courses_view_layout, vg, false);

        final CourseItemViewHolder viewHolder = new CourseItemViewHolder(view);

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
    public void onBindViewHolder(CourseItemViewHolder viewHolder, int position) {
        CourseItem item = courseItemList.get(position);
        String letter = String.valueOf(item.getCourseName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(letter, generator.getRandomColor());
        viewHolder.courseImageView.setImageDrawable(textDrawable);
        viewHolder.courseNameTextView.setText(item.getCourseName());
        viewHolder.courseIdTextView.setText(item.getCourseId());
        Glide.with(context).load(item.getCoursePhotoUrl()).into(viewHolder.coursePhotoImageView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
