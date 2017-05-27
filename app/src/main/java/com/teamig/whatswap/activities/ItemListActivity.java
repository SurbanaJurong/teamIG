package com.teamig.whatswap.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.Query;
import com.teamig.whatswap.R;
import com.teamig.whatswap.adapters.ItemAdapter;
import com.teamig.whatswap.models.Item;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    Button testButton;
    private Intent intent;

    private FirebaseStorage mFirebaseStorage;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAllCollectDatabaseRef;
    private FirebaseAuth mFirebaseAuth;

    private RecyclerView recyclerView;

    private List<Item> cateItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        intent = getIntent();
        String category = intent.getStringExtra("category");
        Log.d(TAG, "onCreate, listing category: " + category);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        setupFirebase(category);

        testButton = (Button) findViewById(R.id.btn_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
                ItemListActivity.this.startActivity(intent);
            }
        });


    }

    private void setupFirebase(String category) {

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAllCollectDatabaseRef = mFirebaseDatabase.getReference().child("all_collect");

        cateItemList = new ArrayList<>();
        Query myTopPostsQuery = mAllCollectDatabaseRef.orderByChild("category").equalTo(category);
        Log.d(TAG, "Sending Query to Server side");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    cateItemList.add(item);
                }
                ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(), cateItemList);
                recyclerView.setAdapter(itemAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
