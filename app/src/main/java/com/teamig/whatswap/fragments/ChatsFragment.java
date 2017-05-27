package com.teamig.whatswap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamig.whatswap.R;
import com.teamig.whatswap.activities.ChatActivity;
import com.teamig.whatswap.adapters.ChatItemAdapter;
import com.teamig.whatswap.models.Chat;
import com.teamig.whatswap.models.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyk on 27/5/17.
 */

public class ChatsFragment extends Fragment {

    private RecyclerView rvChats;
    private ChatItemAdapter chatItemAdapter;
    private List<Chat> chatList;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("chats");

    public ChatsFragment(){

    }

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        rvChats = (RecyclerView) view.findViewById(R.id.rv_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(linearLayoutManager);
        chatList = new ArrayList<>();
        chatItemAdapter = new ChatItemAdapter(getContext(), chatList);
        rvChats.setAdapter(chatItemAdapter);


        Button btnTest = (Button) view.findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                getActivity().startActivity(intent);
            }
        });

        getDataFromServer();
        return view;
    }


    private void getDataFromServer(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    Chat chat = new Chat();
                    DataSnapshot data = (DataSnapshot)i.next();
                    chat.setTitle(data.child("title").getValue().toString());
                    if (data.child("buyerUid").getValue() != null)
                        chat.setBuyerUid(data.child("buyerUid").getValue().toString());
                    if(data.child("sellerUid").getValue() != null)
                        chat.setSellerUid(data.child("sellerUid").getValue().toString());
                    if(data.child("messages").getValue() != null)
                        chat.setMessages((List<Message> ) data.child("messages").getValue());
                    chatList.add(chat);
                }
                chatItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
