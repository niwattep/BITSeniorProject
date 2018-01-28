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
import com.watniwat.android.myapplication.Model.CourseItem;
import com.watniwat.android.myapplication.R;

public class CourseDetailActivity extends AppCompatActivity {
    private TextView mCourseNameTextView;
    private TextView mCourseIdTextView;
    private TextView mCourseMemberCountTextView;
    private ViewPager mCourseDetailsViewPager;
    private TabLayout mCourseDetailsTabLayout;

    private CourseItem course;
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
        mCourseNameTextView = findViewById(R.id.tv_course_name);
        mCourseMemberCountTextView = findViewById(R.id.tv_course_member_count);
        mCourseIdTextView = findViewById(R.id.tv_course_id);
        mCourseDetailsViewPager = findViewById(R.id.course_details_view_pager);
        mCourseDetailsTabLayout = findViewById(R.id.course_details_tab_layout);
    }

    private void createTabs() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        CourseMenuFragmentAdapter pagerAdapter = new CourseMenuFragmentAdapter(fragmentManager, courseUId, course.getCourseName());
        mCourseDetailsViewPager.setAdapter(pagerAdapter);
        //mCourseDetailsTabLayout.setTabsFromPagerAdapter(pagerAdapter);
        mCourseDetailsTabLayout.setupWithViewPager(mCourseDetailsViewPager);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.EXTRA_COURSE_UID)) {
            courseUId = intent.getStringExtra(MainActivity.EXTRA_COURSE_UID);
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
        mCourseNameTextView.setText(course.getCourseName());
        mCourseMemberCountTextView.setText(String.valueOf(course.getMembersCount()));
        mCourseIdTextView.setText(course.getCourseId());
        createTabs();
    }

    private ValueEventListener onLoadCourseData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                course = dataSnapshot.getValue(CourseItem.class);
                setupViewData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}
