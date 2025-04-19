package com.grocery.groceryadmin.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grocery.groceryadmin.R;
import com.grocery.groceryadmin.databinding.ActivityGetProductImagesBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetProductImagesActivity extends AppCompatActivity {

    ActivityGetProductImagesBinding binding;

    FirebaseStorage storage;
    FirebaseFirestore fireStore;
    StorageReference storageReference;
    Uri productImageUri;
    ArrayList<SlideModel> slideModelArrayList;
    DocumentReference productsDocRef;
    CollectionReference productImagesCol;
    private final int GALLERY_REQ_CODE = 2866;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGetProductImagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        slideModelArrayList = new ArrayList<>();

        Intent intent = getIntent();
        String productTitle = intent.getStringExtra("productTitle");
        long productPrice = intent.getLongExtra("productPrice", 0);
        long availableProducts = intent.getLongExtra("availableProducts", 0);
        String productDescription = intent.getStringExtra("productDescription");
        String productCategory = intent.getStringExtra("productCategory");
        String productKey = intent.getStringExtra("key");
        String shortProductName = intent.getStringExtra("shortProductName");

        // doc ref and col
        productsDocRef = fireStore.collection("Product").document(productKey);
        productImagesCol = productsDocRef.collection("ProductImages");


        binding.imgProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGalleryForProductImage();
            }
        });

        binding.addProductImageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGalleryForProductImage();
            }
        });

        binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckValidationAndStoreData(
                        productTitle, productPrice, availableProducts,
                        productDescription, productCategory, productKey, shortProductName
                );



            }
        });

        binding.btnGoPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



    private void StoreProductData(
            String productTitle, long productPrice, long availableProducts, String productDescription,
            String productCategory,String key, String shortProductName

    ) {


        Map<String , Object> productsData = new HashMap<>();
        productsData.put("productTitle", productTitle);
        productsData.put("productPrice", productPrice);
        productsData.put("availableProducts", availableProducts);
        productsData.put("productDescription", productDescription);
        productsData.put("productCategory", productCategory);

        productsData.put("timeStamp", FieldValue.serverTimestamp());
        productsData.put("key", key);
        productsData.put("shortProductName", shortProductName);

        fireStore.collection("Product")
                .document(key)
                .set(productsData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(GetProductImagesActivity.this, "Added", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GetProductImagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void CheckValidationAndStoreData(
            String productTitle, long productPrice, long availableProducts, String productDescription,
            String productCategory, String key, String shortProductName

    ) {

        if (productImageUri != null) {
            StoreProductData(productTitle, productPrice, availableProducts, productDescription , productCategory, key, shortProductName);


        } else {

            Toast.makeText(this, "Minimum 1 Product Image Required", Toast.LENGTH_SHORT).show();

        }


    }

    private void OpenGalleryForProductImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQ_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY_REQ_CODE) {

                productImageUri = data.getData();

                binding.imgAddProduct.setVisibility(View.GONE);

                    // ShowProductImages
                    ShowProductImages(productImageUri);
                StoreProductImages(productImageUri);

            }

        }

    }


    private void StoreProductImages(Uri productImages) {

        String imageId = UUID.randomUUID().toString();

        StorageReference ref = storageReference.child("ProductImages/" + imageId);
        ref.putFile(productImages)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ref.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Toast.makeText(GetProductImagesActivity.this, "Url Download", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> productImage = new HashMap<>();
                                    productImage.put("productImage", uri.toString());

                                    productsDocRef.update(productImage)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(GetProductImagesActivity.this, "One Image Also Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    productImage.put("key", imageId);

                                    productImagesCol.document(imageId)
                                                    .set(productImage)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(GetProductImagesActivity.this,
                                                                            "Product Images Collection is Created",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GetProductImagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GetProductImagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



}



   private void ShowProductImages(Uri productImageUri) {

       slideModelArrayList.add(new SlideModel(productImageUri.toString(), ScaleTypes.FIT));
       binding.imgProductImage.setImageList(slideModelArrayList, ScaleTypes.FIT);

    }




}