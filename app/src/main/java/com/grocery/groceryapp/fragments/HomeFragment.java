package com.grocery.groceryapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.MainActivity;
import com.grocery.groceryapp.activities.UserSearchesActivity;
import com.grocery.groceryapp.adapters.AllProductsAdapter;
import com.grocery.groceryapp.adapters.CategoryAdapter;
import com.grocery.groceryapp.databinding.FragmentHomeBinding;
import com.grocery.groceryapp.models.CategoryModel;
import com.grocery.groceryapp.models.ProductsModel;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    ArrayList<ProductsModel> productsModelArrayList;
    AllProductsAdapter allProductsAdapter;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    ArrayList<CategoryModel> categoryModelArrayList;
    CategoryAdapter categoryAdapter;
    // this is for Category Icons
    private final String techImage = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTwECOnwPS8WhwlPfC1tXBhdh5vaCo-z0S6j_9pAg1nt-Q2dL0u";
    private final String clothingImage = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQjKnLtVxOZ_0HlkwAH_R6S7EgRJX4LB0vBEoOA8mmw8OkbehPb";
    private final String automotiveImage = "https://thumbs.dreamstime.com/z/automotive-repair-icon-car-service-hood-mechanic-tools-line-vector-illustration-automotive-repair-icon-car-service-hood-mechanic-253153129.jpg?w=768";
    private final String beautyImage = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTmC7vxdR4DM9emGgxiG-KsC8XTVYBe-l3V2vyUmY9RfqoH-WfQ";
    // this is for Slider Images
    private final String imageUri1 = "https://www.eurofreshmarket.com/manage/web/sites/default/files/frontSliders/slider-weekly-ad.jpg";
    private final String imageUri2 = "https://www.ebasketksa.com/app/home_image/64b4ff61d0fad22247c9175b0cba8193.jpeg";
    private final String imageUri3 = "https://storetodoorjamaica.com/wp-content/uploads/2020/09/Banner-left-text.jpg";
    private final String imageUri4 = "https://img.freepik.com/free-photo/discount-armchair-podium_23-2150165449.jpg?t=st=1715852042~exp=1715855642~hmac=3708bd8de78d6c68354f54a30da41697a85a8d932078a2d4734efce1b48ca917&w=996";


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Adding Offers Image Slider
        AddSliderImages();

        // Recycler View for Adding Category Data
        categoryModelArrayList = new ArrayList<>();
        binding.rvCategory.setHasFixedSize(true);
        binding.rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelArrayList);
        binding.rvCategory.setAdapter(categoryAdapter);
        AddCategoryData();


        // Recycler View For Adding the Products Data
        productsModelArrayList = new ArrayList<>();
        binding.rvAllProducts.setHasFixedSize(true);
        binding.rvAllProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allProductsAdapter = new AllProductsAdapter(getContext(), productsModelArrayList);
        binding.rvAllProducts.setAdapter(allProductsAdapter);
        AddAllProductsData();


        binding.searchViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoSearchesActivity();
            }
        });

/*        binding.nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > 1000) {
                    binding.searchViewHolder.setVisibility(View.GONE);
                }
                if (binding.nestedScrollView.getScrollY() < 20) {
                    binding.searchViewHolder.setVisibility(View.VISIBLE);
                }

            }
        });*/



        return view;
    }


    private void AddCategoryData() {
        categoryModelArrayList.add(new CategoryModel(
                "Tech", techImage, getResources().getColor(R.color.app_icon), 1)
        );

        categoryModelArrayList.add(new CategoryModel(
                "Clothing", clothingImage, getResources().getColor(R.color.app_icon), 2)
        );
        categoryModelArrayList.add(new CategoryModel(
                "Automotive", automotiveImage, getResources().getColor(R.color.app_icon), 3)
        );
        categoryModelArrayList.add(new CategoryModel(
                "Beauty", beautyImage, getResources().getColor(R.color.app_icon), 4)
        );


    }

    private void GotoSearchesActivity() {
        Intent intent = new Intent(getContext(), UserSearchesActivity.class);
        startActivity(intent);
    }

    private void AddSliderImages() {

        binding.imgOffersSlider.addData(new CarouselItem(imageUri1, "Slide Image 1"));
        binding.imgOffersSlider.addData(new CarouselItem(imageUri2, "Slide Image 2"));
        binding.imgOffersSlider.addData(new CarouselItem(imageUri3, "Slide Image 3"));
        binding.imgOffersSlider.addData(new CarouselItem(imageUri4, "Slide Image 4"));


    }

    private void AddAllProductsData() {

        fireStore.collection("Product")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

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