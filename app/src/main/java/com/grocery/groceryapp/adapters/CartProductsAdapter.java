package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.ProductDataActivity;
import com.grocery.groceryapp.activities.SignUpActivity;
import com.grocery.groceryapp.databinding.CartProductsItemBinding;
import com.grocery.groceryapp.models.CartProductsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.ViewHolder> {

    Context context;
    ArrayList<CartProductsModel> cartProductsModelArrayList;
    TextView txtSubTotal; // this is for set the sum on the cart fragment


    public CartProductsAdapter(Context context, ArrayList<CartProductsModel> cartProductsModelArrayList, TextView txtSubTotal) {
        this.context = context;
        this.cartProductsModelArrayList = cartProductsModelArrayList;
        this.txtSubTotal = txtSubTotal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartProductsModel cartProductsModel = cartProductsModelArrayList.get(position);

        Glide.with(context)
                        .load(cartProductsModel.getProductImage())
                        .placeholder(context.getResources().getDrawable(R.drawable.loading_image))
                        .into(holder.binding.imgProductImage);

        holder.binding.txtProductTitle.setText(cartProductsModel.getProductTitle());
        holder.binding.txtAvailableProduct.setText(String.valueOf(cartProductsModel.getAvailableProducts()));
        holder.binding.txtProductPrice.setText(String.valueOf(cartProductsModel.getProductPrice()));
        holder.binding.txtProductItems.setText(String.valueOf(cartProductsModel.getProductItems()));

        String userId = holder.auth.getCurrentUser().getUid();
        holder.userDocRef = holder.fireStore.collection("Users").document(userId);
        holder.cartProductsCol = holder.userDocRef.collection("CartProducts");

        incrementSubTotal(); // show the default product price like permanently

        holder.binding.seeDetailsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToProductDataActivityAndPassData(cartProductsModel); // By Clicking the See Details Holder
            }
        });


        holder.binding.imgPlusProductNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long productItems = Long.parseLong(holder.binding.txtProductItems.getText().toString());

                long availableProduct = cartProductsModel.getAvailableProducts();


                if (productItems < availableProduct) {

                        productItems++;
                        notifyDataSetChanged();
                        holder.binding.txtProductItems.setText(String.valueOf(productItems));
                        cartProductsModel.setProductItems(productItems);
                        UpdateProductItemOnFireStore(productItems, cartProductsModel, holder);
                        incrementSubTotal();

                } else {
                    Toast.makeText(context, "This Product have only " + cartProductsModel.getAvailableProducts() + " Products", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.binding.imgMinusProductNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long productItems = Long.parseLong(holder.binding.txtProductItems.getText().toString());

                if (productItems > 1) {

                        productItems--;
                        notifyDataSetChanged();
                        holder.binding.txtProductItems.setText(String.valueOf(productItems));
                        cartProductsModel.setProductItems(productItems);
                        decrementSubTotal();
                        UpdateProductItemOnFireStore(productItems, cartProductsModel, holder);

                }

            }
        });

        holder.binding.imgDeleteProductFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete From Cart");
                builder.setMessage("Are you sure want to delete this item");

                builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteProductFromCart(holder, cartProductsModel);
                        dialog.dismiss();
                    }
                });

                builder.show();


            }
        });



    }

    @Override
    public int getItemCount() {
        return cartProductsModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CartProductsItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference userDocRef;
        CollectionReference cartProductsCol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartProductsItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

        }


    }

    public void incrementSubTotal() {

        double subTotal = 0.0;
        int i;
        for (i = 0 ; i< cartProductsModelArrayList.size() ; i++)
            subTotal = subTotal + (cartProductsModelArrayList.get(i).getProductPrice() * cartProductsModelArrayList.get(i).getProductItems());


        txtSubTotal.setText(String.valueOf(subTotal));

    }

    public void decrementSubTotal() {

        double subTotal = 0.0;
        int i;
        for (i = 0 ; i< cartProductsModelArrayList.size() ; i++)
            subTotal = subTotal + (cartProductsModelArrayList.get(i).getProductPrice() / cartProductsModelArrayList.get(i).getProductItems());


        txtSubTotal.setText(String.valueOf(subTotal));

    }

    public void GoToProductDataActivityAndPassData(CartProductsModel cartProductsModel) {
        Intent intent = new Intent(context, ProductDataActivity.class);
        intent.putExtra("productTitle", cartProductsModel.getProductTitle());
        intent.putExtra("productPrice", cartProductsModel.getProductPrice());
        intent.putExtra("availableProducts", cartProductsModel.getAvailableProducts());
        intent.putExtra("productDescription", cartProductsModel.getProductDescription());
        intent.putExtra("productCategory", cartProductsModel.getProductCategory());
        intent.putExtra("productImage", cartProductsModel.getProductImage());
        intent.putExtra("key", cartProductsModel.getKey());
        context.startActivity(intent);
    }

    public void DeleteProductFromCart(

            @NonNull ViewHolder holder, CartProductsModel cartProductsModel

    ) {

        String cartProductsKey = cartProductsModel.getKey();


        holder.cartProductsCol.document(cartProductsKey)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Deleted from Cart Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void UpdateProductItemOnFireStore(

            long productItems, CartProductsModel cartProductsModel,
            @NonNull ViewHolder holder

    ) {

        String cartProductsKey = cartProductsModel.getKey();


        holder.cartProductsCol.document(cartProductsKey)
                .update("productItems", productItems)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });


    }



}