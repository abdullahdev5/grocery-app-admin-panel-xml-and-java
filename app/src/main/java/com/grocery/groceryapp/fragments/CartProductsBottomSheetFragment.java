package com.grocery.groceryapp.fragments;

import android.content.Intent;
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
import com.grocery.groceryapp.activities.CheckOutProductActivity;
import com.grocery.groceryapp.adapters.CartProductsAdapter;
import com.grocery.groceryapp.adapters.WishListProductsAdapter;
import com.grocery.groceryapp.databinding.FragmentCartProductsBottomSheetBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.WishListProductsModel;

import java.util.ArrayList;


public class CartProductsBottomSheetFragment extends BottomSheetDialogFragment {

    FragmentCartProductsBottomSheetBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference cartProductsCol;
    ArrayList<CartProductsModel> cartProductsModelArrayList;
    CartProductsAdapter cartProductsAdapter;
    private double subTotal = 0.0;


    public CartProductsBottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartProductsBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        String uid = auth.getCurrentUser().getUid();
        userDocRef = fireStore.collection("Users").document(uid);

        // Wishlist Products
        cartProductsCol = userDocRef.collection("CartProducts");


        cartProductsModelArrayList = new ArrayList<>();
        binding.rvCartProducts.setHasFixedSize(true);
        binding.rvCartProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        cartProductsAdapter = new CartProductsAdapter(getContext(), cartProductsModelArrayList, binding.txtSubTotal);
        binding.rvCartProducts.setAdapter(cartProductsAdapter);

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

    private void AddCartProducts() {

        cartProductsCol
                .orderBy("key", Query.Direction.DESCENDING)
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


    private void CheckValidationAndSetCartProducts() {

        Query query = cartProductsCol.count().getQuery();

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int count = task.getResult().getDocuments().size();

                            if (count == 0) {
                                binding.checkoutHolder.setVisibility(View.GONE);

                            } else {
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





}