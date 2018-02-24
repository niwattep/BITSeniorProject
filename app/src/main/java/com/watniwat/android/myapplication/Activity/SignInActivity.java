package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.watniwat.android.myapplication.R;

public class SignInActivity extends AppCompatActivity {
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
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                onLoginFailure();
            }
        }
        if (requestCode == RC_EMAIL_SIGN_UP) {
            if (resultCode == RESULT_CANCELED) {
                Snackbar.make(mSignInButton, "Sign up fail", Snackbar.LENGTH_LONG).show();
            }
        }
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
                    Snackbar.make(mSignInButton, "Please enter your email and password", Snackbar.LENGTH_SHORT).show();
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        onLoginFailure();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void setupFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mFirebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    onLoginCompleted();
                }
            }
        };
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
        showProgressBar();
        mGoogleSignInButton.setEnabled(false);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void signInWithEmail() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onLoginCompleted();
                        } else {
                            onLoginFailure();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String idToken = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.child(user.getUid()).child("fcmToken").setValue(idToken);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void onLoginFailure() {
        mGoogleSignInButton.setEnabled(true);
        Snackbar.make(mSignInButton, "Sign In Fail. Please check your email and password again", Snackbar.LENGTH_SHORT).show();
    }

    private void showProgressBar() {
        progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
        layout.addView(progressBar);
    }

}
