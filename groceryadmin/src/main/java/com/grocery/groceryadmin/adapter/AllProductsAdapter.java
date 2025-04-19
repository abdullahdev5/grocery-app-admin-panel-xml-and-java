package com.grocery.groceryadmin.adapter;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryadmin.R;
import com.grocery.groceryadmin.databinding.AllProductsItemBinding;
import com.grocery.groceryadmin.models.ProductsModel;

import java.util.ArrayList;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductsModel> productsModelArrayList;


    public AllProductsAdapter(Context context, ArrayList<ProductsModel> productsModelArrayList) {
        this.context = context;
        this.productsModelArrayList = productsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductsModel productsModel = productsModelArrayList.get(position);

        Glide.with(context)
                .load(productsModel.getProductImage())
                .placeholder(context.getResources().getDrawable(R.drawable.loading_image))
                .into(holder.binding.imgProduct);

        holder.binding.txtProductTittle.setText(productsModel.getProductTitle());
        holder.binding.txtProductPrice.setText(String.valueOf(productsModel.getProductPrice()));

        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are You Sure you want to Delete this Product?");

                // set teh Negative Button on A Alert Dialog
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // set teh Positive Button on A Alert Dialog
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveProductData(holder, productsModel);
                        dialog.dismiss();
                    }
                });

                builder.show();



            }
        });


    }

    @Override
    public int getItemCount() {
        return productsModelArrayList.size();
    }

    public void RemoveProductData(@NonNull ViewHolder holder, ProductsModel productsModel) {

        String productKey = productsModel.getKey();

        holder.productDocRef = holder.fireStore.collection("Product").document(productKey);

        holder.productDocRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
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
        DocumentReference productDocRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = AllProductsItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();

        }
    }


}
