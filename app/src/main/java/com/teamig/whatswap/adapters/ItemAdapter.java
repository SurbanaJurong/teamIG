package com.teamig.whatswap.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.teamig.whatswap.R;
import com.teamig.whatswap.activities.ItemDetailActivity;
import com.teamig.whatswap.models.Item;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by lyk on 26/5/17.
 */

class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView itemName;
    ImageView itemImage;
    Context context;

    ItemViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        itemView.setOnClickListener(this);
        itemName = (TextView) itemView.findViewById(R.id.tv_item_name);
        itemImage = (ImageView) itemView.findViewById(R.id.iv_item_photo);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ItemDetailActivity.class);
        context.startActivity(intent);
    }
}

public class ItemAdapter extends RecyclerView.Adapter {
    private List<Item> itemList;
    private Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.itemList = itemList;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, null);
        ItemViewHolder rcv = new ItemViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).itemName.setText(itemList.get(position).getName());
        Picasso.with(context).load(itemList.get(position).getPhotoUris().get(0)).into(((ItemViewHolder) holder).itemImage);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
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
