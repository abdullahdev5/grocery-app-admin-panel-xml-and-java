package com.grocery.groceryapp.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.mutation.NumericIncrementTransformOperation;
import com.google.firebase.provider.FirebaseInitProvider;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.adapters.BuyingProductsAdapter;
import com.grocery.groceryapp.adapters.CartProductsAdapter;
import com.grocery.groceryapp.databinding.ActivityCheckOutProductBinding;
import com.grocery.groceryapp.models.BuyingProductsModel;
import com.grocery.groceryapp.models.CartProductsModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CheckOutProductActivity extends AppCompatActivity {

    ActivityCheckOutProductBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef, cartProductsRef;
    CollectionReference cartProductsCol, buyedProductsCol;
    ArrayList<BuyingProductsModel> buyingProductsModelArrayList;
    BuyingProductsAdapter buyingProductsAdapter;
    private double totalPrice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCheckOutProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        cartProductsCol = userDocRef.collection("CartProducts");

        String cartProductsKey = cartProductsCol.document().getId();
        cartProductsRef = cartProductsCol.document(cartProductsKey);

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        buyingProductsModelArrayList = new ArrayList<>();
        binding.rvBuyingProducts.setHasFixedSize(true);
        binding.rvBuyingProducts.setLayoutManager(new LinearLayoutManager(this));
        buyingProductsAdapter = new BuyingProductsAdapter(this, buyingProductsModelArrayList);
        binding.rvBuyingProducts.setAdapter(buyingProductsAdapter);
        AddBuyingProducts();





        Intent intent = getIntent();

        double subTotal = intent.getDoubleExtra("subTotal", 0);

        ShowUsernameAndEmail();

        // calculating the Total Price
        CalculateTotalPrice(subTotal);
        binding.txtSubTotal.setText(String.valueOf(subTotal));
        binding.txtTotalPrice.setText(String.valueOf(totalPrice));

        binding.edtPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        binding.btnOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = binding.edtPhoneNumber.getText().toString();
                String address = binding.edtAddress.getText().toString();

                if (
                        phoneNumber.length() < 10 ||
                                address.isEmpty()
                ) {

                    Toast.makeText(CheckOutProductActivity.this, "Please Add Information first", Toast.LENGTH_SHORT).show();

                } else {
                    UpdateCartProductsByClickingOrderButton(phoneNumber, address);
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

    private void makeNotification(long orderId) {

        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Your Order has been Placed")
                .setContentText("Order Id: " + String.valueOf(orderId))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), BuyedProductsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);

            if (notificationChannel == null) {

                int importance  = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, "Description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);

            }

        }

        notificationManager.notify(0, notificationBuilder.build());



    }

    private void UpdateCartProductsByClickingOrderButton(String phoneNumber, String address) {

        if (binding.cashOnDeliveryHolder.getBackground() != null) {

                String getLessOrderId = String.valueOf(UUID.randomUUID().getMostSignificantBits()).concat("-").substring(0, 7);

                long orderId = Long.parseLong(getLessOrderId);

                String orderStatus = "Processing";
                String paymentMethod = "cash";
                String userName = binding.txtUsername.getText().toString();


                cartProductsCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                Map<String, Object> updateBuyedProductsDocs = new HashMap<>();
                                updateBuyedProductsDocs.put("orderId", orderId);
                                updateBuyedProductsDocs.put("orderStatus", orderStatus);
                                updateBuyedProductsDocs.put("paymentMethod", paymentMethod);
                                updateBuyedProductsDocs.put("phoneNumber", phoneNumber);
                                updateBuyedProductsDocs.put("address", address);
                                updateBuyedProductsDocs.put("fullName", userName);
                                updateBuyedProductsDocs.put("timeStamp", FieldValue.serverTimestamp());

                                documentSnapshot.getReference()
                                        .update(updateBuyedProductsDocs)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(CheckOutProductActivity.this,
                                                        "Your Order is Placed Now with this Order Id: "
                                                                + orderId, Toast.LENGTH_SHORT).show();
                                                AddBuyedProducts();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CheckOutProductActivity.this,
                                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        } else {
                            Toast.makeText(CheckOutProductActivity.this,
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        }

    }


    private void AddBuyedProducts() {

            cartProductsCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {

                    Map<String , Object> cartProductData = document.getData();

                    String key = document.getString("key");

        buyedProductsCol.document(key)
                .set(cartProductData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        makeNotification(document.getLong("orderId"));

                        document.getReference().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        GoToBuyedProductsActivity();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckOutProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }

                } else  {
                    Toast.makeText(CheckOutProductActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void GoToBuyedProductsActivity() {
        Intent intent = new Intent(getApplicationContext(), BuyedProductsActivity.class);
        startActivity(intent);
        finish();
    }

    private void ShowUsernameAndEmail() {
        String uid = auth.getCurrentUser().getUid();

        fireStore.collection("Users")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        binding.txtUsername.setText(value.getString("fullName"));
                        binding.txtEmail.setText(value.getString("email"));

                    }
                });



    }

    private void AddBuyingProducts() {

        cartProductsCol.orderBy("key", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                        if (error != null) {

                            Toast.makeText(CheckOutProductActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                buyingProductsModelArrayList.add(document.getDocument().toObject(BuyingProductsModel.class));

                            }

                            buyingProductsAdapter.notifyDataSetChanged();


                        }

                    }
                });

    }

    private void CalculateTotalPrice(double subTotal) {

        // Calculating the Total Price with Tax of 7.5%
        double taxRate = Double.parseDouble(binding.txtTax.getText().toString());
        double taxAmount = subTotal * taxRate;
        totalPrice = subTotal + taxAmount;


    }



}