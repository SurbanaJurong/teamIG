package com.teamig.whatswap.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.teamig.whatswap.MainActivity;
import com.teamig.whatswap.R;
import com.teamig.whatswap.activities.UserProfileUpdateActivity;
import com.teamig.whatswap.utils.ImageUtil;
import com.teamig.whatswap.utils.PermissionUtil;
import com.teamig.whatswap.utils.PrefUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    private static final int REQUEST_TAKE_PHOTO = 111;
    private static final int REQUEST_PICK_IMAGE = 222;

    private CircleImageView ivProfile;
    private Button btnSignOut, btnUpdateProfile;
    private TextView tvUsername, tvCommunity;
    private Uri outputUri;
    private String outputPath;
    private boolean uploadSuccessful = false;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnSignOut = (Button) view.findViewById(R.id.settings_btn_logout);
        btnUpdateProfile = (Button) view.findViewById(R.id.settings_btn_update_profile);
        ivProfile = (CircleImageView) view.findViewById(R.id.iv_profile_pic);
        tvUsername = (TextView) view.findViewById(R.id.tv_username);
        tvUsername.setText(PrefUtil.getStringPreference(PrefUtil.USERNAME, getContext()));
        tvCommunity = (TextView) view.findViewById(R.id.tv_community);
        tvCommunity.setText(PrefUtil.getStringPreference(PrefUtil.COMMUNITY, getContext()));
        String url = PrefUtil.getStringPreference(PrefUtil.URL, getContext());
        Log.d(TAG, "trying to get profile pic from " + url);
        Picasso.with(getContext()).load(url).into(ivProfile);

        setupButtons();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.d("RESULT", String.valueOf(requestCode) + " " + resultCode);
        if(requestCode == REQUEST_PICK_IMAGE){
            if(imageReturnedIntent!=null)
                outputUri = imageReturnedIntent.getData();
        }
        if ((requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_IMAGE)&& resultCode == Activity.RESULT_OK) {
            try {
                if(requestCode == REQUEST_TAKE_PHOTO)
                    ImageUtil.rotatePhoto(outputUri, this.getContext(), outputPath);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                StorageReference mUserProfileImageStorageRef = FirebaseStorage.getInstance().getReference().child("user_profile_pictures");
                StorageReference tempPhotoRef = mUserProfileImageStorageRef.child(uid + ".jpg");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), outputUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();

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
                        String url = taskSnapshot.getMetadata().getDownloadUrl().toString();
                        PrefUtil.setStringPreference(PrefUtil.URL, url, getContext());
                        Picasso.with(getContext()).load(PrefUtil.getStringPreference(PrefUtil.URL, getContext())).into(ivProfile);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(uploadSuccessful) {
                Picasso.with(getContext()).load(PrefUtil.getStringPreference(PrefUtil.URL, getContext())).into(ivProfile);
            }
        }
    }

    public void setupButtons(){
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getActivity());
                PrefUtil.clearPreference(getContext());
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog photoSourceDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
            Log.d(TAG, "Setting Fragment Button clicked");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }


    private void takePicture() {
        try {


            if(!PermissionUtil.checkPermissionForCamera(this.getActivity())){
                PermissionUtil.requestPermissionForCamera(this.getActivity());
            }
            else {
                if(!PermissionUtil.checkPermissionForExternalStorage(this.getActivity())){
                    PermissionUtil.requestPermissionForExternalStorage(this.getActivity());
                }
                else{
                    File tempPhotoFile = ImageUtil.createTemporaryFile("item_photo", ".jpg");
                    outputPath = tempPhotoFile.getAbsolutePath();
                    outputUri = FileProvider.getUriForFile(this.getContext(), this.getActivity().getApplicationContext().getPackageName() + ".provider", tempPhotoFile);
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                    startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);
                    Toast.makeText(this.getContext(), "haha", Toast.LENGTH_SHORT).show();
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
}
