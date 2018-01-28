package com.watniwat.android.myapplication.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.watniwat.android.myapplication.Fragment.CourseMemberFragment;
import com.watniwat.android.myapplication.Fragment.CourseMenuFragment;

/**
 * Created by Niwat on 03-Jan-18.
 */

public class CourseMenuFragmentAdapter extends FragmentStatePagerAdapter {
    private final int TAB_NUMBER = 2;
    private String[] tabNames = {"Menu", "Members"};

    private String courseUId;
    private String courseName;

    public CourseMenuFragmentAdapter(FragmentManager fm, String courseUId, String courseName) {
        super(fm);
        this.courseUId = courseUId;
        this.courseName = courseName;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return CourseMenuFragment.newInstance(courseUId, courseName);
            case 1: return CourseMemberFragment.newInstance(courseUId);
            default: return CourseMenuFragment.newInstance(courseUId, courseName);
        }
    }

    @Override
    public int getCount() {
        return TAB_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }
}
