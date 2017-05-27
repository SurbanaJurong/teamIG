package com.teamig.whatswap.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.teamig.whatswap.R;

/**
 * Created by lyk on 27/5/17.
 */

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private ScrollView svChatZone;
    private ImageButton btnSend;
    private EditText etChat;
    private LinearLayout llChatZone;
    boolean testSelf = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        llChatZone = (LinearLayout) findViewById(R.id.ll_chat_zone);
        etChat = (EditText) findViewById(R.id.et_chat);
        svChatZone = (ScrollView) findViewById(R.id.sv_chat_zone);

        setupButtons();

    }

    private void setupButtons(){
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testSelf){
                    Log.d(TAG, "testself false");
                    testSelf = false;
                    RelativeLayout rlMsg = (RelativeLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.textbox_self, null);
                    TextView tvMsg = (TextView) rlMsg.findViewById(R.id.tv_msg);
                    tvMsg.setText(etChat.getText());
                    llChatZone.addView(rlMsg);
                    svChatZone.requestFocus();
                    svChatZone.fullScroll(View.FOCUS_DOWN);
                }
                else {
                    Log.d(TAG, "testself true");
                    testSelf = true;
                    RelativeLayout rlMsg = (RelativeLayout) LayoutInflater.from(ChatActivity.this).inflate(R.layout.textbox_other, null);
                    TextView tvMsg = (TextView) rlMsg.findViewById(R.id.tv_msg);
                    tvMsg.setText(etChat.getText());
                    llChatZone.addView(rlMsg);
                    svChatZone.requestFocus();
                    svChatZone.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }
}
