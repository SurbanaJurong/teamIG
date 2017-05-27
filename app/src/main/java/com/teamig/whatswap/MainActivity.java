package com.teamig.whatswap;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.ui.User;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teamig.whatswap.activities.UserProfileUpdateActivity;
import com.teamig.whatswap.fragments.ChatsFragment;
import com.teamig.whatswap.fragments.MainFragment;
import com.teamig.whatswap.fragments.NearbyFragment;
import com.teamig.whatswap.fragments.OnFragmentInteractionListener;
import com.teamig.whatswap.fragments.SettingsFragment;
import com.teamig.whatswap.fragments.StorageFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.teamig.whatswap.models.UserProfile;
import com.teamig.whatswap.utils.PrefUtil;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;

    private MainFragment mainFragment;
    private StorageFragment storageFragment;
    private SettingsFragment settingsFragment;
    private NearbyFragment nearbyFragment;
    private ChatsFragment chatsFragment;

    // activity tracking constants
    public static final int RC_SIGN_IN = 123;
    public static final String USER_PROFILE_DATABASE = "users";
    public static final int RC_PROFILE_UPDATE_ACTIVITY = 124;
    public static final int RC_ITEM_SUBMIT_ACTIVITY = 125;

    // Firebase Variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStageListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserProfileDatabaseRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserProfileImageStorageRef;
    private Uri profilePicUri;
    private FirebaseUser user;

    private UserProfile userProfile;


    private String userProfileUploadStatus;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return switchFragment(item.getItemId());
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setupFragments();

        // Initialize Firebase components
        Log.d(TAG, "======================== Calling InitFireBase() from MainActivity onCreate");
        initFireBase();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setupFragments(){
        fragmentManager = getSupportFragmentManager();
        mainFragment = MainFragment.newInstance();
        storageFragment = StorageFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();
        nearbyFragment = NearbyFragment.newInstance();
        chatsFragment = ChatsFragment.newInstance();
        switchFragment(R.id.navigation_home);
    }

    private boolean switchFragment(int id){
        switch(id) {
            case R.id.navigation_home:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, mainFragment);
                fragmentTransaction.commit();
                return true;
            case R.id.navigation_storage:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, storageFragment);
                fragmentTransaction.commit();
                return true;
            case R.id.navigation_settings:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, settingsFragment);
                fragmentTransaction.commit();
                return true;
            case R.id.navigation_nearby:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, nearbyFragment);
                fragmentTransaction.commit();
                return true;
            case R.id.navigation_chats:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, chatsFragment);
                fragmentTransaction.commit();
                return true;
            default:
                return false;
        }
    }

    private void initFireBase() {

        Log.d(TAG, "==================== InitFireBase Called");

        // Keep Tracking of user Profile uploading process
        userProfileUploadStatus = "negative";

        // Initialize Firebase Real Time Database Set
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserProfileDatabaseRef = mFirebaseDatabase.getReference().child(USER_PROFILE_DATABASE);

        // Initialize Firebase Storage Set
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserProfileImageStorageRef = mFirebaseStorage.getReference().child("user_profile_pictures");

        // Initialize Firebase Authentication components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStageListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user signed in or not
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User already signed in
                    Log.d(TAG, "on AuthStateChanged : == User Already Signed in with ID: " + user.getUid());
                    Log.d(TAG, "on AuthStageChanged : == Calling checkUserProfile()");
                    checkUserProfile();
                } else {
                    // User is signed out, Start Sign in Activity
                    Log.d(TAG, "on AuthStateChanged : == User not detected, starting sign in activity");
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                                    ).build(), RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStageListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "============ MainaActivity onActivityResult : RequestCode: " + requestCode);
        // MainActivity is Back
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Logged In. Welcome!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed In Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_PROFILE_UPDATE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Back from Profile Update Activity, Calling uploadUserProfile()");
                uploadUserProfile(data);
            }
        } else if (requestCode == RC_ITEM_SUBMIT_ACTIVITY) {
            Log.d(TAG, "Re-Opening Storage Fragment");
            setupFragments();
            switchFragment(R.id.navigation_storage);
        }
    }

    private void uploadUserProfile(final Intent data) {

        // Assume User logged in.
        //final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            userProfileUploadStatus = "inProcess";
            String uid = user.getUid();
            mUserProfileImageStorageRef.child(uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override
                public void onSuccess(Uri uri) {
                    // uploading user profile data to Firebase Real time database
                    String username = data.getStringExtra("username");
                    String age = data.getStringExtra("age");
                    String gender = data.getStringExtra("gender");
                    String community = data.getStringExtra("community");
                    if(userProfile == null)
                        userProfile = new UserProfile();
                    userProfile.setUsername(username);
                    userProfile.setAge(age);
                    userProfile.setGender(gender);
                    userProfile.setCommunity(community);
                    mUserProfileDatabaseRef.child(user.getUid()).setValue(userProfile);
                    Toast.makeText(MainActivity.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "uploadUserProfile --- got Download URI");

                    PrefUtil.setStringPreference(PrefUtil.USERNAME, userProfile.getUsername(), getApplicationContext());
                    PrefUtil.setStringPreference(PrefUtil.AGE, userProfile.getAge(), getApplicationContext());
                    PrefUtil.setStringPreference(PrefUtil.GENDER, userProfile.getGender(), getApplicationContext());
                    PrefUtil.setStringPreference(PrefUtil.COMMUNITY, userProfile.getCommunity(), getApplicationContext());
                    Log.d(TAG, "setting url pref to be " + userProfile.getProfilePhotoUrl());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void checkUserProfile() {

        // Check the uploading status first
        if (userProfileUploadStatus.equals("inProcess")) {
            Toast.makeText(this, "Creating Profile Data on Server...", Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("Dummy Thread", "========== Waiting for 3 second");
                        Thread.sleep(3000);
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        MainActivity.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
        // Check if user profile on server or not
        // Assume user is already logged in
        //FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            final String uid = user.getUid();
            Log.d(TAG, "uid is : "+uid);
            // Check if user's profile data is stored on Server
            mUserProfileDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "datasnapshot key: "+dataSnapshot.getKey());
                    Log.d(TAG, "datasnapshot: "+dataSnapshot.toString());
                    if (dataSnapshot.getValue() == null) {
                        //if (dataSnapshot.getValue(UserProfile.class) == null) {

                        // User profile not found on server, Create a temp profile image for new user
                        createTempUserProfilePhoto(uid);

                    } else {
                        Log.d(TAG, "checkUserProfile ------- User Profile Data found at Server.");
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        PrefUtil.setStringPreference(PrefUtil.URL, userProfile.getProfilePhotoUrl(), getApplicationContext());
                        PrefUtil.setStringPreference(PrefUtil.USERNAME, userProfile.getUsername(), getApplicationContext());
                        PrefUtil.setStringPreference(PrefUtil.AGE, userProfile.getAge(), getApplicationContext());
                        PrefUtil.setStringPreference(PrefUtil.GENDER, userProfile.getGender(), getApplicationContext());
                        PrefUtil.setStringPreference(PrefUtil.COMMUNITY, userProfile.getCommunity(), getApplicationContext());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "checkUserProfile ------- Check User Data -- Failed ");
                }
            });

        } else {
            Log.w(TAG, "checkUserProfile ------- User not existing....");
            initFireBase();
        }
    }
    private void createTempUserProfilePhoto(String uid) {
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        StorageReference tempPhotoRef = mUserProfileImageStorageRef.child(uid + ".jpg");

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.default_profile_pic);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();

        UploadTask uploadTask = tempPhotoRef.putBytes(bitMapData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "createTempUserProfilePhoto ---- Failed to upload image");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                if (downloadUri != null) {
                    Log.d(TAG, "createTempUserProfilePhoto --- Created, download URI: " + downloadUri.toString());
                    // Start new activity to ask User update profile
                    Intent intent = new Intent(MainActivity.this, UserProfileUpdateActivity.class);
                    startActivityForResult(intent, RC_PROFILE_UPDATE_ACTIVITY);
                    if(userProfile == null)
                        userProfile = new UserProfile();

                    userProfile.setProfilePhotoUrl(downloadUri.toString());

                    PrefUtil.setStringPreference(PrefUtil.URL, userProfile.getProfilePhotoUrl(), getApplicationContext());
                }
            }
        });

    }
}
