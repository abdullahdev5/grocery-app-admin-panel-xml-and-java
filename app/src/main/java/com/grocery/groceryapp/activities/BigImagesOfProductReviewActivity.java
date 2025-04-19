package com.grocery.groceryapp.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityBigImagesOfProductReviewBinding;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

public class BigImagesOfProductReviewActivity extends AppCompatActivity {

    ActivityBigImagesOfProductReviewBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference productDocRef, productReviewsDocRef;
    CollectionReference productCol, productReviewsCol, productReviewImagesCol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBigImagesOfProductReviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initCloudFireStoreSubCollections();


        AddProductReviewImages();



        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.INSTANCE.animateInAndOut(BigImagesOfProductReviewActivity.this);
            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateInAndOut(BigImagesOfProductReviewActivity.this);
    }


    private void initCloudFireStoreSubCollections() {

        // Setting the Collection of product Review Images in the Product Collection
        productCol = fireStore.collection("Product");
        productDocRef = productCol.document(getIntent().getStringExtra("productKey"));
        productReviewsCol = productDocRef.collection("ProductReviews");
        productReviewsDocRef = productReviewsCol.document(getIntent().getStringExtra("key"));
        productReviewImagesCol = productReviewsDocRef.collection("ProductReviewImages");

    }

    private void AddProductReviewImages() {

        AddProductReviewDescriptionAndRating();

        productReviewImagesCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.ADDED) {

                        binding.productReviewImage.addData(
                                new CarouselItem(document.getDocument().getString("orderReviewImage"),
                                        "productReviewImages"));

                    }

                }

            }
        });

    }

    private void AddProductReviewDescriptionAndRating() {
        binding.txtProductReviewDescription.setText(getIntent().getStringExtra("orderReviewDescription"));
        binding.txtProductRating.setText(String.valueOf(getIntent().getLongExtra("orderRating", 0)));
    }




}