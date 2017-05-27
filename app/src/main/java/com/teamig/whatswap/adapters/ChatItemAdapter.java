package com.teamig.whatswap.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.teamig.whatswap.R;
import com.teamig.whatswap.activities.ChatActivity;
import com.teamig.whatswap.models.Chat;

import java.util.List;

/**
 * Created by lyk on 27/5/17.
 */

class ChatItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView itemName;
    TextView itemAbbrev;
    Context context;

    ChatItemViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        itemView.setOnClickListener(this);
        itemName = (TextView) itemView.findViewById(R.id.tv_item_name);
        itemAbbrev = (TextView) itemView.findViewById(R.id.tv_item_abbrev);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ChatActivity.class);
        context.startActivity(intent);
    }
}

public class ChatItemAdapter extends RecyclerView.Adapter {
    private List<Chat> chatList;
    private Context context;

    public ChatItemAdapter(Context context, List<Chat> chatList) {
        this.chatList = chatList;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null);
        ChatItemViewHolder rcv = new ChatItemViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ChatItemViewHolder) holder).itemName.setText(chatList.get(position).getTitle());
        ((ChatItemViewHolder) holder).itemAbbrev.setText(chatList.get(position).getTitle().substring(0,1).toUpperCase());
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
