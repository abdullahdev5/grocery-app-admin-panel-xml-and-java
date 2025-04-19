package com.grocery.groceryapp.activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityMainBinding;
import com.grocery.groceryapp.fragments.CartFragment;
import com.grocery.groceryapp.fragments.HomeFragment;
import com.grocery.groceryapp.fragments.ProfileFragment;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference cartProductsCol, buyedProductsCol;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
   /*     ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/               // this code Add the Extra Height at the bottom of Bottom Navigation View items.

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        getWindow().setStatusBarColor(getResources().getColor(R.color.app_icon)); // changing status bar

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);
        cartProductsCol = userDocRef.collection("CartProducts");

        buyedProductsCol = userDocRef.collection("BuyedProducts");

        SetBadgeToCartMenuItem();

        BottomNavItemSelected();


    }



    private void SetBadgeToCartMenuItem() {

        cartProductsCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int document = value.getDocuments().size();

                if (document != 0) {
                    BadgeDrawable badgeDrawable =
                            binding.bottomNavigation
                                    .getOrCreateBadge(R.id.nav_cart);

                    badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
                    badgeDrawable.setBackgroundColor(getResources().getColor(R.color.app_icon));
                    badgeDrawable.onTextSizeChange();
                    badgeDrawable.setNumber(document);
                    badgeDrawable.setVisible(true);

                }



            }
        });



    }


    private void BottomNavItemSelected() {

        loadFragment(new HomeFragment(), 0);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_home) {

                    loadFragment(new HomeFragment(), 1);

                    return true;

                }
                if (menuItem.getItemId() == R.id.nav_cart) {

                    loadFragment(new CartFragment(), 1);

                    return true;

                }
                if (menuItem.getItemId() == R.id.nav_profile) {

                    loadFragment(new ProfileFragment(), 1);

                    return true;

                }

                return false;
            }
        });

    }


    private void loadFragment(Fragment fragment, int flag) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (flag == 0) {

            transaction.add(R.id.frameLayout, fragment);

        } else
            transaction.replace(R.id.frameLayout, fragment);


        transaction.commit();

    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Alert!");
        builder.setMessage("Are you sure want to Leave this App.");

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
                dialog.dismiss();
            }
        });

        builder.show();


    }


}