package com.watniwat.android.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.watniwat.android.myapplication.Model.Message;
import com.watniwat.android.myapplication.Adapter.MessageAdapter;
import com.watniwat.android.myapplication.Fragment.CourseMenuFragment;
import com.watniwat.android.myapplication.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mChatRecyclerView;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String courseUId;
    private ArrayList<Message> messageList;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesReference;
    private DatabaseReference mCourseMessagesReference;
    private DatabaseReference mThisCourseMessagesReference;
    private FirebaseUser user;

    private MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindView();
        setupListener();
        getIntentData();
        setupFirebaseAuth();
        setupFirebaseDatabase();

        loadMessages();
    }

    private void bindView() {
        mChatRecyclerView = findViewById(R.id.rv_chat);
        mMessageEditText = findViewById(R.id.edt_message);
        mSendButton = findViewById(R.id.btn_send_message);
    }

    private void setupListener() {
        mSendButton.setOnClickListener(onSendButtonClick());
        mMessageEditText.setOnFocusChangeListener(onMessageEditTextFocus());
    }

    private void setupFirebaseAuth() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        } else {
            goToLoginScreen();
        }
    }

    private void setupFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesReference = mFirebaseDatabase.getReference("messages");
        mCourseMessagesReference = mFirebaseDatabase.getReference("course-messages");
        mThisCourseMessagesReference = mCourseMessagesReference.child(courseUId);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(CourseMenuFragment.EXTRA_COURSE_UID)) {
            courseUId = intent.getStringExtra(CourseMenuFragment.EXTRA_COURSE_UID);
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
                        Message.DATA_TYPE_TEXT, text, user.getPhotoUrl().toString(),
                        System.currentTimeMillis());
                databaseReference.setValue(message);
                mMessageEditText.setText("");
            } else {
                goToLoginScreen();
            }
        }
    }

    private void goToLoginScreen() {
        finish();
        startActivity(new Intent(this, SignInActivity.class));
    }
}
