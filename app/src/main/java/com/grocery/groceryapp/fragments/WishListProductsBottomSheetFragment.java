package com.grocery.groceryapp.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.adapters.WishListProductsAdapter;
import com.grocery.groceryapp.databinding.FragmentWishListProductsBottomSheetBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ProductsModel;
import com.grocery.groceryapp.models.WishListProductsModel;

import java.util.ArrayList;


public class WishListProductsBottomSheetFragment extends BottomSheetDialogFragment {

    FragmentWishListProductsBottomSheetBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference wishListProductsCol;
    ArrayList<WishListProductsModel> wishListProductsModelArrayList;
    WishListProductsAdapter wishListProductsAdapter;

    public WishListProductsBottomSheetFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentWishListProductsBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String uid = auth.getCurrentUser().getUid();
        userDocRef = fireStore.collection("Users").document(uid);

        // Wishlist Products
        wishListProductsCol = userDocRef.collection("WishListProducts");


        wishListProductsModelArrayList = new ArrayList<>();
        binding.rvWishListProducts.setHasFixedSize(true);
        binding.rvWishListProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        wishListProductsAdapter = new WishListProductsAdapter(getContext(), wishListProductsModelArrayList);
        binding.rvWishListProducts.setAdapter(wishListProductsAdapter);

        AddWishListProducts();

        RemoveProductFromWishList();




        return view;
    }


    private void AddWishListProducts() {

        wishListProductsCol
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                wishListProductsModelArrayList.add(document.getDocument().toObject(WishListProductsModel.class));

                            }

                            wishListProductsAdapter.notifyDataSetChanged();


                        }

                    }
                });



    }

    private void RemoveProductFromWishList() {

        wishListProductsCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error != null) {

                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                }

                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.REMOVED) {

                        String removedDocId = document.getDocument().getId();
                        int indexOfRemovedItem = -1;
                        for (int i = 0; i < wishListProductsModelArrayList.size(); i++) {

                            WishListProductsModel item = wishListProductsModelArrayList.get(i);
                            if (item.getKey().equals(removedDocId)) {
                                indexOfRemovedItem = i;
                                break;
                            }

                        }

                        if (indexOfRemovedItem != -1) {
                            wishListProductsModelArrayList.remove(indexOfRemovedItem);
                            wishListProductsModelArrayList.remove(document.getDocument().toObject(WishListProductsModel.class));
                        }

                    }

                    wishListProductsAdapter.notifyDataSetChanged();

                }


            }
        });

    }



}