package com.grocery.groceryapp.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityAddOrderReviewsBinding;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddOrderReviewsActivity extends AppCompatActivity {

    ActivityAddOrderReviewsBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;
    DocumentReference userDocRef, productDocRef, productReviewDocRef, orderReviewDocRef;
    CollectionReference productReviewsCol, productReviewImagesCol, orderReviewsCol, orderReviewImagesCol, productCol;
    // orderReviewsCol is that collection added on the users doc.
    private final int GALLERY_REQ_CODE = 8410978;
    private Uri imageFileUri;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddOrderReviewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference();

        initCloudFireStoreSubCollections();
        initOrderReviewDescriptionTextCount();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Your Review....");
        progressDialog.setCancelable(false);

        Log.d("TAG", "onCreate: productKey: " + getIntent().getStringExtra("key"));


        CheckRatingStatus();
        AddPassingOrderDetail();


        binding.addPhotosHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (
                        binding.edtDescription.getText().toString().isEmpty() || binding.ratingBar.getRating() == 0
                        || imageFileUri == null
                ) {
                    Toast.makeText(AddOrderReviewsActivity.this, "Please Add All Review Information First", Toast.LENGTH_SHORT).show();

                } else {

                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            Toast.makeText(AddOrderReviewsActivity.this, "Your Review is Added", Toast.LENGTH_SHORT).show();
                        }
                    }, 3000);
                }

            }
        });





    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY_REQ_CODE) {

                imageFileUri = data.getData();

                binding.imgGallery.setBackground(null);

                ShowOrderReviewImages(imageFileUri);
                StoreOrderReviewImages(imageFileUri);


            }


        }


    }

    private void initCloudFireStoreSubCollections() {

        String uid = auth.getCurrentUser().getUid();

        userDocRef = fireStore.collection("Users").document(uid);

        productCol = fireStore.collection("Product");
        productDocRef = productCol.document(getIntent().getStringExtra("key"));
        productReviewsCol = productDocRef.collection("ProductReviews");

        productReviewDocRef = productReviewsCol.document(uid);

        productReviewImagesCol = productReviewDocRef.collection("ProductReviewImages");


        // for Order Reviews
        orderReviewsCol = userDocRef.collection("OrderReviews");
        orderReviewDocRef = orderReviewsCol.document(getIntent().getStringExtra("key"));

        orderReviewImagesCol = orderReviewDocRef.collection("OrderReviewImages");

    }

    private void initOrderReviewDescriptionTextCount() {

        binding.edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                binding.txtDescriptionLength.setText(String.valueOf(s.length()));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void CheckRatingStatus() {

        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (fromUser) {

                    if (rating != 0) {
                        binding.reviewsDataHolder.setVisibility(View.VISIBLE);
                    } else {
                        binding.reviewsDataHolder.setVisibility(View.GONE);
                    }

                    if (rating == 0) {
                        binding.txtRatingStatus.setText("Please Add Rating First");

                    } else if (rating == 1) {
                        binding.txtRatingStatus.setText("Terrible");

                    } else if (rating == 2) {
                        binding.txtRatingStatus.setText("Poor");

                    } else if (rating == 3) {
                        binding.txtRatingStatus.setText("Fair");

                    } else if (rating == 4) {
                        binding.txtRatingStatus.setText("Good");

                    } else if (rating == 5) {
                        binding.txtRatingStatus.setText("Excellent");

                    }


                }


            }
        });

    }

    private void AddPassingOrderDetail() {

        Glide.with(AddOrderReviewsActivity.this)
                .load(getIntent().getStringExtra("productImage"))
                .placeholder(R.drawable.loading_image)
                .into(binding.imgProductImage);

        binding.txtProductTitle.setText(getIntent().getStringExtra("productTitle"));

    }

    private void ShowOrderReviewImages(Uri imageFileUri) {

        binding.imgOrderReviewImage.addData(new CarouselItem(String.valueOf(imageFileUri), "User Order Review Images"));

    }

    private void StoreOrderReviewImages(Uri imageFileUri) {

        String imageId = UUID.randomUUID().toString();

        StorageReference ref = storageReference.child("OrderReviewImages").child(imageId);

        ref.putFile(imageFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        StoreReviewsInProductCol(uri, imageId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddOrderReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddOrderReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void StoreReviewsInProductCol(Uri uri, String imageId) {

        ToOrderReviewsModel toOrderReviewsModel = new ToOrderReviewsModel(
                getIntent().getStringExtra("productTitle"),
                getIntent().getStringExtra("productDescription"),
                getIntent().getStringExtra("productCategory"),
                getIntent().getStringExtra("productImage"),
                auth.getCurrentUser().getUid(), getIntent().getStringExtra("orderStatus"),
                getIntent().getStringExtra("fullName"), uri.toString(),
                binding.edtDescription.getText().toString(),
                getIntent().getLongExtra("productItems", 0),
                getIntent().getLongExtra("productPrice", 0),
                getIntent().getLongExtra("availableProducts", 0),
                getIntent().getLongExtra("orderId", 0),
                Math.round(binding.ratingBar.getRating()), Timestamp.now()

        );


        productReviewDocRef
                .set(toOrderReviewsModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String, Object> addReviewImages = new HashMap<>();
                        addReviewImages.put("orderReviewImage", uri.toString());

                        productReviewImagesCol.document(imageId)
                                .set(addReviewImages)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        StoreReviewsInOrdersCol(imageId, uri);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddOrderReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddOrderReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void StoreReviewsInOrdersCol(String imageId, Uri uri) {

        ToOrderReviewsModel toOrderReviewsModel = new ToOrderReviewsModel(
                getIntent().getStringExtra("productTitle"),
                getIntent().getStringExtra("productDescription"),
                getIntent().getStringExtra("productCategory"),
                getIntent().getStringExtra("productImage"),
                Math.round(binding.ratingBar.getRating()), getIntent().getStringExtra("orderStatus"),
                getIntent().getStringExtra("fullName"), uri.toString(),
                binding.edtDescription.getText().toString(),
                getIntent().getLongExtra("productItems", 0),
                getIntent().getLongExtra("productPrice", 0),
                getIntent().getLongExtra("availableProducts", 0),
                getIntent().getLongExtra("orderId", 0),
                getIntent().getStringExtra("key"), Timestamp.now()

        );


        orderReviewDocRef.set(toOrderReviewsModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Map<String, Object> addReviewImages = new HashMap<>();
                        addReviewImages.put("orderReviewImage", uri.toString());

                        orderReviewImagesCol.document(imageId)
                                .set(addReviewImages)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddOrderReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddOrderReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }



}