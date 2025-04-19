package com.grocery.groceryapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
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
import com.grocery.groceryapp.activities.BuyedProductsActivity;
import com.grocery.groceryapp.adapters.BuyedProductsAdapter;
import com.grocery.groceryapp.databinding.FragmentToShippedBinding;
import com.grocery.groceryapp.models.BuyedProductsModel;

import java.util.ArrayList;


public class ToShippedFragment extends Fragment {

   FragmentToShippedBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol;
    ArrayList<BuyedProductsModel> buyedProductsModelArrayList;
    BuyedProductsAdapter buyedProductsAdapter;

    public ToShippedFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentToShippedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        buyedProductsModelArrayList = new ArrayList<>();
        binding.rvToShipped.setHasFixedSize(true);
        binding.rvToShipped.setLayoutManager(new LinearLayoutManager(getContext()));
        buyedProductsAdapter = new BuyedProductsAdapter(getContext(), buyedProductsModelArrayList);
        binding.rvToShipped.setAdapter(buyedProductsAdapter);
        CheckValidationAndThenAddToShippedProducts();




        return  view;
    }


    private void CheckValidationAndThenAddToShippedProducts() {

        Query query = buyedProductsCol.whereEqualTo("orderStatus", getResources().getString(R.string.shipped));

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    int count = task.getResult().size();

                    if (count == 0) {
                        binding.txtError.setVisibility(View.VISIBLE);

                    } else {
                        binding.txtError.setVisibility(View.GONE);
                        AddToShippedProducts();
                    }


                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    private void AddToShippedProducts() {

        buyedProductsCol.whereEqualTo("orderStatus", getResources().getString(R.string.shipped))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                buyedProductsModelArrayList.add(document.getDocument().toObject(BuyedProductsModel.class));

                            }

                            buyedProductsAdapter.notifyDataSetChanged();


                        }



                    }
                });


    }




}