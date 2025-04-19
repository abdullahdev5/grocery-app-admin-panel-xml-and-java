package com.grocery.groceryapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.CheckOutProductActivity;
import com.grocery.groceryapp.adapters.AllProductsAdapter;
import com.grocery.groceryapp.adapters.CartProductsAdapter;
import com.grocery.groceryapp.databinding.FragmentCartBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ProductsModel;

import java.util.ArrayList;


public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    ArrayList<ProductsModel> productsModelArrayList;
    ArrayList<CartProductsModel> cartProductsModelArrayList;
    AllProductsAdapter allProductsAdapter;
    CartProductsAdapter cartProductsAdapter;
    DocumentReference userDocRef;
    CollectionReference cartProductsCol;
    private double subTotal = 0.0;

    public CartFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        // Recycler View for Cart Products
        cartProductsModelArrayList = new ArrayList<>();
        binding.rvCartProducts.setHasFixedSize(true);
        binding.rvCartProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        cartProductsAdapter = new CartProductsAdapter(getContext(), cartProductsModelArrayList, binding.txtSubTotal);
        binding.rvCartProducts.setAdapter(cartProductsAdapter);



        // Recycler View for All Products
        productsModelArrayList = new ArrayList<>();
        binding.rvAllProducts.setHasFixedSize(true);
        binding.rvAllProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allProductsAdapter = new AllProductsAdapter(getContext(), productsModelArrayList);
        binding.rvAllProducts.setAdapter(allProductsAdapter);
        AddAllProductsData();


        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);
        cartProductsCol = userDocRef.collection("CartProducts");

        CheckValidationAndSetCartProducts();

        RemoveProductFromCart();

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        double subTotal = Double.parseDouble(binding.txtSubTotal.getText().toString());

                        Intent intent = new Intent(getContext(), CheckOutProductActivity.class);
                        intent.putExtra("subTotal", subTotal);
                        startActivity(intent);


            }
        });



        return view;
    }

    private void CheckValidationAndSetCartProducts() {

        Query query = cartProductsCol.count().getQuery();

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int count = task.getResult().getDocuments().size();

                            if (count == 0) {
                                binding.txtNoItemsHere.setVisibility(View.VISIBLE);
                                binding.checkoutHolder.setVisibility(View.GONE);

                            } else {
                                binding.txtNoItemsHere.setVisibility(View.GONE);
                                binding.checkoutHolder.setVisibility(View.VISIBLE);

                                AddCartProducts();
                                ShowSubTotalPrice();

                            }

                        }

                    }
                });

    }


    private void ShowSubTotalPrice() {

        for (int i = 0 ; i < cartProductsModelArrayList.size() ; i++)
            subTotal = subTotal + cartProductsModelArrayList.get(i).getProductPrice() * cartProductsModelArrayList.get(i).getProductItems();

        for (int i = 0 ; i < cartProductsModelArrayList.size() ; i++)
            subTotal = subTotal + cartProductsModelArrayList.get(i).getProductPrice() / cartProductsModelArrayList.get(i).getProductItems();

        binding.txtSubTotal.setText(String.valueOf(subTotal));

    }

    private void AddAllProductsData() {

        fireStore.collection("Product")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                productsModelArrayList.add(document.getDocument().toObject(ProductsModel.class));

                            }

                            allProductsAdapter.notifyDataSetChanged();


                        }

                    }
                });



    }



    private void AddCartProducts() {

        cartProductsCol.orderBy("key", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error != null) {

                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                }

                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.ADDED) {

                        cartProductsModelArrayList.add(document.getDocument().toObject(CartProductsModel.class));

                    }

                    cartProductsAdapter.notifyDataSetChanged();


                }

            }
        });

    }


    private void RemoveProductFromCart() {

        cartProductsCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error != null) {

                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                }

                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.REMOVED) {

                        String removedDocId = document.getDocument().getId();
                        int indexOfRemovedItem = -1;
                        for (int i = 0; i < cartProductsModelArrayList.size(); i++) {

                            CartProductsModel item = cartProductsModelArrayList.get(i);
                            if (item.getKey().equals(removedDocId)) {
                                indexOfRemovedItem = i;
                                break;
                            }

                        }

                        if (indexOfRemovedItem != -1) {
                            cartProductsModelArrayList.remove(indexOfRemovedItem);
                            cartProductsModelArrayList.remove(document.getDocument().toObject(CartProductsModel.class));
                        }

                    }

                    cartProductsAdapter.notifyDataSetChanged();

                }


            }
        });

    }



}