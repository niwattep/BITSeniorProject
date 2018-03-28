package com.watniwat.android.myapplication.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.watniwat.android.myapplication.Constant;
import com.watniwat.android.myapplication.R;

public class ResetPasswordActivity extends DialogActivity {

	private EditText mEmailEditText;
	private Button mResetButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);

		bindView();
		setupView();
	}

	private void bindView() {
		mEmailEditText = findViewById(R.id.edt_email_input);
		mResetButton = findViewById(R.id.btn_reset_password);
	}

	private void setupView() {
		mResetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String email = mEmailEditText.getText().toString();
				if (email != null) {
					sendPasswordResetEmail(email);
				}
			}
		});
	}

	private void sendPasswordResetEmail(String email) {
		showLoading();
		FirebaseAuth.getInstance().sendPasswordResetEmail(email)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							hideLoading();
							Log.d(Constant.LOG_TAG, "Successfully reset password.");
							Snackbar.make(mResetButton, "A password reset email has been sent, please check your email.", Snackbar.LENGTH_INDEFINITE).show();
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						hideLoading();
						Log.d(Constant.LOG_TAG, "Fail to send password reset email. " + e.toString());
						Snackbar.make(mResetButton, "Fail to send password reset email, please check your email address.", Snackbar.LENGTH_INDEFINITE).show();
					}
				});
	}
}
