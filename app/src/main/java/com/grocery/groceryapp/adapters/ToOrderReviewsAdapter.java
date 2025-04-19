package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.AddOrderReviewsActivity;
import com.grocery.groceryapp.databinding.ToOrderReviewsItemBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ToOrderReviewsAdapter extends RecyclerView.Adapter<ToOrderReviewsAdapter.ViewHolder> {

    Context context;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;


    public ToOrderReviewsAdapter(Context context, ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList) {
        this.context = context;
        this.toOrderReviewsModelArrayList = toOrderReviewsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.to_order_reviews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ToOrderReviewsModel toOrderReviewsModel = toOrderReviewsModelArrayList.get(position);


        holder.timeStamp = toOrderReviewsModel.getTimeStamp();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        String uid = holder.auth.getCurrentUser().getUid();
        holder.userDocRef = holder.fireStore.collection("Users").document(uid);
        holder.orderReviewsCol = holder.userDocRef.collection("OrderReviews");

        // RemoveProductWhenAddReview(holder, toOrderReviewsModel);


        Glide.with(context)
                .load(toOrderReviewsModel.getProductImage())
                .placeholder(R.drawable.loading_image)
                .into(holder.binding.imgProductImage);

        holder.binding.txtProductTitle.setText(toOrderReviewsModel.getProductTitle());
        holder.binding.txtDate.setText(dateFormatter.format(holder.timeStamp.toDate()));

        holder.binding.allViewsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToAddOrderReviewsActivityAddPassData(toOrderReviewsModel);
            }
        });




    }

    @Override
    public int getItemCount() {
        return toOrderReviewsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ToOrderReviewsItemBinding binding;
        Timestamp timeStamp;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference userDocRef;
        CollectionReference orderReviewsCol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ToOrderReviewsItemBinding.bind(itemView);

            auth = FirebaseAuth.getInstance();
            fireStore = FirebaseFirestore.getInstance();


        }

    }


    public void GoToAddOrderReviewsActivityAddPassData(ToOrderReviewsModel toOrderReviewsModel) {

        Intent intent = new Intent(context, AddOrderReviewsActivity.class);
        intent.putExtra("productTitle", toOrderReviewsModel.getProductTitle());
        intent.putExtra("productPrice", toOrderReviewsModel.getProductPrice());
        intent.putExtra("availableProducts", toOrderReviewsModel.getAvailableProducts());
        intent.putExtra("productDescription", toOrderReviewsModel.getProductDescription());
        intent.putExtra("productCategory", toOrderReviewsModel.getProductCategory());
        intent.putExtra("productImage", toOrderReviewsModel.getProductImage());
        intent.putExtra("key", toOrderReviewsModel.getKey());
        context.startActivity(intent);

    }


}
