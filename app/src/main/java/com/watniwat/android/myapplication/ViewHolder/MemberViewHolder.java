package com.watniwat.android.myapplication.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.watniwat.android.myapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Niwat on 23-Jan-18.
 */
public class MemberViewHolder extends RecyclerView.ViewHolder {
	public CircleImageView mProfileImageView;
	public TextView mNameTextView;

	public MemberViewHolder(View itemView) {
		super(itemView);
		mProfileImageView = itemView.findViewById(R.id.iv_member_profile);
		mNameTextView = itemView.findViewById(R.id.tv_member_name);
	}
}
