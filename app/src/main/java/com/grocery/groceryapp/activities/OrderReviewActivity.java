package com.grocery.groceryapp.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityOrderReviewBinding;
import com.grocery.groceryapp.fragments.AllOrdersFragment;
import com.grocery.groceryapp.fragments.ToDeliveredFragment;
import com.grocery.groceryapp.fragments.ToOrderReviewsFragment;
import com.grocery.groceryapp.fragments.ToShippedFragment;
import com.grocery.groceryapp.fragments.UserOrderReviewsFragment;

public class OrderReviewActivity extends AppCompatActivity {

    ActivityOrderReviewBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference userDocRef;
    CollectionReference buyedProductsCol, orderReviewsCol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderReviewBinding.inflate(getLayoutInflater());
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

        orderReviewsCol = userDocRef.collection("OrderReviews");


        AddTabLayout();

        SetBadgeToTheToReviewTabItem();
        SetBadgeToTheUserReviewsTabItem();







        setSupportActionBar(binding.toolbar);

        // set te back Icon on Tool Bar
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));

        // set the click Listener to back Icon on Tool Bar
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.INSTANCE.animateSlideDown(OrderReviewActivity.this);
            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideDown(OrderReviewActivity.this);
    }

    private void SetBadgeToTheToReviewTabItem() {

        Query query = buyedProductsCol.whereEqualTo("orderStatus", getResources().getString(R.string.delivered));

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    int count = task.getResult().size();

                    if (count == 0) {


                    } else {

                        BadgeDrawable badgeDrawable = binding.tabLayout.getTabAt(0).getOrCreateBadge();
                        badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
                        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.app_icon));
                        badgeDrawable.setNumber(count);
                    }


                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void SetBadgeToTheUserReviewsTabItem() {

        Query query = orderReviewsCol.count().getQuery();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int count = value.getDocuments().size();

                if (count == 0) {


                } else {

                    BadgeDrawable badgeDrawable = binding.tabLayout.getTabAt(1).getOrCreateBadge();
                    badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
                    badgeDrawable.setBackgroundColor(getResources().getColor(R.color.app_icon));
                    badgeDrawable.setNumber(count);

                }


            }
        });

    }

    private void AddTabLayout() {

        binding.toolbar.setTitle("To Reviews");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new ToOrderReviewsFragment());
        transaction.commit();

        binding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        binding.toolbar.setTitle("To Reviews");
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frameLayout, new ToOrderReviewsFragment());
                        transaction.commit();
                        break;

                    case 1:
                        binding.toolbar.setTitle("My Review");
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.frameLayout, new UserOrderReviewsFragment());
                        transaction1.commit();
                        break;

                    default:



                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }




}