package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.OrderStatusActivity;
import com.grocery.groceryapp.databinding.BuyedProductsItemBinding;
import com.grocery.groceryapp.models.BuyedProductsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyedProductsAdapter extends RecyclerView.Adapter<BuyedProductsAdapter.ViewHolder>{

    Context context;
    ArrayList<BuyedProductsModel> buyedProductsModelArrayList;

    public BuyedProductsAdapter(Context context, ArrayList<BuyedProductsModel> buyedProductsModelArrayList) {
        this.context = context;
        this.buyedProductsModelArrayList = buyedProductsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.buyed_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BuyedProductsModel buyedProductsModel = buyedProductsModelArrayList.get(position);

        String uid = holder.auth.getCurrentUser().getUid();
        holder.userDocRef = holder.fireStore.collection("Users").document(uid);
        holder.buyedProductsCol = holder.userDocRef.collection("BuyedProducts");

        // Adding the Time and Date from TimeStamp
        holder.timeStamp = buyedProductsModel.getTimeStamp();

        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");


        Glide.with(context)
                .load(buyedProductsModel.getProductImage())
                .placeholder(R.drawable.loading_image)
                .into(holder.binding.imgProductImage);

        holder.binding.txtProductTitle.setText(buyedProductsModel.getProductTitle());
        holder.binding.txtTime.setText(timeFormatter.format(holder.timeStamp.toDate()));
        holder.binding.txtDate.setText(dateFormatter.format(holder.timeStamp.toDate()));
        holder.binding.txtProductPrice.setText(String.valueOf(buyedProductsModel.getProductPrice()));
        holder.binding.txtProductItems.setText(String.valueOf(buyedProductsModel.getProductItems()));
        holder.binding.txtOrderId.setText(String.valueOf(buyedProductsModel.getOrderId()));

        holder.sum = holder.sum + buyedProductsModel.getProductPrice() * buyedProductsModel.getProductItems();
        holder.binding.txtTotalPrice.setText(String.valueOf(holder.sum));

        holder.binding.allViewsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToOrderStatusActivityAndPassData(holder, buyedProductsModel);
            }
        });


    }

    @Override
    public int getItemCount() {
        return buyedProductsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        BuyedProductsItemBinding binding;
        FirebaseAuth auth;
        FirebaseFirestore fireStore;
        DocumentReference userDocRef;
        CollectionReference buyedProductsCol;
        double sum = 0.0;
        Timestamp timeStamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = BuyedProductsItemBinding.bind(itemView);

            auth = FirebaseAuth.getInstance();
            fireStore = FirebaseFirestore.getInstance();

        }

    }



    public void GoToOrderStatusActivityAndPassData(@NonNull ViewHolder holder, BuyedProductsModel buyedProductsModel) {

        Intent intent = new Intent(context, OrderStatusActivity.class);
        intent.putExtra("orderId", buyedProductsModel.getOrderId());
        intent.putExtra("orderStatus", buyedProductsModel.getOrderStatus());
        intent.putExtra("totalPrice", holder.sum);
        intent.putExtra("phoneNumber", buyedProductsModel.getPhoneNumber());
        intent.putExtra("address", buyedProductsModel.getAddress());
        intent.putExtra("fullName", buyedProductsModel.getFullName());
        intent.putExtra("productTitle", buyedProductsModel.getProductTitle());
        intent.putExtra("productImage", buyedProductsModel.getProductImage());
        intent.putExtra("productPrice", buyedProductsModel.getProductPrice());
        intent.putExtra("productItems", buyedProductsModel.getProductItems());
        intent.putExtra("key", buyedProductsModel.getKey());
        context.startActivity(intent);
        // Adding Slide Up Animation
        Animatoo.INSTANCE.animateSlideUp(context);

    }


}
