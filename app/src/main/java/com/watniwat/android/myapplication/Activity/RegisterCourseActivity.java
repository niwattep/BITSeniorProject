package com.watniwat.android.myapplication.Activity;

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
import com.watniwat.android.myapplication.Model.CourseItem;
import com.watniwat.android.myapplication.R;

public class RegisterCourseActivity extends AppCompatActivity {
	private EditText mCourseIdInputEditText;
	private Button mRegisterCourseButton;

	private FirebaseUser user;

	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mUserCoursesRef;
	private DatabaseReference mCourseIdRef;
	private DatabaseReference mCourseUsersRef;
	private DatabaseReference mCoursesRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_course);

		bindView();
		setupFirebaseAuth();
		setupFirebaseDatabase();
		setupListener();
	}

	private void bindView() {
		mCourseIdInputEditText = findViewById(R.id.edt_courseId);
		mRegisterCourseButton = findViewById(R.id.btn_register_course);
	}

	private void setupListener() {
		mRegisterCourseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mCourseIdInputEditText.getText().toString().isEmpty()) {
					Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
					registerCourse(mCourseIdInputEditText.getText().toString());
				}
			}
		});
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
		mCoursesRef = mFirebaseDatabase.getReference("courses");
	}

	private void registerCourse(final String courseId) {
		mCourseIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.hasChild(courseId)) {
					String courseUId = dataSnapshot.child(courseId).getValue(String.class);
					getCourse(courseUId);
					Toast.makeText(getApplicationContext(), "found", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	private void getCourse(final String courseUId) {
		mCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				DataSnapshot child = dataSnapshot.child(courseUId);
				CourseItem courseItem = child.getValue(CourseItem.class);
				register(courseItem);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	private void register(CourseItem courseItem) {
		mUserCoursesRef.child(user.getUid()).child(courseItem.getCourseUId()).setValue(courseItem);
		mCourseUsersRef.child(courseItem.getCourseUId()).child(user.getUid()).child("name").setValue(user.getDisplayName());
		mCourseUsersRef.child(courseItem.getCourseUId()).child(user.getUid()).child("photoUrl").setValue(user.getPhotoUrl().toString());

		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
		finish();
	}

	private void goToLoginScreen() {
		finish();
		startActivity(new Intent(this, SignInActivity.class));
	}
}
