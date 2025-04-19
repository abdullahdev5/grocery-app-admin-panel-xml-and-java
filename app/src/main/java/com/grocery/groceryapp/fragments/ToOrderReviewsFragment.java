package com.grocery.groceryapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.grocery.groceryapp.adapters.BuyedProductsAdapter;
import com.grocery.groceryapp.adapters.ToOrderReviewsAdapter;
import com.grocery.groceryapp.databinding.FragmentToOrderReviewsBinding;
import com.grocery.groceryapp.models.BuyedProductsModel;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import java.util.ArrayList;


public class ToOrderReviewsFragment extends Fragment {

    FragmentToOrderReviewsBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;
    ToOrderReviewsAdapter toOrderReviewsAdapter;


    public ToOrderReviewsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentToOrderReviewsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        toOrderReviewsModelArrayList = new ArrayList<>();
        binding.rvToReviews.setHasFixedSize(true);
        binding.rvToReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        toOrderReviewsAdapter = new ToOrderReviewsAdapter(getContext(), toOrderReviewsModelArrayList);
        binding.rvToReviews.setAdapter(toOrderReviewsAdapter);
        CheckValidationAndThenAddToOrderReviewsProducts();




        return view;
    }


    private void CheckValidationAndThenAddToOrderReviewsProducts() {

        Query query = buyedProductsCol.whereEqualTo("orderStatus", getResources().getString(R.string.delivered));

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    int count = task.getResult().size();

                    if (count == 0) {
                        binding.txtError.setVisibility(View.VISIBLE);

                    } else {
                        binding.txtError.setVisibility(View.GONE);
                        AddToOrderReviewsProducts();
                    }


                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void AddToOrderReviewsProducts() {

        buyedProductsCol.whereEqualTo("orderStatus", getResources().getString(R.string.delivered))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                toOrderReviewsModelArrayList.add(document.getDocument().toObject(ToOrderReviewsModel.class));

                            }

                            toOrderReviewsAdapter.notifyDataSetChanged();


                        }



                    }
                });


    }





}