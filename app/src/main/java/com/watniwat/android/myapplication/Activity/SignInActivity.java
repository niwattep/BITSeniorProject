package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.watniwat.android.myapplication.R;

public class SignInActivity extends DialogActivity {
    private static final int RC_GOOGLE_SIGN_IN = 1234;
    private static final int RC_EMAIL_SIGN_UP = 5678;

    private TextInputLayout mEmailTIL;
    private EditText mEmailEditText;
    private TextInputLayout mPasswordTIL;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private TextView mSignUpClickableTextView;
    private ProgressBar progressBar;

    private SignInButton mGoogleSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().hide();

        bindView();
        setupView();

        setupGoogleSignIn();
        setupFirebaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void bindView() {
        mGoogleSignInButton = findViewById(R.id.btn_google_sign_in);
        mEmailTIL = findViewById(R.id.til_email_input);
        mEmailEditText = findViewById(R.id.edt_email_input);
        mPasswordTIL = findViewById(R.id.til_password_input);
        mPasswordEditText = findViewById(R.id.edt_password_input);
        mSignInButton = findViewById(R.id.btn_sign_in);
        mSignUpClickableTextView = findViewById(R.id.tv_clickable_sign_up);
    }

    private void setupView() {
        mSignUpClickableTextView.setPaintFlags(mSignUpClickableTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mGoogleSignInButton.setOnClickListener(onSignInClick());
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEmailEditText.getText().toString().isEmpty() && !mPasswordEditText.getText().toString().isEmpty()) {
                    signInWithEmail();
                } else {
                    Snackbar.make(mSignInButton, "Please enter your email and password", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mSignUpClickableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), SignUpActivity.class), RC_EMAIL_SIGN_UP);
            }
        });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupFirebaseAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onLoginCompleted();
        }
    }

    private View.OnClickListener onSignInClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        };
    }

    private void signInWithGoogle() {
        showLoading();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void signInWithEmail() {
        showLoading();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onLoginCompleted();
                        } else {
                            onLoginFailure();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onLoginFailure();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                onLoginFailure();
            }
        }
        if (requestCode == RC_EMAIL_SIGN_UP) {
            if (resultCode == RESULT_CANCELED) {
                Snackbar.make(mSignInButton, "Sign up fail", Snackbar.LENGTH_LONG).show();
            } else if (requestCode == RESULT_OK) {
                onLoginCompleted();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onLoginCompleted();
                        } else {
                            onLoginFailure();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onLoginFailure();
                    }
                });
    }

    private void onLoginCompleted() {
        hideLoading();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String idToken = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.child(user.getUid()).child("fcmToken").setValue(idToken);

        startActivity(new Intent(this, RoomListActivity.class));
        finish();
    }

    private void onLoginFailure() {
        hideLoading();

        Snackbar.make(mSignInButton, "Sign In Fail. Please check your email and password again", Snackbar.LENGTH_LONG).show();
    }
}
