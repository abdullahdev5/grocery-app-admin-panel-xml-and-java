package com.grocery.groceryapp.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityOrderStatusBinding;

public class OrderStatusActivity extends AppCompatActivity {

    ActivityOrderStatusBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol;

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

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        AddUserInformation();

        UpdateOrderStatus();





        setSupportActionBar(binding.toolbar);

        // set te back Icon on Tool Bar
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));

        // set the click Listener to back Icon on Tool Bar
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.INSTANCE.animateSlideDown(OrderStatusActivity.this);
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideDown(OrderStatusActivity.this);
    }

    private void makeNotification(String notificationTitle, String notificationDescription) {

        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), OrderStatusActivity.class);

        intent.putExtra("orderId", getIntent().getLongExtra("orderId", 0));
        intent.putExtra("orderStatus", getIntent().getStringExtra("orderStatus"));
        intent.putExtra("totalPrice", getIntent().getStringExtra("totalPrice"));
        intent.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
        intent.putExtra("address", getIntent().getStringExtra("address"));
        intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
        intent.putExtra("productTitle", getIntent().getStringExtra("productTitle"));
        intent.putExtra("productImage", getIntent().getStringExtra("productImage"));
        intent.putExtra("productPrice", getIntent().getLongExtra("productPrice", 0));
        intent.putExtra("productItems", getIntent().getLongExtra("productItems", 0));
        intent.putExtra("key", getIntent().getStringExtra("key"));

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

    private void AddUserInformation() {

        binding.txtOrderId.setText(String.valueOf(getIntent().getLongExtra("orderId", 0)));
        binding.txtUserName.setText(getIntent().getStringExtra("fullName"));
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


    private void UpdateOrderStatus() {


            String orderStatus = getIntent().getStringExtra("orderStatus");

            if (orderStatus.equals(getResources().getString(R.string.processing))) {

                binding.viewProcessing.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

            } else if (orderStatus.equals(getResources().getString(R.string.shipped))) {

                // Add Notification of Shipped Product.
                makeNotification(getIntent().getStringExtra("productTitle"),
                        "Your Package Now is on the way to our last mile hub");

                binding.dividerShipped.setBackgroundColor(getResources().getColor(R.color.app_icon));
                binding.viewShipped.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

            } else if (orderStatus.equals(getResources().getString(R.string.out_for_delivery))) {

                // Add Notification of Out for Delivery Product.
                makeNotification(getIntent().getStringExtra("productTitle"),
                        "Our Delivery Partner will attempt to deliver Your Pavkage today");

                binding.dividerShipped.setBackgroundColor(getResources().getColor(R.color.app_icon));
                binding.viewShipped.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));
                binding.dividerOutForDelivery.setBackgroundColor(getResources().getColor(R.color.app_icon));
                binding.viewOutForDelivery.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

            } else if (orderStatus.equals(getResources().getString(R.string.delivered))) {

                // Add Notification of Shipped Product.
                makeNotification(getIntent().getStringExtra("productTitle"),
                        "Your Package has been delivered");

                binding.dividerShipped.setBackgroundColor(getResources().getColor(R.color.app_icon));
                binding.viewShipped.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));
                binding.dividerOutForDelivery.setBackgroundColor(getResources().getColor(R.color.app_icon));
                binding.viewOutForDelivery.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));
                binding.dividerDelivered.setBackgroundColor(getResources().getColor(R.color.app_icon));
                binding.viewDelivered.setBackground(getResources().getDrawable(R.drawable.shape_order_status_current));

            }



    }




}