package com.watniwat.android.myapplication.Course;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.watniwat.android.myapplication.Chat.ChatActivity;
import com.watniwat.android.myapplication.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseMenuFragment extends Fragment {
    public static final String ARG_PARAM_COURSE_UID = "courseUId";
    public static final String EXTRA_COURSE_UID = "courseUId";
    private Button mChatButton;
    private String courseUId;

    public CourseMenuFragment() {
        // Required empty public constructor
    }

    public static CourseMenuFragment newInstance(String courseUId) {
        CourseMenuFragment courseMenuFragment = new CourseMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_COURSE_UID, courseUId);
        courseMenuFragment.setArguments(args);
        return courseMenuFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseUId = getArguments().getString(ARG_PARAM_COURSE_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_menu, container, false);

        bindView(view);
        setupListener();

        return view;
    }

    private void bindView(View view){
        mChatButton = view.findViewById(R.id.btn_chat);
    }

    private void setupListener() {
        mChatButton.setOnClickListener(onChatButtonClick());
    }

    private View.OnClickListener onChatButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(EXTRA_COURSE_UID, courseUId);
                startActivity(intent);
            }
        };
    }

}
