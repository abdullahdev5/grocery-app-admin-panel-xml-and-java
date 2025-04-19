package com.grocery.groceryapp.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.grocery.groceryapp.databinding.FragmentAllOrdersBinding;
import com.grocery.groceryapp.models.BuyedProductsModel;

import java.util.ArrayList;


public class AllOrdersFragment extends Fragment {

    FragmentAllOrdersBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol;
    ArrayList<BuyedProductsModel> buyedProductsModelArrayList;
    BuyedProductsAdapter buyedProductsAdapter;


    public AllOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAllOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        buyedProductsModelArrayList = new ArrayList<>();
        binding.rvAllOrders.setHasFixedSize(true);
        binding.rvAllOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        buyedProductsAdapter = new BuyedProductsAdapter(getContext(), buyedProductsModelArrayList);
        binding.rvAllOrders.setAdapter(buyedProductsAdapter);
        AddAllOrderProducts();








        return view;
    }


    private void AddAllOrderProducts() {

        buyedProductsCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
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