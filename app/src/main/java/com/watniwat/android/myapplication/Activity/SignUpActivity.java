package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.watniwat.android.myapplication.R;

public class SignUpActivity extends AppCompatActivity {

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
		mFirebaseAuth = FirebaseAuth.getInstance();
		String email = mEmailEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		final String displayName = mDisplayNameEditText.getText().toString();
		mFirebaseAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							FirebaseUser user = mFirebaseAuth.getCurrentUser();
							UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
									.setDisplayName(displayName).build();
							user.updateProfile(profileUpdates);
							DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
							userRef.child(user.getUid()).child("displayName").setValue(displayName);
							onLoginCompleted();
						} else {
							Log.d("MyLOG", "Signup fail" + task.getException().toString());
							onLoginFailure();
						}
					}
				});
	}

	private void onLoginCompleted() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

		String idToken = FirebaseInstanceId.getInstance().getToken();
		DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
		dbRef.child(user.getUid()).child("fcmToken").setValue(idToken);

		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	private void onLoginFailure() {
		setResult(RESULT_CANCELED);
		finish();
		Snackbar.make(mSignUpButton, "Sign In Fail. Please check you email and password again", Snackbar.LENGTH_SHORT).show();
	}
}
