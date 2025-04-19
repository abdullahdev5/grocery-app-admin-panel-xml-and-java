package com.grocery.groceryadmin.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryadmin.R;
import com.grocery.groceryadmin.databinding.ActivityOrderStatusBinding;
import com.grocery.groceryadmin.models.OrdersModel;

public class OrderStatusActivity extends AppCompatActivity {

    ActivityOrderStatusBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    CollectionReference buyedProductsCol, usersCol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderStatusBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        usersCol= fireStore.collection("Users");

        AddUserInformation();
        UpdateOrderStatusOnUI();


        binding.txtStatus.setText(getIntent().getStringExtra("orderStatus")); // Showing the Updated Order Status Permanently.

        binding.imgMenuStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderStatusPopupMenu(v);
            }
        });

        binding.btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderStatus = binding.txtStatus.getText().toString();

                if (orderStatus != null) {
                    UpdateOrdersStatusOnDatabase(orderStatus);
                }

            }
        });




        setSupportActionBar(binding.toolbar);

        // set te back Icon on Tool Bar
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));

        // set the click Listener to back Icon on Tool Bar
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }

    private void OrderStatusPopupMenu(View view) {

        PopupMenu statusPopupMenu = new PopupMenu(getApplicationContext(), view);

        statusPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.nav_delivered) {

                    binding.txtStatus.setText(getResources().getString(R.string.delivered));
                    return true;

                } else if (item.getItemId() == R.id.nav_outForDelivery) {

                    binding.txtStatus.setText(getResources().getString(R.string.out_for_delivery));
                    return true;

                } else if (item.getItemId() == R.id.nav_shipped) {

                    binding.txtStatus.setText(getResources().getString(R.string.shipped));
                    return true;

                } else if (item.getItemId() == R.id.nav_processing) {

                    binding.txtStatus.setText(getResources().getString(R.string.processing));
                    return true;

                }

                return false;
            }
        });


        statusPopupMenu.getMenuInflater().inflate(R.menu.order_status_menu, statusPopupMenu.getMenu());
        statusPopupMenu.show();


    }

    private void UpdateOrdersStatusOnDatabase(String orderStatus) {

        Query query = usersCol;

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(OrderStatusActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onEvent: Error");
                }

                for (DocumentSnapshot document : value.getDocuments()) {

                    String uid = document.getId();

                    buyedProductsCol = document.getReference().collection("BuyedProducts");

                    buyedProductsCol.document(getIntent().getStringExtra("key"))
                            .update("orderStatus", orderStatus)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(OrderStatusActivity.this, "Updated: " + orderStatus, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OrderStatusActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }


            }
        });


    }

    private void UpdateOrderStatusOnUI() {


        String orderStatus = getIntent().getStringExtra("orderStatus");

        if (orderStatus.equals(getResources().getString(R.string.processing))) {

            binding.viewProcessing.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

        } else if (orderStatus.equals(getResources().getString(R.string.shipped))) {

            binding.dividerShipped.setBackgroundColor(getResources().getColor(R.color.app_icon));
            binding.viewShipped.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

        } else if (orderStatus.equals(getResources().getString(R.string.out_for_delivery))) {

            binding.dividerShipped.setBackgroundColor(getResources().getColor(R.color.app_icon));
            binding.viewShipped.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));
            binding.dividerOutForDelivery.setBackgroundColor(getResources().getColor(R.color.app_icon));
            binding.viewOutForDelivery.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

        } else if (orderStatus.equals(getResources().getString(R.string.delivered))) {

            binding.dividerShipped.setBackgroundColor(getResources().getColor(R.color.app_icon));
            binding.viewShipped.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));
            binding.dividerOutForDelivery.setBackgroundColor(getResources().getColor(R.color.app_icon));
            binding.viewOutForDelivery.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));
            binding.dividerDelivered.setBackgroundColor(getResources().getColor(R.color.app_icon));
            binding.viewDelivered.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

        }



    }

    private void AddUserInformation() {

        binding.txtOrderId.setText(String.valueOf(getIntent().getLongExtra("orderId", 0)));
        binding.txtUserName.setText(getIntent().getStringExtra("userName"));
        binding.txtPhoneNumber.setText(getIntent().getStringExtra("phoneNumber"));
        binding.txtAddress.setText(getIntent().getStringExtra("address"));

        AddOrderInformation();

    }

    private void AddOrderInformation() {

        Glide.with(getApplicationContext())
                .load(getIntent().getStringExtra("productImage"))
                .placeholder(R.drawable.loading_image)
                .into(binding.imgProductImage);

        binding.txtProductTitle.setText(getIntent().getStringExtra("productTitle"));
        binding.txtProductPrice.setText(String.valueOf(getIntent().getLongExtra("productPrice", 0)));
        binding.txtProductItems.setText(String.valueOf(getIntent().getLongExtra("productItems", 0)));
        binding.txtTotalPrice.setText(String.valueOf(getIntent().getDoubleExtra("totalPrice", 0)));


    }




}