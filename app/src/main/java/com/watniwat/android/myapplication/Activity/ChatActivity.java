package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.watniwat.android.myapplication.Constant;
import com.watniwat.android.myapplication.Fragment.ConfirmDialog;
import com.watniwat.android.myapplication.Model.Message;
import com.watniwat.android.myapplication.Adapter.MessageAdapter;
import com.watniwat.android.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mChatRecyclerView;
    private EditText mMessageEditText;
    private ImageView mAttachFileButton;
    private ImageView mSendImageButton;
    private Button mSendButton;
    private Toolbar mToolbar;

    private String roomUId;
    private String roomName;
    private ArrayList<Message> messageList;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRoomMessagesReference;
    private DatabaseReference mThisRoomMessagesReference;
    private FirebaseUser user;

    private MessageAdapter mMessageAdapter;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getIntentData();
        bindView();
        setupView();
        setupFirebaseAuth();
        setupFirebaseDatabase();
        setupStorage();
        loadMessages();
    }

    private void bindView() {
        mChatRecyclerView = findViewById(R.id.rv_chat);
        mMessageEditText = findViewById(R.id.edt_message);
        mSendButton = findViewById(R.id.btn_send_message);
        mToolbar = findViewById(R.id.toolbar);
        mAttachFileButton = findViewById(R.id.btn_attach_file);
        mSendImageButton = findViewById(R.id.btn_send_image);
    }

    private void setupView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(roomName);
        mSendButton.setOnClickListener(onSendButtonClick());
        mSendImageButton.setOnClickListener(onSendImageButtonClick());
        mAttachFileButton.setOnClickListener(onAttachFileButtonClick());
        mMessageEditText.setOnFocusChangeListener(onMessageEditTextFocus());
    }

    private void setupFirebaseAuth() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            goToLoginScreen();
        }
    }

    private void setupFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRoomMessagesReference = mFirebaseDatabase.getReference(Constant.ROOM_MESSAGES);
        mThisRoomMessagesReference = mRoomMessagesReference.child(roomUId);
    }

    private void setupStorage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(RoomListActivity.EXTRA_ROOM_UID) && intent.hasExtra(RoomListActivity.EXTRA_ROOM_NAME)) {
            roomUId = intent.getStringExtra(RoomListActivity.EXTRA_ROOM_UID);
            roomName = intent.getStringExtra(RoomListActivity.EXTRA_ROOM_NAME);
        }
    }

    private void loadMessages() {
        messageList = new ArrayList<>();
        setupMessageRecyclerView();
        mThisRoomMessagesReference.addChildEventListener(onMessageEvent());
    }

    private void setupMessageRecyclerView() {
        mMessageAdapter = new MessageAdapter(this, messageList, roomUId, user.getUid());

        mChatRecyclerView.setAdapter(mMessageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mChatRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.chat_leave_room) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            ConfirmDialog dialog = ConfirmDialog.newInstance("Do you want to leave this room", "Yes", "No");
            dialog.setOnFinishDialogListener(new ConfirmDialog.OnFinishDialogListener() {
                @Override
                public void onFinish(ConfirmDialog.Button button) {
                    if (button == ConfirmDialog.Button.POSITIVE) {
                        leaveRoom();
                        finish();
                    } else {
                        //TODO cancel
                    }
                }
            });
            dialog.show(fragmentManager, null);
        }

        return super.onOptionsItemSelected(item);
    }

    private ChildEventListener onMessageEvent() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(0, message);
                mMessageAdapter.notifyDataSetChanged();
                mChatRecyclerView.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    private View.OnClickListener onSendButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        };
    }

    private View.OnClickListener onSendImageButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        };
    }

    private View.OnClickListener onAttachFileButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFile();
            }
        };
    }

    private void pickImage() {
        EZPhotoPickConfig config = new EZPhotoPickConfig();
        config.photoSource = PhotoSource.GALLERY;
        config.exportingSize = 500;
        EZPhotoPick.startPhotoPickActivity(this, config);
    }

    private View.OnFocusChangeListener onMessageEditTextFocus() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mMessageEditText.isFocused()) {
                    mChatRecyclerView.scrollToPosition(0);
                }
            }
        };
    }

    private void sendMessage() {
        String text = mMessageEditText.getText().toString();
        if (!text.isEmpty()) {
            if (user != null) {
                DatabaseReference databaseReference = mThisRoomMessagesReference.push();
                Message message = new Message(user.getUid(), user.getDisplayName(),
                        Message.DATA_TYPE_TEXT, text, user.getPhotoUrl() == null? null : user.getPhotoUrl().toString(),
                        System.currentTimeMillis(), roomName);
                databaseReference.setValue(message);
                mMessageEditText.setText("");
            } else {
                goToLoginScreen();
            }
        }
    }

    private void sendImageMessage(Bitmap pickedPhoto) {
        if (pickedPhoto != null) {
            final DatabaseReference databaseReference = mThisRoomMessagesReference.push();
            String fileName = databaseReference.getKey() + ".jpg";

            StorageReference photoReference = mStorageRef.child(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            pickedPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = photoReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Message message = new Message(user.getUid(), user.getDisplayName(),
                            Message.DATA_TYPE_IMAGE, taskSnapshot.getDownloadUrl().toString(),
                            user.getPhotoUrl() == null? null : user.getPhotoUrl().toString(),
                            System.currentTimeMillis(), roomName);
                    databaseReference.setValue(message);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Send image failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void pickFile() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE &&
                resultCode == RESULT_OK) {
            try {
                Bitmap pickedPhoto = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                sendImageMessage(pickedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goToLoginScreen() {
        finish();
        startActivity(new Intent(this, SignInActivity.class));
    }

    private void leaveRoom() {
        DatabaseReference roomUsers = mFirebaseDatabase.getReference(Constant.ROOM_USERS);
        DatabaseReference userRooms = mFirebaseDatabase.getReference(Constant.USER_ROOMS);
        roomUsers.child(roomUId).child(user.getUid()).removeValue();
        userRooms.child(user.getUid()).child(roomUId).removeValue();
    }
}
