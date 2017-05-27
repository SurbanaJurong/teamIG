package com.teamig.whatswap.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.teamig.whatswap.R;
import com.teamig.whatswap.adapters.ItemPhotoAdapter;
import com.teamig.whatswap.models.Item;
import com.teamig.whatswap.utils.ImageUtil;
import com.teamig.whatswap.utils.PermissionUtil;
import com.teamig.whatswap.utils.PrefUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewItemActivity extends AppCompatActivity {

    private static final String TAG = "NewItemActivity";

    private static final int REQUEST_TAKE_PHOTO = 111;
    private static final int REQUEST_PICK_IMAGE = 222;
    private RecyclerView photoRecyclerView;
    private Uri outputUri;
    private String outputPath;
    private ItemPhotoAdapter itemPhotoAdapter;
    private Spinner spCategory, spTrade, spDelivery;
    private EditText etItemName, etDesc, etPrice;

    private List<String> photoDownloadUrlPerUpload;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUploadItemPhotoStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserItemDatabaseRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private DatabaseReference mRootReference;
    private DatabaseReference mAllCollectDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        setUpButtons();
        setUpRecyclerView();
        setupSpinners();
        setUpEditView();
        setUpFirebase();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.d("RESULT", String.valueOf(requestCode) + " " + resultCode);
        if(requestCode == REQUEST_PICK_IMAGE){
            if(imageReturnedIntent!=null)
                outputUri = imageReturnedIntent.getData();
        }
        if ((requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_IMAGE)&& resultCode == RESULT_OK) {
            try {
                if(requestCode == REQUEST_TAKE_PHOTO)
                    ImageUtil.rotatePhoto(outputUri, this, outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            itemPhotoAdapter.addItem(outputUri);
            photoRecyclerView.scrollToPosition(0);
        }
    }

    private void setUpFirebase(){

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mUploadItemPhotoStorageRef = mFirebaseStorage.getReference().child("user_item_photos");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootReference = mFirebaseDatabase.getReference();
        mUserItemDatabaseRef = mRootReference.child("items");
        mAllCollectDatabaseRef = mRootReference.child("all_collect");


    }

    private void setUpRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        photoRecyclerView = (RecyclerView) findViewById(R.id.rv_photo);


        itemPhotoAdapter = new ItemPhotoAdapter(this);

        photoRecyclerView.setLayoutManager(layoutManager);

        photoRecyclerView.setAdapter(itemPhotoAdapter);

    }

    private void setUpEditView() {
        etItemName = (EditText) findViewById(R.id.et_item_name);
        etDesc = (EditText) findViewById(R.id.et_item_desc);
        etPrice = (EditText) findViewById(R.id.et_item_price);
    }

    private void setUpButtons() {
        ImageButton addPhotoBtn;
        Button btnSubmit, btnCancel;
        addPhotoBtn = (ImageButton) findViewById(R.id.btn_add_photo);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        if (addPhotoBtn != null) {
            addPhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog photoSourceDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewItemActivity.this);
                    builder.setTitle("Select Source of Image");
                    final CharSequence[] actions = {"Camera", " Photo Gallery"};
                    builder.setItems(actions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    takePicture();
                                    break;
                                case 1:
                                    openGallery();
                                    break;
                            }
                            dialog.dismiss();
                        }
                    });
                    photoSourceDialog = builder.create();
                    photoSourceDialog.show();
                }
            });
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear PhotoDownloadUriPerUpload Buffer
                photoDownloadUrlPerUpload = new ArrayList<>();

                Log.d(TAG, "button submit clicked, uploading images to server");
                List<Uri> photoUris =  itemPhotoAdapter.getDataSet();
                for (int i = 0; i < photoUris.size(); i++) {
                    uploadPhotoToStorage(photoUris.get(i), i, photoUris.size());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewItemActivity.this.finish();
            }
        });
    }

    private void setupSpinners(){
        spCategory = (Spinner) findViewById(R.id.sp_category);
        spTrade = (Spinner) findViewById(R.id.sp_trad_mod);
        spDelivery = (Spinner) findViewById(R.id.sp_deli_mod);

        ArrayAdapter<CharSequence> adapterCate = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterCate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spCategory.setAdapter(adapterCate);

        ArrayAdapter<CharSequence> adapterTrade = ArrayAdapter.createFromResource(this,
                R.array.trading_mod_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spTrade.setAdapter(adapterTrade);

        ArrayAdapter<CharSequence> adapterDeli = ArrayAdapter.createFromResource(this,
                R.array.delivery_mod_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterDeli.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spDelivery.setAdapter(adapterDeli);
    }

    private void takePicture() {
        try {


            if(!PermissionUtil.checkPermissionForCamera(this)){
                PermissionUtil.requestPermissionForCamera(this);
            }
            else {
                if(!PermissionUtil.checkPermissionForExternalStorage(this)){
                    PermissionUtil.requestPermissionForExternalStorage(this);
                }
                else{
                    File tempPhotoFile = ImageUtil.createTemporaryFile("item_photo", ".jpg");
                    outputPath = tempPhotoFile.getAbsolutePath();
                    outputUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", tempPhotoFile);
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                    startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);
                    Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    private void uploadPhotoToStorage(Uri localUri, int i, final int total_num) {

        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), localUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitMapData = stream.toByteArray();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String temp_name = user.getUid() + "_" + sdf.format(new Date()) + "_" + i + ".jpg";
            StorageReference tempPhotoRef = mUploadItemPhotoStorageRef.child(temp_name);

            UploadTask uploadTask = tempPhotoRef.putBytes(bitMapData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e(TAG, "upload new profile photo ---- Failed to upload image");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri url = taskSnapshot.getMetadata().getDownloadUrl();
                    if (url != null){
                        photoDownloadUrlPerUpload.add(url.toString());
                        Log.d(TAG, "One Image Uploaded to Storage, URL returned.");

                        // Check if all photos have been uploaded
                        if (photoDownloadUrlPerUpload.size() == total_num) {


                            String itemName = etItemName.getText().toString();
                            String itemDesc = etDesc.getText().toString();
                            String itemPrice = etPrice.getText().toString();
                            String itemCate = spCategory.getSelectedItem().toString();
                            String itemTrad = spTrade.getSelectedItem().toString();
                            String itemDeli = spDelivery.getSelectedItem().toString();

                            String key1 = mUserItemDatabaseRef.push().getKey();
                            String key2 = mAllCollectDatabaseRef.push().getKey();

                            Item item = new Item(user.getUid(), itemName, itemDesc, itemPrice, itemCate, itemTrad, itemDeli, photoDownloadUrlPerUpload);
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/all_collect/" + key2, item);
                            childUpdates.put("/items/" + user.getUid() + "/" + key1, item);

                            mRootReference.updateChildren(childUpdates);

                            Log.d(TAG, "New Item Successfully Uploaded To Firebase Server");
                            finish();
                        }
                    } else {
                        Log.d(TAG, "!!!!!!! Successfully uploaded, but URL not returned");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Failed to Upload Image to Storage Server");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
