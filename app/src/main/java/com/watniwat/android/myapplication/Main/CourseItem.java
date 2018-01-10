package com.watniwat.android.myapplication.Main;

import java.io.Serializable;

/**
 * Created by Niwat on 26-Dec-17.
 */

public class CourseItem implements Serializable {
    private String courseUId;
    private String courseId;
    private String courseName;
    private String courseDescription;
    private int membersCount;

    public CourseItem() {}

    public CourseItem(String courseUId, String courseName, String courseId, String courseDescription) {
        this.courseUId = courseUId;
        this.courseName = courseName;
        this.courseId = courseId;
        this.courseDescription = courseDescription;
        this.membersCount = 1;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseUId() {
        return courseUId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public int getMembersCount() {
        return membersCount;
    }

    @Override
    public String toString() {
        return "courseId: " + courseId + ", courseName: " + courseName;
    }
}
