package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.ProductDataActivity;
import com.grocery.groceryapp.databinding.WishListProductsItemBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ProductsModel;
import com.grocery.groceryapp.models.WishListProductsModel;

import java.util.ArrayList;

public class WishListProductsAdapter extends RecyclerView.Adapter<WishListProductsAdapter.ViewHolder> {

    Context context;
    ArrayList<WishListProductsModel> wishListProductsModelArrayList;

    public WishListProductsAdapter(Context context, ArrayList<WishListProductsModel> wishListProductsModelArrayList) {
        this.context = context;
        this.wishListProductsModelArrayList = wishListProductsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wish_list_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WishListProductsModel wishListProductsModel = wishListProductsModelArrayList.get(position);

        String uid = holder.auth.getCurrentUser().getUid();// userId
        holder.userDocRef = holder.fireStore.collection("Users").document(uid);

        // Cart Products
        holder.cartProductsCol = holder.userDocRef.collection("CartProducts");

        // Wishlist Products
        holder.wishListProductsCol = holder.userDocRef.collection("WishListProducts");

        // set the data on views
        Glide.with(context)
                .load(wishListProductsModel.getProductImage())
                .placeholder(context.getResources().getDrawable(R.drawable.loading_image))
                .into(holder.binding.imgProductImage);

        holder.binding.txtProductTitle.setText(wishListProductsModel.getProductTitle());
        holder.binding.txtProductPrice.setText(String.valueOf(wishListProductsModel.getProductPrice()));


        holder.binding.seeDetailsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToProductDataActivityAndPassData(wishListProductsModel);
            }
        });

        holder.binding.imgAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductToCart(holder, wishListProductsModel);
            }
        });

        holder.binding.imgDeleteWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete From WishList");
                builder.setMessage("Are you sure want to delete this Product");

                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteProductFromWishList(holder, wishListProductsModel);
                        dialog.dismiss();
                    }
                });

                builder.show();


            }
        });



    }

    @Override
    public int getItemCount() {
        return wishListProductsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        WishListProductsItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference userDocRef;
        CollectionReference cartProductsCol, wishListProductsCol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = WishListProductsItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

        }

    }

    public void GoToProductDataActivityAndPassData(WishListProductsModel wishListProductsModel) {
        Intent intent = new Intent(context, ProductDataActivity.class);
        intent.putExtra("productTitle", wishListProductsModel.getProductTitle());
        intent.putExtra("productPrice", wishListProductsModel.getProductPrice());
        intent.putExtra("availableProducts", wishListProductsModel.getAvailableProducts());
        intent.putExtra("productDescription", wishListProductsModel.getProductDescription());
        intent.putExtra("productCategory", wishListProductsModel.getProductCategory());
        intent.putExtra("productImage", wishListProductsModel.getProductImage());
        intent.putExtra("key", wishListProductsModel.getKey());
        context.startActivity(intent);
    }

    public void AddProductToCart(@NonNull ViewHolder holder, WishListProductsModel wishListProductsModel
    ) {

        long productItems = wishListProductsModel.getProductItems();
        productItems = 1;

        ProductsModel productsModel1 = new ProductsModel(

                wishListProductsModel.getProductTitle(), wishListProductsModel.getProductPrice(),
                wishListProductsModel.getAvailableProducts(), wishListProductsModel.getProductDescription(),
                wishListProductsModel.getProductCategory(), wishListProductsModel.getProductImage(),
                wishListProductsModel.getKey(), productItems

        );


        holder.cartProductsCol.document(wishListProductsModel.getKey())
                .set(productsModel1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
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

    public void DeleteProductFromWishList(@NonNull ViewHolder holder, WishListProductsModel wishListProductsModel
    ) {

        holder.wishListProductsCol.document(wishListProductsModel.getKey())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Deleted From Wish List Successfully", Toast.LENGTH_SHORT).show();
                    }
                });


    }



}
