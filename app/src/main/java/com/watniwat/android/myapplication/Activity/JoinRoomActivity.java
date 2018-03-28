package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.watniwat.android.myapplication.Constant;
import com.watniwat.android.myapplication.Model.Room;
import com.watniwat.android.myapplication.R;

public class JoinRoomActivity extends DialogActivity {
	private final int INPUT_MAX = 15;
	private final int INPUT_MIN = 4;
	private EditText mRoomIdInputEditText;
	private Button mJoinRoomButton;
	private TextInputLayout mRoomIdInputTIL;
	private Toolbar mToolbar;

	private FirebaseUser user;

	private FirebaseDatabase mFirebaseDatabase;
	private DatabaseReference mUserRoomsRef;
	private DatabaseReference mRoomIdRef;
	private DatabaseReference mRoomUsersRef;
	private DatabaseReference mRoomsRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);

		bindView();
		setupFirebaseAuth();
		setupFirebaseDatabase();
		setupView();
	}

	private void bindView() {
		mRoomIdInputEditText = findViewById(R.id.edt_room_id);
		mJoinRoomButton = findViewById(R.id.btn_join_room);
		mRoomIdInputTIL = findViewById(R.id.til_room_id);
		mToolbar = findViewById(R.id.toolbar);

	}

	private void setupView() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mJoinRoomButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mRoomIdInputEditText.getText().toString().isEmpty()) {
					joinRoom(mRoomIdInputEditText.getText().toString());
				}
			}
		});
		mRoomIdInputTIL.setCounterMaxLength(INPUT_MAX);
		mRoomIdInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.length() < INPUT_MIN || charSequence.length() > INPUT_MAX) {
					mRoomIdInputTIL.setError("Room ID must have between 4 and 15 characters ");
					mRoomIdInputTIL.setHintTextAppearance(R.style.error_text_appearance);
					mJoinRoomButton.setEnabled(false);
				} else {
					mRoomIdInputTIL.setError("");
					mRoomIdInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
					mJoinRoomButton.setEnabled(true);
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
		if (user == null) {
			goToLoginScreen();
		}
	}

	private void setupFirebaseDatabase() {
		mFirebaseDatabase = FirebaseDatabase.getInstance();
		mUserRoomsRef = mFirebaseDatabase.getReference(Constant.USER_ROOMS);
		mRoomIdRef = mFirebaseDatabase.getReference(Constant.ROOM_IDS);
		mRoomUsersRef = mFirebaseDatabase.getReference(Constant.ROOM_USERS);
		mRoomsRef = mFirebaseDatabase.getReference(Constant.ROOMS);
	}

	private void joinRoom(final String roomId) {
		showLoading();
		mRoomIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Log.d(Constant.LOG_TAG, "datasnapshot hasChild: " + dataSnapshot.hasChild(roomId));
				if (dataSnapshot.hasChild(roomId)) {
					String roomUId = dataSnapshot.child(roomId).getValue(String.class);
					Log.d(Constant.LOG_TAG, "Room UID: " + roomUId);
					getRoom(roomUId);
				} else {
					hideLoading();
					Snackbar.make(mJoinRoomButton, "Room not found", Snackbar.LENGTH_LONG)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {
								}
							}).show();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});
	}

	private void getRoom(final String roomUId) {
		mRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				DataSnapshot child = dataSnapshot.child(roomUId);
				Room room = child.getValue(Room.class);
				join(room);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	private void join(Room room) {
		mUserRoomsRef.child(user.getUid()).child(room.getRoomUId()).setValue(room);
		mRoomUsersRef.child(room.getRoomUId()).child(user.getUid()).setValue(true);
		FirebaseMessaging.getInstance().subscribeToTopic(room.getRoomUId());

		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
		hideLoading();
		finish();
	}

	private void goToLoginScreen() {
		finish();
		startActivity(new Intent(this, SignInActivity.class));
	}
}
