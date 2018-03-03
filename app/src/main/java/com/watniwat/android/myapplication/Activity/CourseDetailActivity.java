package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.watniwat.android.myapplication.Adapter.CourseMenuFragmentAdapter;
import com.watniwat.android.myapplication.Model.Room;
import com.watniwat.android.myapplication.R;

public class CourseDetailActivity extends AppCompatActivity {
    private TextView mCourseNameTextView;
    private TextView mCourseIdTextView;
    private ViewPager mCourseDetailsViewPager;
    private TabLayout mCourseDetailsTabLayout;

    private Room course;
    private String courseUId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        bindView();
        getIntentData();
        setupFirebaseDatabase();
        setupListener();
    }

    private void bindView() {
        mCourseNameTextView = findViewById(R.id.tv_room_name);
        mCourseIdTextView = findViewById(R.id.tv_room_id);
        mCourseDetailsViewPager = findViewById(R.id.course_details_view_pager);
        mCourseDetailsTabLayout = findViewById(R.id.course_details_tab_layout);
    }

    private void createTabs() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        CourseMenuFragmentAdapter pagerAdapter = new CourseMenuFragmentAdapter(fragmentManager, courseUId, course.getRoomName());
        mCourseDetailsViewPager.setAdapter(pagerAdapter);
        //mCourseDetailsTabLayout.setTabsFromPagerAdapter(pagerAdapter);
        mCourseDetailsTabLayout.setupWithViewPager(mCourseDetailsViewPager);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(RoomListActivity.EXTRA_ROOM_UID)) {
            courseUId = intent.getStringExtra(RoomListActivity.EXTRA_ROOM_UID);
        }

    }

    private void setupFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        if (!courseUId.isEmpty()) {
            mCourseReference = mFirebaseDatabase.getReference("courses").child(courseUId);
        }
    }

    private void setupListener() {
        mCourseReference.addListenerForSingleValueEvent(onLoadCourseData());
    }

    private void setupViewData() {
        mCourseNameTextView.setText(course.getRoomName());
        mCourseIdTextView.setText(course.getRoomId());
        createTabs();
    }

    private ValueEventListener onLoadCourseData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                course = dataSnapshot.getValue(Room.class);
                setupViewData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}
