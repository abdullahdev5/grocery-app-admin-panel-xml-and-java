package com.grocery.groceryapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import com.grocery.groceryapp.databinding.ActivityBuyedProductsBinding;
import com.grocery.groceryapp.models.BuyedProductsModel;
import com.grocery.groceryapp.models.BuyingProductsModel;

import java.util.ArrayList;

public class BuyedProductsActivity extends AppCompatActivity {

    ActivityBuyedProductsBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol;
    ArrayList<BuyedProductsModel> buyedProductsModelArrayList;
    BuyedProductsAdapter buyedProductsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBuyedProductsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        buyedProductsModelArrayList = new ArrayList<>();
        binding.rvBuyedProducts.setHasFixedSize(true);
        binding.rvBuyedProducts.setLayoutManager(new LinearLayoutManager(this));
        buyedProductsAdapter = new BuyedProductsAdapter(this, buyedProductsModelArrayList);
        binding.rvBuyedProducts.setAdapter(buyedProductsAdapter);
        AddBuyedProducts();






        setSupportActionBar(binding.toolbar);

        // set te back Icon on Tool Bar
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));

        // set the click Listener to back Icon on Tool Bar
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.INSTANCE.animateShrink(BuyedProductsActivity.this);
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateShrink(BuyedProductsActivity.this);
    }

    private void AddBuyedProducts() {

        buyedProductsCol.orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(BuyedProductsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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