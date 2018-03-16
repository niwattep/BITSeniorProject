package com.watniwat.android.myapplication.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.watniwat.android.myapplication.Constant;
import com.watniwat.android.myapplication.R;

public class SignUpActivity extends DialogActivity {

	private TextInputLayout mEmailTIL;
	private EditText mEmailEditText;
	private TextInputLayout mPasswordTIL;
	private EditText mPasswordEditText;
	private TextInputLayout mDisplayNameTIL;
	private EditText mDisplayNameEditText;
	private Button mSignUpButton;

	private FirebaseAuth mFirebaseAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		bindView();
		setupView();
	}

	private void bindView() {
		mEmailTIL = findViewById(R.id.til_email_input);
		mEmailEditText = findViewById(R.id.edt_email_input);
		mPasswordTIL = findViewById(R.id.til_password_input);
		mPasswordEditText = findViewById(R.id.edt_password_input);
		mDisplayNameTIL = findViewById(R.id.til_display_name_input);
		mDisplayNameEditText = findViewById(R.id.edt_display_name_input);
		mSignUpButton = findViewById(R.id.btn_sign_up);
	}

	private void setupView() {
		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signUpWithEmail();
			}
		});
	}

	private void signUpWithEmail() {
		showLoading();
		String email = mEmailEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		final String displayName = mDisplayNameEditText.getText().toString();
		FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							FirebaseUser user = mFirebaseAuth.getCurrentUser();
							UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
									.setDisplayName(displayName).build();
							user.updateProfile(profileUpdates);
							DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constant.USERS);
							userRef.child(user.getUid()).child("displayName").setValue(displayName);
							onSignUpCompleted();
						} else {
							Log.d("MyLOG", "Signup fail" + task.getException().toString());
							onLoginFailure();
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						onLoginFailure();
					}
				});
	}

	private void onSignUpCompleted() {
		hideLoading();

		setResult(RESULT_OK);
		finish();
	}

	private void onLoginFailure() {
		hideLoading();
		setResult(RESULT_CANCELED);
		finish();
		Snackbar.make(mSignUpButton, "Sign In Fail. Please check you email and password again", Snackbar.LENGTH_SHORT).show();
	}
}
