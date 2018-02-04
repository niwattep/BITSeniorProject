package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.watniwat.android.myapplication.Model.CourseItem;
import com.watniwat.android.myapplication.Adapter.CourseItemAdapter;
import com.watniwat.android.myapplication.R;
import com.watniwat.android.myapplication.Utilities;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int RC_CREATE_COURSE = 1234;
    private static final int RC_REGISTER_COURSE = 5678;
    public static final String EXTRA_COURSE_UID = "extra-course-uid";
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mCourseRecyclerView;
    private CourseItemAdapter mCourseItemAdapter;
    private ArrayList<CourseItem> courseItems;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser user;

    private DatabaseReference mUserCoursesRef;
    private DatabaseReference mThisUserCoursesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        setupView();
        setupGoogleSignIn();
        setupFirebaseAuth();
        setupFirebaseDatabase();



    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCourses();
    }

    private void bindView() {
        mCourseRecyclerView = findViewById(R.id.course_recycler_view);
        mToolbar = findViewById(R.id.toolbar);
        mFab = findViewById(R.id.fab);
        setSupportActionBar(mToolbar);
    }

    private void setupView() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), RegisterCourseActivity.class), RC_REGISTER_COURSE);
            }
        });

        mCourseRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !mFab.isShown())
                    mFab.show();
                else if(dy > 0 && mFab.isShown())
                    mFab.hide();
            }
        });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void setupFirebaseAuth() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            goToLoginScreen();
        }
    }

    private void setupFirebaseDatabase() {
        mUserCoursesRef = FirebaseDatabase.getInstance().getReference("user-courses");
        mThisUserCoursesRef = mUserCoursesRef.child(user.getUid());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CREATE_COURSE) {
            if (resultCode == RESULT_OK) {
                Utilities.showToast("Course created", this);
            } else Utilities.showToast("Cancel creating course", this);
        }

        if (requestCode == RC_REGISTER_COURSE) {
            if (requestCode == RESULT_OK) {
                Utilities.showToast("Course Registered!", this);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.menu_create_course) {
            startActivityForResult(new Intent(this, CreateCourseActivity.class), RC_CREATE_COURSE);
            return true;
        }
        if (id == R.id.menu_sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCourses() {
        courseItems = new ArrayList<>();
        setupCourseItemsRecyclerView();
        mThisUserCoursesRef.addChildEventListener(onCourseItemsEventListener());
        Log.d("ME", "Child event listener added. The size of courseItems is " + courseItems.size());
    }

    private void setupCourseItemsRecyclerView() {
        mCourseItemAdapter = new CourseItemAdapter(this, courseItems);
        mCourseItemAdapter.setOnItemClickListener(onCourseItemClick());

        mCourseRecyclerView.setAdapter(mCourseItemAdapter);
        mCourseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private ChildEventListener onCourseItemsEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CourseItem newCourse = dataSnapshot.getValue(CourseItem.class);
                courseItems.add(newCourse);
                mCourseItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CourseItem removedCourse = dataSnapshot.getValue(CourseItem.class);
                courseItems.remove(removedCourse);
                mCourseItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utilities.showToast("Error loading data.", getApplicationContext());
            }
        };
    }

    private CourseItemAdapter.OnItemClickListener onCourseItemClick() {
        return new CourseItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CourseItem course = courseItems.get(position);
                String courseUId = course.getCourseUId();
                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                intent.putExtra(EXTRA_COURSE_UID, courseUId);
                getApplicationContext().startActivity(intent);
            }
        };
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            FirebaseAuth.getInstance().signOut();
                            goToLoginScreen();
                        } else {
                            onSignOutFailure();
                        }
                    }
                });
    }

    private void onSignOutFailure() {
        Utilities.showToast("Sign out failed. Please try again.", this);
    }

    private void goToLoginScreen() {
        finish();
        startActivity(new Intent(this, SignInActivity.class));
    }
}
