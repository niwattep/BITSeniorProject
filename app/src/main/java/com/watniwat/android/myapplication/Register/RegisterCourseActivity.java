package com.watniwat.android.myapplication.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.watniwat.android.myapplication.R;
import com.watniwat.android.myapplication.SignIn.SignInActivity;

public class RegisterCourseActivity extends AppCompatActivity {
    private EditText mCourseIdInputEditText;
    private Button mRegisterCourseButton;

    private FirebaseUser user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserCoursesRef;
    private DatabaseReference mCourseIdRef;
    private DatabaseReference mCourseUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_course);

        bindView();
        setupFirebaseAuth();
        setupFirebaseDatabase();
    }

    private void bindView() {
        mCourseIdInputEditText = findViewById(R.id.edt_courseId);
        mRegisterCourseButton = findViewById(R.id.btn_register_course);
    }

    private void setupFirebaseAuth() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        } else {
            goToLoginScreen();
        }
    }

    private void setupFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserCoursesRef = mFirebaseDatabase.getReference("user-courses");
        mCourseIdRef = mFirebaseDatabase.getReference("course-ids");
        mCourseUsersRef = mFirebaseDatabase.getReference("course-users");
    }

    private void registerCourse(final String courseId) {
        mCourseIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(courseId)) {
                    String courseUId = dataSnapshot.child(courseId).getValue(String.class);
                    String userUId = user.getUid();
                    //mUserCoursesRef.child(userUId).child(courseUId).child("courseName").setValue()
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToLoginScreen() {
        finish();
        startActivity(new Intent(this, SignInActivity.class));
    }
}
