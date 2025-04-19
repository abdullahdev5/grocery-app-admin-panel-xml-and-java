package com.grocery.groceryapp.activities;

import android.animation.Animator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
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
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.adapters.AllProductsAdapter;
import com.grocery.groceryapp.adapters.ProductReviewsAdapter;
import com.grocery.groceryapp.databinding.ActivityProductDataBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ProductsModel;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDataActivity extends AppCompatActivity {

    ActivityProductDataBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference productDocRef, userDocRef;
    CollectionReference productImagesCol, cartProductsCol, productReviewsCol, wishListProductsCol;
    ArrayList<ProductsModel> productsModelArrayList;
    AllProductsAdapter allProductsAdapter;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;
    ProductReviewsAdapter productReviewsAdapter;
    private long productItems = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        // for Adding Product with same Category
        productsModelArrayList = new ArrayList<>();
        binding.rvProductDataByCategory.setHasFixedSize(true);
        binding.rvProductDataByCategory.setLayoutManager(new GridLayoutManager(this, 2));
        allProductsAdapter = new AllProductsAdapter(this, productsModelArrayList);
        binding.rvProductDataByCategory.setAdapter(allProductsAdapter);


        Intent intent = getIntent();
        String productTitle = intent.getStringExtra("productTitle");
        long productPrice = intent.getLongExtra("productPrice", 0);
        long availableProducts = intent.getLongExtra("availableProducts", 0);
        String productDescription = intent.getStringExtra("productDescription");
        String productCategory = intent.getStringExtra("productCategory");
        String productImage = intent.getStringExtra("productImage");
        String productKey = intent.getStringExtra("key");


        // for Product Reviews
        toOrderReviewsModelArrayList = new ArrayList<>();
        binding.rvProductReviews.setHasFixedSize(true);
        binding.rvProductReviews.setLayoutManager(new LinearLayoutManager(this));
        productReviewsAdapter = new ProductReviewsAdapter(this, toOrderReviewsModelArrayList, productKey);
        binding.rvProductReviews.setAdapter(productReviewsAdapter);


        // for Shoeing Product Images
        productDocRef = fireStore.collection("Product").document(productKey);
        productImagesCol = productDocRef.collection("ProductImages");

        // for Product Reviews Collection
        productReviewsCol = productDocRef.collection("ProductReviews");

        // for Adding to Cart
        String uid = auth.getCurrentUser().getUid();
        userDocRef = fireStore.collection("Users").document(uid);
        cartProductsCol = userDocRef.collection("CartProducts");

        // for wishList Products
        wishListProductsCol = userDocRef.collection("WishListProducts");


        binding.txtProductTitle.setText(productTitle);
        binding.txtAvailableProduct.setText(String.valueOf(availableProducts));
        binding.txtProductPrice.setText(String.valueOf(productPrice));
        binding.txtProductDescription.setText(productDescription);

        GetProductImagesAndShow();

        AddDataByCategory(productCategory);

        AddProductReviews();

        CheckReviewHaveOnThatProductAndShowingTheViewsAndHiding();

        GetWishListIconStatus(productKey);


        binding.txtSeeMoreProductReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productReviewsAdapter.getItemCount() > 1) {
                    binding.txtSeeMoreProductReviews.setText("see all");
                    productReviewsAdapter.lessItems();
                } else {
                    binding.txtSeeMoreProductReviews.setText("less all");
                    productReviewsAdapter.showMoreItems();
                }


            }
        });

        binding.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductToCart(
                        productTitle, productPrice, availableProducts, productDescription,
                        productCategory, productImage, productKey
                );
            }
        });


        binding.searchViewHolderMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), UserSearchesActivity.class);
                startActivity(intent1);
            }
        });

        binding.wishListsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wishlist_anim);
                binding.wishListsHolder.startAnimation(animation);
                binding.imgWishList.setImageResource(R.drawable.liked_wishlist_icon);

                AddWishListProducts(
                        productTitle, productPrice, availableProducts, productDescription,
                        productCategory, productImage, productKey
                );
            }
        });

        binding.wishListsHolder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wishlist_anim);
                binding.wishListsHolder2.startAnimation(animation);
                binding.imgWishList2.setImageResource(R.drawable.liked_wishlist_icon);

                AddWishListProducts(
                        productTitle, productPrice, availableProducts, productDescription,
                        productCategory, productImage, productKey
                );
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > 50) {

                    binding.wishListsHolder2.setVisibility(View.VISIBLE);
                    binding.wishListsHolder.setVisibility(View.GONE);

                }
                if (scrollY < 50) {

                    binding.wishListsHolder2.setVisibility(View.GONE);
                    binding.wishListsHolder.setVisibility(View.VISIBLE);
                }

            }
        });



    }


    private void CheckReviewHaveOnThatProductAndShowingTheViewsAndHiding() {

        Query query = productReviewsCol.count().getQuery();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int count = value.getDocuments().size();

                if (count == 0) {

                    binding.txtSeeMoreProductReviews.setVisibility(View.GONE);
                    binding.txtNoProductReviews.setVisibility(View.VISIBLE);

                } else {

                    binding.txtSeeMoreProductReviews.setVisibility(View.VISIBLE);
                    binding.txtNoProductReviews.setVisibility(View.GONE);

                }


            }
        });



    }

    private void AddProductReviews() {

        productReviewsCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(ProductDataActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }

                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.ADDED) {

                        toOrderReviewsModelArrayList.add(document.getDocument().toObject(ToOrderReviewsModel.class));

                    }

                    productReviewsAdapter.notifyDataSetChanged();

                }




            }
        });


    }

    private void makeNotification(String productTitle) {

        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(productTitle)
                .setContentText("This Product Added to Your Cart List")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

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



    private void AddProductToCart(
            String productTitle, long productPrice, long availableProducts,
            String productDescription, String productCategory, String productImage, String productKey
            ) {

        CartProductsModel cartProducts = new CartProductsModel(

                productTitle, productPrice, availableProducts, productDescription, productCategory,
                productImage, productKey, productItems

        );

        cartProductsCol.document(productKey)
                .set(cartProducts)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeNotification(productTitle);
                        Toast.makeText(ProductDataActivity.this, "Added to Cart Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void GetProductImagesAndShow() {

        productImagesCol
                .orderBy("key", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                binding.imgProductImages.addData(new CarouselItem(document.getString("productImage"),
                                        "productImages"));

                            }


                        } else {
                            Toast.makeText(ProductDataActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }



    private void AddDataByCategory(String productCategory) {

        fireStore.collection("Product")
                .whereEqualTo("productCategory", productCategory)
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

    public void AddWishListProducts(
            String productTitle, long productPrice, long availableProducts,
            String productDescription, String productCategory, String productImage, String productKey
    ) {


        ProductsModel productsModel2 = new ProductsModel(

                productTitle, productPrice, availableProducts, productDescription, productCategory,
                productImage, productKey

        );


        wishListProductsCol.document(productKey)
                .set(productsModel2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeNotificationOfAddToWishList(productTitle);
                        Toast.makeText(ProductDataActivity.this, "Added to Wish List", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Map<String , Object> addTimeStamp = new HashMap<>();
        addTimeStamp.put("timeStamp", FieldValue.serverTimestamp());

        wishListProductsCol.document(productKey)
                .update(addTimeStamp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void GetWishListIconStatus(String productKey) {

        wishListProductsCol.document(productKey)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value.exists()) {

                            binding.imgWishList.setImageResource(R.drawable.liked_wishlist_icon);
                            binding.imgWishList2.setImageResource(R.drawable.liked_wishlist_icon);

                        } else {
                            binding.imgWishList.setImageResource(R.drawable.wishlist_icon);
                            binding.imgWishList2.setImageResource(R.drawable.wishlist_icon);
                        }



                    }
                });


    }

    public void makeNotificationOfAddToWishList(String productTitle) {

        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(productTitle)
                .setContentText("This Product Added to Your Wish List")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

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




}