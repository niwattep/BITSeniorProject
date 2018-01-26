package com.watniwat.android.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.watniwat.android.myapplication.Model.Member;
import com.watniwat.android.myapplication.R;
import com.watniwat.android.myapplication.ViewHolder.MemberViewHolder;

import java.util.List;

/**
 * Created by Niwat on 23-Jan-18.
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder> {
	private Context context;
	private List<Member> memberList;

	public MemberAdapter(Context context, List<Member> memberList) {
		this.context = context;
		this.memberList = memberList;
	}

	@Override
	public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.member_view, parent, false);

		MemberViewHolder memberViewHolder = new MemberViewHolder(view);

		return memberViewHolder;
	}

	@Override
	public void onBindViewHolder(MemberViewHolder holder, int position) {
		Member member = memberList.get(position);
		holder.mNameTextView.setText(member.getName());
		Glide.with(holder.itemView.getContext()).load(member.getPhotoUrl()).into(holder.mProfileImageView);
	}

	@Override
	public int getItemCount() {
		return memberList.size();
	}
}
