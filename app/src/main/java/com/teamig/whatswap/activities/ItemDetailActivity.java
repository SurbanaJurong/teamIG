package com.teamig.whatswap.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamig.whatswap.R;
import com.teamig.whatswap.adapters.ImagePagerAdapter;
import com.teamig.whatswap.models.Chat;
import com.teamig.whatswap.models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;


public class ItemDetailActivity extends AppCompatActivity {

    private static final String TAG="ItemDetailActivity";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_item_photo);
        int[] imageUrls = new int[2];
        imageUrls[0] = R.drawable.cate_men;
        imageUrls[1] = R.drawable.cate_electro;
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imageUrls);
        viewPager.setAdapter(imagePagerAdapter);

        CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);

        Button btnChat = (Button) findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String key = mDatabase.child("chats").push().getKey();
//
//                Message message1 = new Message("hello", "id1");
//                Message message2 = new Message("hello!", "id2");
//                List<Message> messages = new ArrayList<>();
//                messages.add(message1);
//                messages.add(message2);
//                Map<String, Object> map = new HashMap<>();
//                map.put("buyerUid", "buyer");
//                map.put("sellerUid", "seller");
//                map.put("title", "title");
//                map.put("messages", messages);
//
//                Map<String, Object> childUpdate = new HashMap<>();
//                childUpdate.put("/chats/" + key, map);
//
//                mDatabase.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "successfully updated!");
//                    }
//                });
                Log.d(TAG, "lol");
                Message message1 = new Message("hello", "id1");
                Message message2 = new Message("hello!", "id2");

                List<Message> messages = new ArrayList<>();
                messages.add(message1);
                messages.add(message2);

                Chat chat = new Chat("id1", "id2", "item1", messages);
                String key = mDatabase.child("chats").push().getKey();
                mDatabase.child("chats").child(key).setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(ItemDetailActivity.this, ChatActivity.class);
                        ItemDetailActivity.this.startActivity(intent);
                    }
                });
                Log.d(TAG, "lol1");
            }
        });
    }
}
