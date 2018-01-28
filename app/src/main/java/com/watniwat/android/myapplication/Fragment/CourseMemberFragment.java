package com.watniwat.android.myapplication.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.watniwat.android.myapplication.Adapter.MemberAdapter;
import com.watniwat.android.myapplication.Model.Member;
import com.watniwat.android.myapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseMemberFragment extends Fragment {
    private final int GRID_COLUMN = 2;
    public static final String ARG_PARAM_COURSE_UID = "courseUId";
    public static final String EXTRA_COURSE_UID = "courseUId";
    private String courseUId;

    private List<Member> memberList;

    private RecyclerView mMemberRecyclerView;
    private MemberAdapter mMemberAdapter;

    public CourseMemberFragment() { }

    public static CourseMemberFragment newInstance(String courseUId) {
        CourseMemberFragment courseMemberFragment = new CourseMemberFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_COURSE_UID, courseUId);
        courseMemberFragment.setArguments(args);
        return courseMemberFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseUId = getArguments().getString(ARG_PARAM_COURSE_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_member, container, false);

        bindView(view);
        loadData();

        return view;
    }

    private void bindView(View view) {
        mMemberRecyclerView = view.findViewById(R.id.rv_member);
    }

    private void loadData() {
        memberList = new ArrayList<>();
        mMemberAdapter = new MemberAdapter(this.getContext(), memberList);
        mMemberRecyclerView.setAdapter(mMemberAdapter);
        mMemberRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference courseMembersRef = FirebaseDatabase.getInstance()
                .getReference("course-users/" + courseUId);
        courseMembersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Member member = dataSnapshot.getValue(Member.class);
                memberList.add(member);
                mMemberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

