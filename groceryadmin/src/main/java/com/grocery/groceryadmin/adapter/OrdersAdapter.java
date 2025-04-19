package com.grocery.groceryadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryadmin.R;
import com.grocery.groceryadmin.activities.OrderStatusActivity;
import com.grocery.groceryadmin.databinding.OrdersItemBinding;
import com.grocery.groceryadmin.models.OrdersModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    Context context;
    ArrayList<OrdersModel> ordersModelArrayList;

    public OrdersAdapter(Context context, ArrayList<OrdersModel> ordersModelArrayList) {
        this.context = context;
        this.ordersModelArrayList = ordersModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orders_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrdersModel ordersModel = ordersModelArrayList.get(position);

        // Adding the Time and Date from TimeStamp
        holder.timeStamp = ordersModel.getTimeStamp();

        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        Glide.with(context)
                .load(ordersModel.getProductImage())
                .placeholder(R.drawable.loading_image)
                .into(holder.binding.imgProductImage);

        holder.binding.txtProductTitle.setText(ordersModel.getProductTitle());

        holder.binding.txtTime.setText(timeFormatter.format(holder.timeStamp.toDate()));
        holder.binding.txtDate.setText(dateFormatter.format(holder.timeStamp.toDate()));
        holder.binding.txtProductPrice.setText(String.valueOf(ordersModel.getProductPrice()));
        holder.binding.txtProductItems.setText(String.valueOf(ordersModel.getProductItems()));
        holder.binding.txtOrderId.setText(String.valueOf(ordersModel.getOrderId()));

        holder.sum = holder.sum + ordersModel.getProductPrice() * ordersModel.getProductItems();
        holder.binding.txtTotalPrice.setText(String.valueOf(holder.sum));

        holder.binding.allViewsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToOrderStatusActivityAndPassData(holder, ordersModel);
            }
        });



    }

    @Override
    public int getItemCount() {
        return ordersModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        OrdersItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference userDocRef;
        CollectionReference buyedProductsCol;
        double sum = 0.0;
        Timestamp timeStamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrdersItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

        }

    }


    public void GoToOrderStatusActivityAndPassData(@NonNull ViewHolder holder, OrdersModel ordersModel) {

        Intent intent = new Intent(context, OrderStatusActivity.class);
        intent.putExtra("orderId", ordersModel.getOrderId());
        intent.putExtra("orderStatus", ordersModel.getOrderStatus());
        intent.putExtra("totalPrice", holder.sum);
        intent.putExtra("phoneNumber", ordersModel.getPhoneNumber());
        intent.putExtra("address", ordersModel.getAddress());
        intent.putExtra("userName", ordersModel.getUserName());
        intent.putExtra("productTitle", ordersModel.getProductTitle());
        intent.putExtra("productImage", ordersModel.getProductImage());
        intent.putExtra("productPrice", ordersModel.getProductPrice());
        intent.putExtra("productItems", ordersModel.getProductItems());
        intent.putExtra("key", ordersModel.getKey());
        context.startActivity(intent);

    }





}
