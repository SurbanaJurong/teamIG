package com.teamig.whatswap.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamig.whatswap.R;
import com.teamig.whatswap.adapters.CategoryAdapter;
import com.teamig.whatswap.models.Category;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamig.whatswap.MainActivity;
import com.teamig.whatswap.activities.NewItemActivity;
import com.teamig.whatswap.adapters.ItemAdapter;
import com.teamig.whatswap.models.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mItemPhotoStorageRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAllCollectDatabaseRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private ValueEventListener itemListListener;
    private List<Item> userItermList;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_main);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        List<Category> categories = setupCategoryList();
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categories);
        recyclerView.setAdapter(categoryAdapter);

        setupFirebase();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    private List<Category> setupCategoryList(){
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(R.drawable.cate_women, "Women's Fashion"));
        categories.add(new Category(R.drawable.cate_men, "Men's Fashion"));
        categories.add(new Category(R.drawable.cate_electro, "Electronic Items"));
        categories.add(new Category(R.drawable.cate_furniture, "Furniture & Home Appliances"));
        categories.add(new Category(R.drawable.cate_luxury, "Luxury"));
        categories.add(new Category(R.drawable.cate_books, "Books & Stationery"));
        categories.add(new Category(R.drawable.cate_cars, "Cars & AutoBikes"));
        categories.add(new Category(R.drawable.cate_sports, "Sports"));
        categories.add(new Category(R.drawable.cate_kids, "Baby & Kids"));
        categories.add(new Category(R.drawable.cate_health, "Health & Beauty"));
        categories.add(new Category(R.drawable.cate_others, "Other Items"));
        return categories;
    }

    private void setupFirebase() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mItemPhotoStorageRef = mFirebaseStorage.getReference().child("user_item_photos");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAllCollectDatabaseRef = mFirebaseDatabase.getReference().child("all_collect");

        userItermList = new ArrayList<>();
        itemListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    userItermList.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Item List failed, log a message
                Log.w(TAG, "load Data :onCancelled", databaseError.toException());
                // ...
            }
        };
    }

    private List<Item> loadAndFilterItems() {

        return null;
    }
}
