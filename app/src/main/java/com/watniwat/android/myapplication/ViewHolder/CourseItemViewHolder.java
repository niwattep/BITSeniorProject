package com.watniwat.android.myapplication.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.watniwat.android.myapplication.R;

/**
 * Created by Niwat on 26-Dec-17.
 */

public class CourseItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView courseImageView;
    public TextView courseNameTextView;
    public TextView courseIdTextView;

    public CourseItemViewHolder(View view) {
        super(view);
        courseImageView = view.findViewById(R.id.img_course_image);
        courseNameTextView = view.findViewById(R.id.tv_course_name);
        courseIdTextView = view.findViewById(R.id.tv_course_id);
    }


}
