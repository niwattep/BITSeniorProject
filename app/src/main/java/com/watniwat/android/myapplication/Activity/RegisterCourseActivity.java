package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
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
	private final int INPUT_MAX = 10;
	private final int INPUT_MIN = 4;
	private EditText mCourseIdInputEditText;
	private Button mRegisterCourseButton;
	private TextInputLayout mCourseIdInputTIL;
	private Toolbar mToolbar;

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
		setupView();
	}

	private void bindView() {
		mCourseIdInputEditText = findViewById(R.id.edt_courseId);
		mRegisterCourseButton = findViewById(R.id.btn_register_course);
		mCourseIdInputTIL = findViewById(R.id.til_courseId);
		mToolbar = findViewById(R.id.toolbar);

	}

	private void setupView() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mRegisterCourseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mCourseIdInputEditText.getText().toString().isEmpty()) {
					registerCourse(mCourseIdInputEditText.getText().toString());
				}
			}
		});
		mCourseIdInputTIL.setCounterMaxLength(INPUT_MAX);
		mCourseIdInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.length() < INPUT_MIN || charSequence.length() > INPUT_MAX) {
					mCourseIdInputTIL.setError("Course ID must have between 4 and 10 characters ");
					mCourseIdInputTIL.setHintTextAppearance(R.style.error_text_appearance);
					mRegisterCourseButton.setEnabled(false);
				} else {
					mCourseIdInputTIL.setError("");
					mCourseIdInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
					mRegisterCourseButton.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
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
				} else
					Snackbar.make(mRegisterCourseButton, "Course not found", Snackbar.LENGTH_SHORT)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {

								}
							}).show();
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
