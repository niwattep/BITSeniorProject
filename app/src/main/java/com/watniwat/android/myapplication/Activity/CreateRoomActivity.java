package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.watniwat.android.myapplication.Constant;
import com.watniwat.android.myapplication.Model.Room;
import com.watniwat.android.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;

public class CreateRoomActivity extends DialogActivity {
    public static final int INPUT_NAME_MAX_LENGTH = 25;
    public static final int INPUT_NAME_MIN_LENGTH = 4;
    public static final int INPUT_ID_MAX_LENGTH = 15;
    public static final int INPUT_ID_MIN_LENGTH = 4;
    public static final int INPUT_DESC_MAX_LENGTH = 120;

    private EditText mRoomNameEditText;
    private EditText mRoomIdEditText;
    private EditText mRoomDescriptionEditText;
    private Button mCreateRoomButton;
    private TextInputLayout mRoomIdInputTIL;
    private TextInputLayout mRoomNameInputTIL;
    private TextInputLayout mRoomDescriptionInputTIL;
    private ImageView mRoomPhotoImageView;
    private Toolbar mToolbar;

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRoomsRef;
    private DatabaseReference mRoomIdRef;
    private DatabaseReference mUserRoomsRef;
    private DatabaseReference mRoomUsersRef;
    private DatabaseReference mUsersRef;

	private StorageReference mStorageRef;

	private Bitmap pickedPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        bindView();
        setupView();
    }

	@Override
	protected void onStart() {
		super.onStart();
		setupDatabase();
		setupUser();
		setupStorage();
	}

	private void bindView() {
        mRoomNameEditText = findViewById(R.id.edt_room_name);
        mRoomIdEditText = findViewById(R.id.edt_room_id);
        mRoomDescriptionEditText = findViewById(R.id.edt_room_description);
        mCreateRoomButton = findViewById(R.id.btn_create_room);
        mToolbar = findViewById(R.id.toolbar);
        mRoomNameInputTIL = findViewById(R.id.til_room_name);
        mRoomDescriptionInputTIL = findViewById(R.id.til_room_description);
        mRoomIdInputTIL = findViewById(R.id.til_room_id);
        mRoomPhotoImageView = findViewById(R.id.iv_room_photo);
    }

    private void setupView() {
    	setSupportActionBar(mToolbar);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mRoomPhotoImageView.setDrawingCacheEnabled(true);
    	mRoomPhotoImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseImage();
			}
		});

        mCreateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = mRoomNameEditText.getText().toString();
                String roomId = mRoomIdEditText.getText().toString();
                String roomDescription = mRoomDescriptionEditText.getText().toString();
                if (!roomName.isEmpty() && !roomId.isEmpty()) {
                    validateAndAddNewRoom(roomName, roomId, roomDescription);
                } else {
					Snackbar.make(mCreateRoomButton, "Please enter room name and room id", Snackbar.LENGTH_SHORT)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {

								}
							}).show();
                }
            }
        });

        mRoomNameInputTIL.setCounterMaxLength(INPUT_NAME_MAX_LENGTH);
        mRoomNameEditText.addTextChangedListener(new InputNameLisener());

        mRoomIdInputTIL.setCounterMaxLength(INPUT_ID_MAX_LENGTH);
        mRoomIdEditText.addTextChangedListener(new InputIdListener());

        mRoomDescriptionInputTIL.setCounterMaxLength(INPUT_DESC_MAX_LENGTH);
        mRoomDescriptionEditText.addTextChangedListener(new InputDescriptionListener());
    }

    private void setupDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRoomsRef = firebaseDatabase.getReference(Constant.ROOMS);
        mRoomIdRef = firebaseDatabase.getReference(Constant.ROOM_IDS);
        mUserRoomsRef = firebaseDatabase.getReference(Constant.USER_ROOMS);
        mRoomUsersRef = firebaseDatabase.getReference(Constant.ROOM_USERS);
        mUsersRef = firebaseDatabase.getReference(Constant.USERS);
    }

    private void setupStorage() {
    	mStorageRef = FirebaseStorage.getInstance().getReference();
	}

    private void setupUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void validateAndAddNewRoom(final String roomName, final String roomId, final String roomDescription) {
    	showLoading();
    	mRoomIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(roomId)) {

                    final String roomUId = mRoomsRef.push().getKey();

					uploadFileAndCreateRoom(roomUId, new OnUploadPhotoSuccess() {
						@Override
						public void sendDataToDatabase(String photoUrl) {
							Room room = new Room(roomUId, roomName, roomId, roomDescription, photoUrl);
							mRoomsRef.child(roomUId).setValue(room);
							mUserRoomsRef.child(user.getUid()).child(roomUId).setValue(room);
							mRoomIdRef.child(room.getRoomId()).setValue(roomUId);
							mRoomUsersRef.child(roomUId).child(user.getUid()).setValue(true);
							FirebaseMessaging.getInstance().subscribeToTopic(roomUId);

							hideLoading();

							setResult(RESULT_OK, getIntent());
							finish();
						}
					});
                } else {
                	hideLoading();
					Snackbar.make(mCreateRoomButton, "This room id is already existed.", Snackbar.LENGTH_SHORT)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {

								}
							}).show();
                    mRoomIdEditText.setText("");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE &&
				resultCode == RESULT_OK) {
			try {
				pickedPhoto = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
				mRoomPhotoImageView.setImageBitmap(pickedPhoto);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void chooseImage() {
		EZPhotoPickConfig config = new EZPhotoPickConfig();
		config.photoSource = PhotoSource.GALLERY;
		config.exportingSize = 500;
		EZPhotoPick.startPhotoPickActivity(this, config);
	}

	private void uploadFileAndCreateRoom(String roomUId, final OnUploadPhotoSuccess callback) {
    	if (pickedPhoto != null) {
			final StorageReference photoReference = mStorageRef.child(roomUId + ".jpg");
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			pickedPhoto.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);

			byte[] data = byteArrayOutputStream.toByteArray();
			UploadTask uploadTask = photoReference.putBytes(data);
			uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					callback.sendDataToDatabase(photoReference.getDownloadUrl().getResult().toString());
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					Snackbar.make(mCreateRoomButton, "Cannot upload file. Check your Internet connection.", Snackbar.LENGTH_SHORT)
							.setAction("OK", new View.OnClickListener() {
								@Override
								public void onClick(View view) {

								}
							}).show();
				}
			});
		} else callback.sendDataToDatabase(null);
	}

	class InputNameLisener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (!isValidNameLength(charSequence)) {
				mRoomNameInputTIL.setError("Room Name must have between" +
						INPUT_NAME_MIN_LENGTH +
						" and " + INPUT_NAME_MAX_LENGTH + " characters");
				mRoomNameInputTIL.setHintTextAppearance(R.style.error_text_appearance);
				mCreateRoomButton.setEnabled(false);
			} else {
				mRoomNameInputTIL.setError("");
				mRoomNameInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
				if (!isValidIdLength(mRoomIdEditText.getText())) {
					mCreateRoomButton.setEnabled(false);
				} else {
					mCreateRoomButton.setEnabled(true);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable editable) {
		}
	}

	class InputIdListener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (!isValidIdLength(charSequence)) {
				mRoomIdInputTIL.setError("Room ID must have between "+ INPUT_ID_MIN_LENGTH +" and " + INPUT_ID_MAX_LENGTH + " characters");
				mRoomIdInputTIL.setHintTextAppearance(R.style.error_text_appearance);
				mCreateRoomButton.setEnabled(false);
			} else {
				mRoomIdInputTIL.setError("");
				mRoomIdInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
				if (!isValidNameLength(mRoomNameEditText.getText())) {
					mCreateRoomButton.setEnabled(false);
				} else {
					mCreateRoomButton.setEnabled(true);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable editable) {
		}
	}

	class InputDescriptionListener implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (!isValidDescLength(charSequence)) {
				mRoomDescriptionInputTIL.setError("Room description must have no more than " + INPUT_DESC_MAX_LENGTH + " characters");
				mRoomDescriptionInputTIL.setHintTextAppearance(R.style.error_text_appearance);
				mCreateRoomButton.setEnabled(false);
			} else {
				mRoomDescriptionInputTIL.setError("");
				mRoomDescriptionInputTIL.setHintTextAppearance(R.style.text_input_text_appearance);
				if (!isValidNameLength(mRoomNameEditText.getText()) || !isValidIdLength(mRoomIdEditText.getText())) {
					mCreateRoomButton.setEnabled(false);
				} else {
					mCreateRoomButton.setEnabled(true);
				}

			}
		}

		@Override
		public void afterTextChanged(Editable editable) {
		}
	}

	interface OnUploadPhotoSuccess {
		void sendDataToDatabase(String photoUrl);
	}
}
