package com.grocery.groceryapp.adapters;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
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
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.MainActivity;
import com.grocery.groceryapp.activities.ProductDataActivity;
import com.grocery.groceryapp.databinding.AllProductsItemBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ProductsModel;
import com.grocery.groceryapp.models.WishListProductsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductsModel> productsModelArrayList;
    public AllProductsAdapter(Context context, ArrayList<ProductsModel> productsModelArrayList) {
        this.context = context;
        this.productsModelArrayList = productsModelArrayList;
    }

    @NonNull
    @Override
    public AllProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProductsAdapter.ViewHolder holder, int position) {

        ProductsModel productsModel = productsModelArrayList.get(position);

        String uid = holder.auth.getCurrentUser().getUid();
        holder.userDocRef = holder.fireStore.collection("Users").document(uid);
        holder.wishListProductsCol = holder.userDocRef.collection("WishListProducts");

        Glide.with(context)
                .load(productsModel.getProductImage())
                .placeholder(context.getResources().getDrawable(R.drawable.loading_image))
                .into(holder.binding.imgProduct);

        holder.binding.txtProductTitle.setText(productsModel.getProductTitle());
        holder.binding.txtProductPrice.setText(String.valueOf(productsModel.getProductPrice()));



        holder.binding.productHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToProductDataActivityAndPassData(productsModel);
            }
        });

        holder.binding.imgAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductToCart(holder, productsModel);
            }
        });


        GetWishListIconStatus(holder, productsModel, holder.wishListProductsCol);

        holder.binding.imgWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  holder.isWishList = !holder.isWishList;

                    if (holder.isWishList) {*/

                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.wishlist_anim);
                        holder.binding.imgWishList.startAnimation(animation);
                        holder.binding.imgWishList.setImageResource(R.drawable.liked_wishlist_icon);
                        AddWishListProducts(productsModel, holder.wishListProductsCol);

                   /* } else {

                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.wishlist_anim);
                        holder.binding.imgWishList.startAnimation(animation);
                        holder.binding.imgWishList.setImageResource(R.drawable.wishlist_icon);
                        RemoveWishListProducts(productsModel, holder.wishListProductsCol);

                    }*/


            }
        });




    }

    @Override
    public int getItemCount() {
        return productsModelArrayList.size();
    }

    public void AddWishListProducts(ProductsModel productsModel,
                                    CollectionReference wishListProductsCol) {


        ProductsModel productsModel2 = new ProductsModel(

                productsModel.getProductTitle(), productsModel.getProductPrice(),
                productsModel.getAvailableProducts(), productsModel.getProductDescription(),
                productsModel.getProductCategory(), productsModel.getProductImage(),
                productsModel.getKey()

        );


        wishListProductsCol.document(productsModel.getKey())
                .set(productsModel2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeNotificationOfAddToWishList(productsModel.getProductTitle());
                        Toast.makeText(context, "Added to Wish List", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Map<String , Object> addTimeStamp = new HashMap<>();
        addTimeStamp.put("timeStamp", FieldValue.serverTimestamp());

        wishListProductsCol.document(productsModel.getKey())
                .update(addTimeStamp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void makeNotificationOfAddToCart(String productTitle) {

        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(productTitle)
                .setContentText("This Product Added to Your Cart List")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_MUTABLE);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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


    public void makeNotificationOfAddToWishList(String productTitle) {

        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(productTitle)
                .setContentText("This Product Added to Your Wish List")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_MUTABLE);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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


    public void GetWishListIconStatus(AllProductsAdapter.ViewHolder holder, ProductsModel productsModel,
                                      CollectionReference wishListProductsCol) {

        wishListProductsCol.document(productsModel.getKey())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value.exists()) {

                            holder.binding.imgWishList.setImageResource(R.drawable.liked_wishlist_icon);

                        } else {
                            holder.binding.imgWishList.setImageResource(R.drawable.wishlist_icon);
                        }



                    }
                });


    }


    public void GoToProductDataActivityAndPassData(ProductsModel productsModel) {

        Intent intent = new Intent(context, ProductDataActivity.class);
        intent.putExtra("productTitle", productsModel.getProductTitle());
        intent.putExtra("productPrice", productsModel.getProductPrice());
        intent.putExtra("availableProducts", productsModel.getAvailableProducts());
        intent.putExtra("productDescription", productsModel.getProductDescription());
        intent.putExtra("productCategory", productsModel.getProductCategory());
        intent.putExtra("productImage", productsModel.getProductImage());
        intent.putExtra("key", productsModel.getKey());
        context.startActivity(intent);
    }


    public void SearchDataList(ArrayList<ProductsModel> searchList) {
        productsModelArrayList = searchList;
        notifyDataSetChanged();
    }

    public void AddProductToCart(@NonNull AllProductsAdapter.ViewHolder holder, ProductsModel productsModel
                                 ) {

        String uid = holder.auth.getCurrentUser().getUid();
        holder.userDocRef = holder.fireStore.collection("Users").document(uid);
        holder.cartProductsCol = holder.userDocRef.collection("CartProducts");

        long productItems = productsModel.getProductItems();
        productItems = 1;

        ProductsModel productsModel1 = new ProductsModel(

                productsModel.getProductTitle(), productsModel.getProductPrice(),
                productsModel.getAvailableProducts(), productsModel.getProductDescription(),
                productsModel.getProductCategory(), productsModel.getProductImage(),
                productsModel.getKey(), productItems

        );


        holder.cartProductsCol.document(productsModel.getKey())
                .set(productsModel1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeNotificationOfAddToCart(productsModel.getProductTitle());
                        Toast.makeText(context, "Added to Cart Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

         AllProductsItemBinding binding;
         FirebaseFirestore fireStore;
         FirebaseAuth auth;
        DocumentReference userDocRef;
        CollectionReference cartProductsCol, wishListProductsCol;
        Boolean isWishList = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AllProductsItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

        }
    }


}
