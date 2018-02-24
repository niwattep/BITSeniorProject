package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.watniwat.android.myapplication.Model.Message;
import com.watniwat.android.myapplication.Adapter.MessageAdapter;
import com.watniwat.android.myapplication.Fragment.CourseMenuFragment;
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

    private String courseUId;
    private String courseName;
    private ArrayList<Message> messageList;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseMessagesReference;
    private DatabaseReference mThisCourseMessagesReference;
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
        getSupportActionBar().setTitle(courseName);
        mSendButton.setOnClickListener(onSendButtonClick());
        mSendImageButton.setOnClickListener(onSendImageButtonClick());
        mAttachFileButton.setOnClickListener(onAttachFileButtonClick());
        mMessageEditText.setOnFocusChangeListener(onMessageEditTextFocus());
    }

    private void setupFirebaseAuth() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) goToLoginScreen();
    }

    private void setupFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCourseMessagesReference = mFirebaseDatabase.getReference("course-messages");
        mThisCourseMessagesReference = mCourseMessagesReference.child(courseUId);
    }

    private void setupStorage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.EXTRA_COURSE_UID) && intent.hasExtra(MainActivity.EXTRA_COURSE_NAME)) {
            courseUId = intent.getStringExtra(MainActivity.EXTRA_COURSE_UID);
            courseName = intent.getStringExtra(MainActivity.EXTRA_COURSE_NAME);
        }
    }

    private void loadMessages() {
        messageList = new ArrayList<>();
        setupMessageRecyclerView();
        mThisCourseMessagesReference.addChildEventListener(onMessageEvent());
    }

    private void setupMessageRecyclerView() {
        mMessageAdapter = new MessageAdapter(this, messageList, courseUId, user.getUid());

        mChatRecyclerView.setAdapter(mMessageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mChatRecyclerView.setLayoutManager(linearLayoutManager);
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
        config.exportingSize = 800;
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
                DatabaseReference databaseReference = mThisCourseMessagesReference.push();
                Message message = new Message(user.getUid(), user.getDisplayName(),
                        Message.DATA_TYPE_TEXT, text, user.getPhotoUrl() == null? null : user.getPhotoUrl().toString(),
                        System.currentTimeMillis());
                databaseReference.setValue(message);
                mMessageEditText.setText("");
            } else {
                goToLoginScreen();
            }
        }
    }

    private void sendImageMessage(Bitmap pickedPhoto) {
        if (pickedPhoto != null) {
            final DatabaseReference databaseReference = mThisCourseMessagesReference.push();
            String fileName = databaseReference.getKey() + ".jpg";

            StorageReference photoReference = mStorageRef.child(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            pickedPhoto.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);

            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = photoReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Message message = new Message(user.getUid(), user.getDisplayName(),
                            Message.DATA_TYPE_IMAGE, taskSnapshot.getDownloadUrl().toString(),
                            user.getPhotoUrl() == null? null : user.getPhotoUrl().toString(),
                            System.currentTimeMillis());
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

    interface OnUploadPhotoSuccess {
        void sendDataToDatabase();
    }
}
