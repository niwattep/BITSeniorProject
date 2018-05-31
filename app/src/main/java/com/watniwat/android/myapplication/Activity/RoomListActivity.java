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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomListActivity extends AppCompatActivity {
    private static final int RC_CREATE_ROOM = 1234;
    private static final int RC_REGISTER_ROOM = 5678;
    private static final int RC_SIGN_IN = 9010;
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

    private DatabaseReference mThisUserRoomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);

        bindView();
        setupView();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startSignIn();
        }
        setupDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startSignIn();
        } else {
            loadRooms();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rooms != null) {
            rooms.clear();
        }
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

    private void setupDrawer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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
            if (resultCode == RESULT_OK) {
                Utilities.showToast("Room Registered!", this);
            }
        }

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_CANCELED) {
                if (response != null) {
                    Toast.makeText(this, "Sign-in Error", Toast.LENGTH_LONG).show();
                }
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mThisUserRoomsRef = FirebaseDatabase.getInstance().getReference(Constant.USER_ROOMS).child(user.getUid());
            mThisUserRoomsRef.addListenerForSingleValueEvent(roomEventListener());
        }
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
        AuthUI.getInstance().signOut(this).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSignOutFailure();
            }
        });
    }

    private void onSignOutFailure() {
        Utilities.showToast("Sign-out failed. Please try again.", this);
    }

    private void startSignIn() {
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }
}
