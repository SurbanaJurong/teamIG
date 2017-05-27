package com.teamig.whatswap.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teamig.whatswap.R;

/**
 * Created by lyk on 26/5/17.
 */

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private int[] imageUrls;

    public ImagePagerAdapter(Context context, int[] imageUrls) {
        mContext = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        int imageUrl = imageUrls[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ImageView imageView = (ImageView) inflater.inflate(R.layout.iv_photo_pager, collection, false);
        //TODO: temp code
        imageView.setImageResource(imageUrl);
        // ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.iv_photo_pager, collection, false);
        collection.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}