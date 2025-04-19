package com.grocery.groceryapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityToShipAndDeliveryBinding;
import com.grocery.groceryapp.fragments.AllOrdersFragment;
import com.grocery.groceryapp.fragments.ToDeliveredFragment;
import com.grocery.groceryapp.fragments.ToShippedFragment;

public class ToShipAndDeliveryActivity extends AppCompatActivity {

    ActivityToShipAndDeliveryBinding binding;


    @Override
    protected void onStart() {
        super.onStart();

        String orderStatus = getIntent().getStringExtra("orderStatus");

        if (orderStatus.equals(getResources().getString(R.string.shipped))) {

            binding.tabLayout.getTabAt(1).select();
            binding.tabLayout.invalidate();

        } else if (orderStatus.equals(getResources().getString(R.string.delivered))) {

            binding.tabLayout.getTabAt(2).select();
            binding.tabLayout.invalidate();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityToShipAndDeliveryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        AddTabLayout();


        setSupportActionBar(binding.toolbar);
        // set te back Icon on Tool Bar
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));

        // set the click Listener to back Icon on Tool Bar
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.INSTANCE.animateSlideDown(ToShipAndDeliveryActivity.this);
            }
        });



    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideDown(ToShipAndDeliveryActivity.this);

    }

    private void AddTabLayout() {


        String orderStatus = getIntent().getStringExtra("orderStatus");

        if (orderStatus.equals(getResources().getString(R.string.shipped))) {

            binding.toolbar.setTitle("To Shipped");

        } else if (orderStatus.equals(getResources().getString(R.string.delivered))) {

            binding.toolbar.setTitle("To Delivered");

        }


        binding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        binding.toolbar.setTitle("All Orders");
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frameLayout, new AllOrdersFragment());
                        transaction.commit();
                        break;

                    case 1:
                        binding.toolbar.setTitle("To Shipped");
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.frameLayout, new ToShippedFragment());
                        transaction1.commit();
                        break;

                    case 2:
                        binding.toolbar.setTitle("To Delivered");
                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.frameLayout, new ToDeliveredFragment());
                        transaction2.commit();
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