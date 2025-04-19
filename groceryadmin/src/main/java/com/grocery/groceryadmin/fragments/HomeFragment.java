package com.grocery.groceryadmin.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.grocery.groceryadmin.adapter.AllProductsAdapter;
import com.grocery.groceryadmin.databinding.FragmentHomeBinding;
import com.grocery.groceryadmin.models.ProductsModel;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    ArrayList<ProductsModel> productsModelArrayList;
    AllProductsAdapter allProductsAdapter;
    ProgressDialog progressDialog;
    FirebaseFirestore fireStore;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();


        // Recycler View For Adding the Products Data
        productsModelArrayList = new ArrayList<>();
        binding.rvAllProducts.setHasFixedSize(true);
        binding.rvAllProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allProductsAdapter = new AllProductsAdapter(getContext(), productsModelArrayList);
        binding.rvAllProducts.setAdapter(allProductsAdapter);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching the Data.....");
        progressDialog.show();

        AddAllProductsData();

        RemoveProductData();



        return view;
    }



    private void AddAllProductsData() {

        fireStore.collection("Product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                productsModelArrayList.add(document.getDocument().toObject(ProductsModel.class));

                            }

                            allProductsAdapter.notifyDataSetChanged();

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });


    }


    private void RemoveProductData() {

        fireStore.collection("Product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.REMOVED) {

                                String removedDocId = document.getDocument().getId();
                                int indexOfRemovedItem = -1;
                                for (int i = 0 ; i < productsModelArrayList.size() ; i++) {

                                    ProductsModel item = productsModelArrayList.get(i);
                                    if (item.getKey().equals(removedDocId)) {
                                        indexOfRemovedItem = i;
                                        break;
                                    }

                                }

                                if (indexOfRemovedItem != -1) {
                                    productsModelArrayList.remove(indexOfRemovedItem);
                                    productsModelArrayList.remove(document.getDocument().toObject(ProductsModel.class));
                                }

                            }

                            allProductsAdapter.notifyDataSetChanged();


                        }

                    }
                });

    }






}