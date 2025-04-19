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
import com.grocery.groceryapp.activities.AddOrderReviewsActivity;
import com.grocery.groceryapp.adapters.UserOrderReviewsAdapter;
import com.grocery.groceryapp.databinding.FragmentUserOrderReviewsBinding;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import java.util.ArrayList;


public class UserOrderReviewsFragment extends Fragment {

    FragmentUserOrderReviewsBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference orderReviewsCol;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;
    UserOrderReviewsAdapter userOrderReviewsAdapter;


    public UserOrderReviewsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserOrderReviewsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Creating Order Reviews Collection
        String uid = auth.getCurrentUser().getUid();
        userDocRef = fireStore.collection("Users").document(uid);
        orderReviewsCol = userDocRef.collection("OrderReviews");


        toOrderReviewsModelArrayList = new ArrayList<>();
        binding.rvUserOrderReviews.setHasFixedSize(true);
        userOrderReviewsAdapter = new UserOrderReviewsAdapter(getContext(), toOrderReviewsModelArrayList);
        binding.rvUserOrderReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserOrderReviews.setAdapter(userOrderReviewsAdapter);
        CheckValidationAndThenAddUserReviewsProducts();





        return view;
    }


    private void AddUserOrderReviews() {

        orderReviewsCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }


                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.ADDED) {

                        toOrderReviewsModelArrayList.add(document.getDocument().toObject(ToOrderReviewsModel.class));

                    }

                    userOrderReviewsAdapter.notifyDataSetChanged();


                }




            }
        });


    }



    private void CheckValidationAndThenAddUserReviewsProducts() {

        Query query = orderReviewsCol.count().getQuery();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int count = value.getDocuments().size();

                if (count == 0) {
                    binding.txtError.setVisibility(View.VISIBLE);

                } else {
                    binding.txtError.setVisibility(View.GONE);
                    AddUserOrderReviews();
                }


            }
        });


    }





}