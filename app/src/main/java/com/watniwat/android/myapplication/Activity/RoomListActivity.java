package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;
import com.watniwat.android.myapplication.Constant;
import com.watniwat.android.myapplication.Model.Room;
import com.watniwat.android.myapplication.Adapter.RoomAdapter;
import com.watniwat.android.myapplication.R;
import com.watniwat.android.myapplication.Utilities;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomListActivity extends AppCompatActivity {
    private static final int RC_CREATE_ROOM = 1234;
    private static final int RC_REGISTER_ROOM = 5678;
    public static final String EXTRA_ROOM_UID = "extra-room-uid";
    public static final String EXTRA_ROOM_NAME = "extra-room-name";
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mRoomRecyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private View mDrawerHeader;
    private CircleImageView mDrawerProfilePic;
    private TextView mDrawerDisplayName;
    private TextView mDrawerEmail;
    private SwipeRefreshLayout mSwipeToRefresh;
    private RoomAdapter mRoomsAdapter;
    private ArrayList<Room> rooms;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser user;

    private DatabaseReference mThisUserRoomsRef;

    private ValueEventListener mRoomEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);

        bindView();
        setupView();
        setupGoogleSignIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirebaseAuth();
        setupFirebaseDatabase();
        setupDrawer();
        loadRooms();
    }

    @Override
    protected void onStop() {
        super.onStop();
        rooms.clear();
    }

    private void bindView() {
        mRoomRecyclerView = findViewById(R.id.room_recycler_view);
        mToolbar = findViewById(R.id.toolbar);
        mFab = findViewById(R.id.fab);
        mDrawerLayout = findViewById(R.id.nav_drawer);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerHeader = mNavigationView.getHeaderView(0);
        mDrawerDisplayName = mDrawerHeader.findViewById(R.id.tv_drawer_name);
        mDrawerEmail = mDrawerHeader.findViewById(R.id.tv_drawer_email);
        mDrawerProfilePic = mDrawerHeader.findViewById(R.id.iv_drawer_profile);
        mSwipeToRefresh = findViewById(R.id.swipe_to_refresh);
        setSupportActionBar(mToolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

    }

    private void setupView() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), JoinRoomActivity.class), RC_REGISTER_ROOM);
            }
        });

        mRoomRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRooms();
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
        mThisUserRoomsRef = FirebaseDatabase.getInstance().getReference(Constant.USER_ROOMS).child(user.getUid());
    }

    private void setupDrawer() {
        if (user.getDisplayName() != null) {
            mDrawerDisplayName.setText(user.getDisplayName());
        } else {
            mDrawerDisplayName.setText(user.getEmail());
        }

        mDrawerEmail.setText(user.getEmail());
        if (user.getPhotoUrl() == null) {
            Glide.with(this).load(R.drawable.ic_002_boy_1).into(mDrawerProfilePic);
        } else Glide.with(this).load(user.getPhotoUrl()).into(mDrawerProfilePic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CREATE_ROOM) {
            if (resultCode == RESULT_OK) {
                Utilities.showToast("Room created", this);
            } else Utilities.showToast("Cancel creating room", this);
        }

        if (requestCode == RC_REGISTER_ROOM) {
            if (requestCode == RESULT_OK) {
                Utilities.showToast("Room Registered!", this);
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
        if (id == R.id.menu_create_room) {
            startActivityForResult(new Intent(this, CreateRoomActivity.class), RC_CREATE_ROOM);
            return true;
        }
        if (id == R.id.menu_sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadRooms() {
        rooms = new ArrayList<>();
        setupRoomsRecyclerView();
        mRoomEventListener = roomEventListener();
        mThisUserRoomsRef.addListenerForSingleValueEvent(mRoomEventListener);
        Log.d("ME", "Child event listener added. The size of rooms is " + rooms.size());
    }

    private void setupRoomsRecyclerView() {
        mRoomsAdapter = new RoomAdapter(this, rooms);
        mRoomsAdapter.setOnItemClickListener(onRoomClick());

        mRoomRecyclerView.setAdapter(mRoomsAdapter);
        mRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private ValueEventListener roomEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Room room = child.getValue(Room.class);
                    rooms.add(room);
                }
                mRoomsAdapter.notifyDataSetChanged();
                if (mSwipeToRefresh.isRefreshing()) {
                    mSwipeToRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private ChildEventListener onRoomsEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Room newRoom = dataSnapshot.getValue(Room.class);
                rooms.add(newRoom);
                mRoomsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Room removedRoom = dataSnapshot.getValue(Room.class);
                rooms.remove(removedRoom);
                mRoomsAdapter.notifyDataSetChanged();
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

    private RoomAdapter.OnItemClickListener onRoomClick() {
        return new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Room room = rooms.get(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(EXTRA_ROOM_UID, room.getRoomUId());
                intent.putExtra(EXTRA_ROOM_NAME, room.getRoomName());
                RoomListActivity.this.startActivity(intent);
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
