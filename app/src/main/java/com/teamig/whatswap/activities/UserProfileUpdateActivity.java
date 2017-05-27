package com.teamig.whatswap.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teamig.whatswap.R;

public class UserProfileUpdateActivity extends AppCompatActivity {

    private static final String TAG = "ProfileUpdateActivity";

    private EditText mUsernameEditText;
    private EditText mUserAgeEditText;
    private EditText mGenderEditText;
    private EditText mCommunityEditText;

    private Button mSaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update);

        mUsernameEditText = (EditText) findViewById(R.id.profile_username);
        mUserAgeEditText = (EditText) findViewById(R.id.profile_age);
        mGenderEditText = (EditText) findViewById(R.id.profile_gender);
        mCommunityEditText = (EditText) findViewById(R.id.profile_community);

        mSaveButton = (Button) findViewById(R.id.btn_profile_save);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_profile();
            }
        });


    }

    private void save_profile() {

        // Save data into Intent
        Intent intent = this.getIntent();
        intent.putExtra("username", mUsernameEditText.getText().toString());
        intent.putExtra("age", mUserAgeEditText.getText().toString());
        intent.putExtra("gender", mGenderEditText.getText().toString());
        intent.putExtra("community", mCommunityEditText.getText().toString());
        this.setResult(RESULT_OK, intent);

        Log.d(TAG, "User Data prepared to return.");
        finish();
    }


}
