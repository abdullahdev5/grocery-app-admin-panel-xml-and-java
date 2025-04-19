package com.grocery.groceryadmin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryadmin.R;
import com.grocery.groceryadmin.adapter.OrdersAdapter;
import com.grocery.groceryadmin.databinding.FragmentOrdersBinding;
import com.grocery.groceryadmin.models.OrdersModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class OrdersFragment extends Fragment {

    FragmentOrdersBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol, usersCol;
    ArrayList<OrdersModel> ordersModelArrayList;
    OrdersAdapter ordersAdapter;


    public OrdersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        usersCol= fireStore.collection("Users");

        ordersModelArrayList = new ArrayList<>();
        binding.rvOrders.setHasFixedSize(true);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersAdapter = new OrdersAdapter(getContext(), ordersModelArrayList);
        binding.rvOrders.setAdapter(ordersAdapter);
        AddOrdersData();



        return view;
    }


    private void AddOrdersData() {

       Query query = usersCol;

       query.addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               
               if (error != null) {
                   Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                   Log.d("TAG", "onEvent: Error");
               }

               for (DocumentSnapshot document : value.getDocuments()) {

                   String uid = document.getId();

                   buyedProductsCol = document.getReference().collection("BuyedProducts");

                   buyedProductsCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {

                           if (task.isSuccessful()) {
                               for (DocumentSnapshot orderDocument : task.getResult().getDocuments()) {
                                   OrdersModel order = orderDocument.toObject(OrdersModel.class);
                                   if (order != null) { // Check for successful conversion
                                       ordersModelArrayList.add(order);
                                       Log.d("TAG", "onEvent: Success");
                                   } else {
                                       Log.w("TAG", "Failed to convert document to OrdersModel");
                                   }
                               }
                               ordersAdapter.notifyDataSetChanged(); // Notify adapter of data change
                           } else {
                               Log.w("TAG", "Error retrieving orders subcollection", task.getException());
                           }

                       }
                   });

               }

               
           }
       });


    }




}