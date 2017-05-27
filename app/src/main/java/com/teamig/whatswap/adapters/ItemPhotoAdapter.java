package com.teamig.whatswap.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.teamig.whatswap.R;
import com.teamig.whatswap.dialogs.ViewItemPhotoDialog;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by lyk on 26/5/17.
 */

public class ItemPhotoAdapter extends RecyclerView.Adapter {
    private static String TAG = "IncidentUploadPhotoAdapter";

    private ArrayList<Uri> dataSet;
    private Context context;

    public ItemPhotoAdapter(Context context){
        this.context = context;
        dataSet = new ArrayList<>();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View photoItem = inflater.inflate(R.layout.item_upload_photo, parent, false);
        return new CustomViewHolder(photoItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((CustomViewHolder)holder).imageView.setImageURI(dataSet.get(position));

        ((CustomViewHolder)holder).imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URI jUri = new URI(dataSet.get(position).toString());
                    File file = new File(jUri);
                    if (file.exists()){
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataSet.remove(position);
                ItemPhotoAdapter.this.notifyItemRemoved(position);
                ItemPhotoAdapter.this.notifyItemRangeChanged(position, dataSet.size() - position);

            }
        });

        ((CustomViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewItemPhotoDialog(v.getContext(),dataSet.get(position)).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataSet ? dataSet.size():0);
    }

    public void addItem(Uri uri){
        dataSet.add(uri);
        notifyItemInserted(dataSet.size()-1);
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private ImageButton imageButton;

        CustomViewHolder(View itemView) {
            super(itemView);
            imageView =(ImageView) itemView.findViewById(R.id.iv_item_upload_photo);
            imageButton =(ImageButton) itemView.findViewById(R.id.ib_item_upload_photo);
        }

    }

    public ArrayList<Uri> getDataSet() {
        return dataSet;
    }
}
