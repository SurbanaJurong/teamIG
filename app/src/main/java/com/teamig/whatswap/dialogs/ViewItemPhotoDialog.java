package com.teamig.whatswap.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.Window;
import android.widget.ImageView;

import com.teamig.whatswap.R;

/**
 * Created by lyk on 26/5/17.
 */

public class ViewItemPhotoDialog extends Dialog {

    public ViewItemPhotoDialog (Context context, Uri uri){
        super(context);
        //force gc
        System.gc();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_view_item_photo);

        ImageView imageView = (ImageView) findViewById(R.id.iv_item_photo);
        imageView.setImageURI(uri);
    }
}
