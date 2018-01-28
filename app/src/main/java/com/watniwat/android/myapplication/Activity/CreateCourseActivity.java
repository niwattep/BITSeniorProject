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

public class CreateCourseActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_NAME = "name";
    public static final int INPUT_NAME_MAX_LENGTH = 25;
    public static final int INPUT_NAME_MIN_LENGTH = 4;
    public static final int INPUT_ID_MAX_LENGTH = 10;
    public static final int INPUT_ID_MIN_LENGTH = 4;
    public static final int INPUT_DESC_MAX_LENGTH = 120;
    public static final int INPUT_DESC_MIN_LENGTH = 0;

    private EditText mCourseNameEditText;
    private EditText mCourseIdEditText;
    private EditText mCourseDescriptionEditText;
    private Button mCreateCourseButton;
    private TextInputLayout mCourseIdInputTIL;
    private TextInputLayout mCourseNameInputTIL;
    private TextInputLayout mCourseDescriptionInputTIL;
    private Toolbar mToolbar;

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
        setupView();
        setupDatabase();
        setupUser();
    }

    private void bindView() {
        mCourseNameEditText = findViewById(R.id.edt_course_name);
        mCourseIdEditText = findViewById(R.id.edt_course_id);
        mCourseDescriptionEditText = findViewById(R.id.edt_course_description);
        mCreateCourseButton = findViewById(R.id.btn_create_course);
        mToolbar = findViewById(R.id.toolbar);
        mCourseNameInputTIL = findViewById(R.id.til_course_name);
        mCourseDescriptionInputTIL = findViewById(R.id.til_course_description);
        mCourseIdInputTIL = findViewById(R.id.til_course_id);
    }

    private void setupView() {
    	setSupportActionBar(mToolbar);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCreateCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = mCourseNameEditText.getText().toString();
                String courseId = mCourseIdEditText.getText().toString();
                String courseDescription = mCourseDescriptionEditText.getText().toString();
                if (!courseName.isEmpty() && !courseId.isEmpty()) {
                    validateAndAddNewCourse(courseName, courseId, courseDescription);
                } else {
					Snackbar.make(mCreateCourseButton, "Please enter course name and course id", Snackbar.LENGTH_SHORT)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {

								}
							}).show();
                }
            }
        });
        mCourseNameInputTIL.setCounterMaxLength(INPUT_NAME_MAX_LENGTH);
        mCourseNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidNameLength(charSequence)) {
                    mCourseNameInputTIL.setError("Course Name must have between 4 and 25 characters");
                    mCourseNameInputTIL.setHintTextAppearance(R.style.error_text_appearance);
                    mCreateCourseButton.setEnabled(false);
                } else {
                    mCourseNameInputTIL.setError("");
                    mCourseNameInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
                    if (!isValidIdLength(mCourseIdEditText.getText())) {
						mCreateCourseButton.setEnabled(false);
					} else {
                    	mCreateCourseButton.setEnabled(true);
					}

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mCourseIdInputTIL.setCounterMaxLength(INPUT_ID_MAX_LENGTH);
        mCourseIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidIdLength(charSequence)) {
                    mCourseIdInputTIL.setError("Course ID must have between 4 and 10 characters");
                    mCourseIdInputTIL.setHintTextAppearance(R.style.error_text_appearance);
                    mCreateCourseButton.setEnabled(false);
                } else {
                    mCourseIdInputTIL.setError("");
                    mCourseIdInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
                    if (!isValidNameLength(mCourseNameEditText.getText())) {
						mCreateCourseButton.setEnabled(false);
					} else {
						mCreateCourseButton.setEnabled(true);
					}

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mCourseDescriptionInputTIL.setCounterMaxLength(INPUT_DESC_MAX_LENGTH);
        mCourseDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidDescLength(charSequence)) {
                    mCourseDescriptionInputTIL.setError("Course description must have no more than 120 characters");
					mCourseDescriptionInputTIL.setHintTextAppearance(R.style.error_text_appearance);
                    mCreateCourseButton.setEnabled(false);
                } else {
					mCourseDescriptionInputTIL.setError("");
					mCourseDescriptionInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
                    if (!isValidNameLength(mCourseNameEditText.getText()) || !isValidIdLength(mCourseIdEditText.getText())) {
						mCreateCourseButton.setEnabled(false);
					} else {
						mCreateCourseButton.setEnabled(true);
					}

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
                    mCourseIdRef.child(course.getCourseId()).setValue(courseUId);
                    mCourseUsersRef.child(courseUId).child(user.getUid()).child("name").setValue(user.getDisplayName());
                    mCourseUsersRef.child(courseUId).child(user.getUid()).child("photoUrl").setValue(user.getPhotoUrl().toString());

                    setResult(RESULT_OK, intent);
                    finish();
                } else {
					Snackbar.make(mCreateCourseButton, "This course id is already existed.", Snackbar.LENGTH_SHORT)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {

								}
							}).show();
                    mCourseIdEditText.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isValidNameLength(CharSequence s) {
    	return s.length() >= INPUT_NAME_MIN_LENGTH && s.length() <= INPUT_NAME_MAX_LENGTH;
	}

	private boolean isValidIdLength(CharSequence s) {
    	return s.length() >= INPUT_ID_MIN_LENGTH && s.length() <= INPUT_ID_MAX_LENGTH;
	}

	private boolean isValidDescLength(CharSequence s) {
    	return s.length() <= INPUT_DESC_MAX_LENGTH;
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
}
