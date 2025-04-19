package com.grocery.groceryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.adapters.AllProductsAdapter;
import com.grocery.groceryapp.databinding.ActivitySearchDataBinding;
import com.grocery.groceryapp.models.ProductsModel;

import java.util.ArrayList;

public class SearchDataActivity extends AppCompatActivity {

    ActivitySearchDataBinding binding;
    ArrayList<ProductsModel> productsModelArrayList;
    AllProductsAdapter allProductsAdapter;
    FirebaseFirestore fireStore;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySearchDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireStore = FirebaseFirestore.getInstance();

        productsModelArrayList = new ArrayList<>();

        binding.rvUserSearchProducts.setHasFixedSize(true);
        binding.rvUserSearchProducts.setLayoutManager(new GridLayoutManager(this, 2));

        allProductsAdapter = new AllProductsAdapter(this, productsModelArrayList);

        binding.rvUserSearchProducts.setAdapter(allProductsAdapter);


        intent = getIntent();
        binding.txtUserSearchQuery.setText(intent.getStringExtra("query"));


        AddData();
        binding.rvUserSearchProducts.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.GONE);
                binding.rvUserSearchProducts.setVisibility(View.VISIBLE);
                SearchList(intent.getStringExtra("query"));
                UpdateData();
            }
        }, 2000);


        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.searchViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), UserSearchesActivity.class);

                if (!binding.txtUserSearchQuery.getText().toString().isEmpty()) {

                    intent1.putExtra("query", intent.getStringExtra("query"));

                }
                startActivity(intent1);
            }
        });


    }



    private void SearchList(String text) {

        ArrayList<ProductsModel> searchList = new ArrayList<>();

        for (ProductsModel productsModel : productsModelArrayList) {

            if (productsModel.getProductTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(productsModel);

            } else if (productsModel.getProductCategory().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(productsModel);

            }

        }

        // Update adapter and handle empty search results visibility
        if (searchList.isEmpty()) {
            // Set "No Results Found" TextView visibility to VISIBLE
            binding.txtNoResultsFound.setVisibility(View.VISIBLE);
            allProductsAdapter.SearchDataList(new ArrayList<>()); // Clear adapter or pass empty list
        } else {
            // Set "No Results Found" TextView visibility to GONE (optional)
            binding.txtNoResultsFound.setVisibility(View.GONE);
            allProductsAdapter.SearchDataList(searchList);
        }

    }


    public void UpdateData() {

        fireStore.collection("Product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {


                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.MODIFIED) {

                                String modifiedDocId = document.getDocument().getId(); // this Line get the Document Id in the FireStore.
                                int indexOfUpdatedItem = -1; // This Line represent if Item in Recycler view -1 means not exists.
                                for (int i = 0; i < productsModelArrayList.size(); i++) { // this is a for Loop that represent if recycler view have item and get the quotesDataArraylist Size Like each Item.

                                    ProductsModel item = productsModelArrayList.get(i); // this Like get the Current Item in Recycler View.
                                    if (item.getKey().equals(modifiedDocId)) { // this if Condition Check if the key in fireStore field == current key.
                                        indexOfUpdatedItem = i; // this Line Represent if key in fireStore field == current key so set the indexOfUpdatedItem = i means Recycler View have Item.
                                        break;
                                    }
                                }
                                if (indexOfUpdatedItem != -1) { // this Line Check if Recycler View != have no Item if that means recycler view have item.
                                    productsModelArrayList.set(indexOfUpdatedItem, document.getDocument().toObject(ProductsModel.class)); // this set the Data in quotesDataArrayList.
                                }

                            }

                            allProductsAdapter.notifyDataSetChanged();


                        }

                    }
                });

    }


    private void AddData() {

        fireStore.collection("Product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

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




}