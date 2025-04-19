package com.grocery.groceryadmin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryadmin.R;
import com.grocery.groceryadmin.activities.GetProductImagesActivity;
import com.grocery.groceryadmin.databinding.FragmentAddProductBinding;


public class AddProductFragment extends Fragment {

    FragmentAddProductBinding binding;
    FirebaseFirestore fireStore;
    PopupMenu categoryPopupMenu;


    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();

        binding.txtCategory.setText(null); // set the Category text to null



        binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckValidationsAndGoNextActivity();
            }
        });

        binding.imgMenuCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopupMenu(v);
            }
        });





        return view;
    }



    private void CategoryPopupMenu(View view) {

        categoryPopupMenu = new PopupMenu(getContext(), view);

        categoryPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.nav_tech) {

                    binding.txtCategory.setText(getResources().getString(R.string.tech));
                    return true;

                } else if (item.getItemId() == R.id.nav_clothing) {

                    binding.txtCategory.setText(getResources().getString(R.string.clothing));
                    return true;

                } else if (item.getItemId() == R.id.nav_automotive) {

                    binding.txtCategory.setText(getResources().getString(R.string.automotive));
                    return true;

                } else if (item.getItemId() == R.id.nav_Beauty) {

                    binding.txtCategory.setText(getResources().getString(R.string.beauty));
                    return true;

                }

                return false;
            }
        });


        categoryPopupMenu.getMenuInflater().inflate(R.menu.category_menu, categoryPopupMenu.getMenu());
        categoryPopupMenu.show();


    }

    private void CheckValidationsAndGoNextActivity() {

        String productTitle = binding.edtProductTitle.getText().toString();
        String productPriceString = binding.edtProductPrice.getText().toString();
        String availableProductsString = binding.edtAvailableProducts.getText().toString();
        String productDescription = binding.edtProductDescription.getText().toString();
        String productCategory = binding.txtCategory.getText().toString();
        String key = fireStore.collection("Products").document().getId();
        String shortProductName = binding.edtShortProductName.getText().toString();

        if (productTitle.isEmpty()) {
            Toast.makeText(getContext(), "Product Title can't be empty", Toast.LENGTH_SHORT).show();

        } else if (productPriceString.isEmpty()) {
            Toast.makeText(getContext(), "Product Price can't be empty", Toast.LENGTH_SHORT).show();

        } else if (availableProductsString.isEmpty()) {
            Toast.makeText(getContext(), "Available Product can't be empty", Toast.LENGTH_SHORT).show();

        } else if (productDescription.isEmpty()) {
            Toast.makeText(getContext(), "Product Description can't be empty", Toast.LENGTH_SHORT).show();

        } else if (productCategory.isEmpty()) {
            Toast.makeText(getContext(), "Product Category can't be null", Toast.LENGTH_SHORT).show();

        } else if (shortProductName.isEmpty()) {
            Toast.makeText(getContext(), "Short Product Name can't be empty", Toast.LENGTH_SHORT).show();

        }

        if (
                productTitle.isEmpty()
                        || productPriceString.isEmpty()
                        || availableProductsString.isEmpty()
                        || productDescription.isEmpty()
                        || productCategory.isEmpty()
                        || shortProductName.isEmpty()

        ) {
            Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();

        } else {

            long productPrice = Long.parseLong(productPriceString);
            long availableProducts = Long.parseLong(availableProductsString);

            GoNextActivity(productTitle, productPrice, availableProducts, productDescription, productCategory, key, shortProductName);

        }


    }


    private void GoNextActivity(
            String productTitle, long productPrice, long availableProducts, String productDescription,
            String productCategory, String key, String shortProductName

    ) {

        Intent intent = new Intent(getContext(), GetProductImagesActivity.class);
        intent.putExtra("productTitle", productTitle);
        intent.putExtra("productPrice", productPrice);
        intent.putExtra("availableProducts", availableProducts);
        intent.putExtra("productDescription", productDescription);
        intent.putExtra("productCategory", productCategory);
        intent.putExtra("key", key);
        intent.putExtra("shortProductName", shortProductName);
        startActivity(intent);
    }



}