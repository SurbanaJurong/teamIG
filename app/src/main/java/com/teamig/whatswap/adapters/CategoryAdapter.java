package com.teamig.whatswap.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamig.whatswap.R;
import com.teamig.whatswap.activities.ItemDetailActivity;
import com.teamig.whatswap.activities.ItemListActivity;
import com.teamig.whatswap.models.Category;

import java.util.List;


class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView categoryName;
    ImageView categoryImage;
    CategoryAdapter categoryAdapter;
    Context context;

    CategoryViewHolder(View itemView, Context context, CategoryAdapter categoryAdapter) {
        super(itemView);
        this.context = context;
        this.categoryAdapter = categoryAdapter;
        itemView.setOnClickListener(this);
        categoryName = (TextView) itemView.findViewById(R.id.name);
        categoryImage = (ImageView) itemView.findViewById(R.id.photo);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        Category selected = categoryAdapter.getItemList().get(getAdapterPosition());
        Intent intent = new Intent(context, ItemListActivity.class);
        intent.putExtra("category", selected.getName());
        context.startActivity(intent);
    }
}


public class CategoryAdapter extends RecyclerView.Adapter {
    private List<Category> itemList;
    private Context context;

    public CategoryAdapter(Context context, List<Category> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, null);
        CategoryViewHolder rcv = new CategoryViewHolder(layoutView, context, this);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CategoryViewHolder) holder).categoryName.setText(itemList.get(position).getName());
        ((CategoryViewHolder) holder).categoryImage.setImageResource(itemList.get(position).getImageId());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public List<Category> getItemList() {
        return itemList;
    }
}
