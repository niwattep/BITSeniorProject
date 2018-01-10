package com.watniwat.android.myapplication.Create;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.watniwat.android.myapplication.Main.CourseItem;
import com.watniwat.android.myapplication.R;

public class CreateCourseActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_NAME = "name";
    private EditText mCourseNameEditText;
    private EditText mCourseIdEditText;
    private EditText mCourseDescriptionEditText;
    private Button mCreateCourseButton;

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mCoursesRef;
    private DatabaseReference mCourseIdRef;
    private DatabaseReference mUserCoursesRef;
    private DatabaseReference mCourseUsersRef;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        bindView();
        setListener();
        setupDatabase();
        setupUser();
    }

    private void bindView() {
        mCourseNameEditText = findViewById(R.id.edt_course_name);
        mCourseIdEditText = findViewById(R.id.edt_course_id);
        mCourseDescriptionEditText = findViewById(R.id.edt_course_description);
        mCreateCourseButton = findViewById(R.id.btn_create_course);
    }

    private void setListener() {
        mCreateCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = getIntent();
                String courseName = mCourseNameEditText.getText().toString();
                String courseId = mCourseIdEditText.getText().toString();
                String courseDescription = mCourseDescriptionEditText.getText().toString();
                if (!courseName.isEmpty() && !courseId.isEmpty()) {

                    validateAndAddNewCourse(courseName, courseId, courseDescription);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter course name and course id", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mCoursesRef = firebaseDatabase.getReference("courses");
        mCourseIdRef = firebaseDatabase.getReference("course-ids");
        mUserCoursesRef = firebaseDatabase.getReference("user-courses");
        mCourseUsersRef = firebaseDatabase.getReference("course-users");
        mUsersRef = firebaseDatabase.getReference("users");
    }

    private void setupUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void validateAndAddNewCourse(final String courseName, final String courseId, final String courseDescription) {
        mCourseIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(courseId)) {
                    Intent intent = getIntent();

                    String courseUId = mCoursesRef.push().getKey();
                    CourseItem course = new CourseItem(courseUId, courseName, courseId, courseDescription);

                    mCoursesRef.child(courseUId).setValue(course);
                    mUserCoursesRef.child(user.getUid()).child(courseUId).setValue(course);
                    //mUsersRef.child(user.getUid()).child("courses").child(courseUId).setValue(true);
                    mCourseIdRef.child(course.getCourseId()).setValue(courseUId);
                    mCourseUsersRef.child(courseUId).child(user.getUid()).child("name").setValue(user.getDisplayName());
                    //mCoursesRef.child(courseUId).child("members").child(user.getUid()).setValue(true);
                    //mCourseUsersRef.child(courseUId).child(user.getUid()).child("img").setValue(user.getPhotoUrl());

                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Course id is already existed", Toast.LENGTH_SHORT).show();
                    mCourseIdEditText.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
