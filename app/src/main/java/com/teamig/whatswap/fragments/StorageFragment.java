package com.teamig.whatswap.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamig.whatswap.MainActivity;
import com.teamig.whatswap.R;
import com.teamig.whatswap.activities.NewItemActivity;
import com.teamig.whatswap.adapters.ItemAdapter;
import com.teamig.whatswap.models.Item;

import java.util.ArrayList;
import java.util.List;

public class StorageFragment extends Fragment {

    private static final String TAG = "StorageFragment";

    private OnFragmentInteractionListener mListener;
    private FloatingActionButton btnAddItem;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    private List<String> photoDownloadUrlPerItem;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mItemPhotoStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserItemDatabaseRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private ValueEventListener itemListListener;

    private List<Item> userItermList;

    public StorageFragment() {
        // Required empty public constructor
    }

    public static StorageFragment newInstance() {
        StorageFragment fragment = new StorageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_storage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        btnAddItem = (FloatingActionButton) view.findViewById(R.id.fab_add_item);
        setupButtons();
        setupFirebase();

        setupItemList();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mUserItemDatabaseRef.child(user.getUid()).removeEventListener(itemListListener);
    }

    private void setupItemList(){

        if (user != null){
            String uid = user.getUid();
            Log.d(TAG, "Reading User Item List from Real Time Database with uid : " + uid);

            // Load User's Item List Data
            mUserItemDatabaseRef.child(uid).addValueEventListener(itemListListener);

        } else {
            Log.w(TAG, "Reading User Item List ------- User not existing....");
        }
    }

    private void setupButtons(){
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewItemActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.RC_ITEM_SUBMIT_ACTIVITY);
            }
        });
    }

    private void setupFirebase(){

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mItemPhotoStorageRef = mFirebaseStorage.getReference().child("user_item_photos");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserItemDatabaseRef = mFirebaseDatabase.getReference().child("items");

        userItermList = new ArrayList<>();
        itemListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    userItermList.add(item);
                }
                itemAdapter = new ItemAdapter(getContext(), userItermList);
                recyclerView.setAdapter(itemAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Item List failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

    }

}
