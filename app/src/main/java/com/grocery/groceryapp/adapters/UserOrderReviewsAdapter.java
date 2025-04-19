package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.AddOrderReviewsActivity;
import com.grocery.groceryapp.activities.ProductDataActivity;
import com.grocery.groceryapp.databinding.UserOrderReviewsItemBinding;
import com.grocery.groceryapp.models.CartProductsModel;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import org.checkerframework.checker.units.qual.N;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UserOrderReviewsAdapter extends RecyclerView.Adapter<UserOrderReviewsAdapter.ViewHolder> {

    Context context;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;


    public UserOrderReviewsAdapter(Context context, ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList) {
        this.context = context;
        this.toOrderReviewsModelArrayList = toOrderReviewsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_order_reviews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ToOrderReviewsModel toOrderReviewsModel = toOrderReviewsModelArrayList.get(position);

        // Setting the Collection of product Review Images in the Product Collection
        holder.userDocRef = holder.fireStore.collection("Users").document(holder.auth.getCurrentUser().getUid());
        holder.orderReviewsCol = holder.userDocRef.collection("OrderReviews");
        holder.orderReviewsDocRef = holder.orderReviewsCol.document(toOrderReviewsModel.getProductKey());
        holder.orderReviewImagesCol = holder.orderReviewsDocRef.collection("OrderReviewImages");

        // set the Recycler View of Product Review Images
        holder.productReviewImagesModelArrayList = new ArrayList<>();
        holder.binding.rvProductReviewImages.setHasFixedSize(true);
        holder.binding.rvProductReviewImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.productReviewImagesAdapter = new ProductReviewImagesAdapter(context, holder.productReviewImagesModelArrayList);
        holder.binding.rvProductReviewImages.setAdapter(holder.productReviewImagesAdapter);
        AddOrderReviewImages(holder);


        // Adding the Date from TimeStamp
        holder.timeStamp = toOrderReviewsModel.getTimeStamp();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.binding.txtDate.setText(dateFormatter.format(holder.timeStamp.toDate()));

        holder.binding.txtOrderReviewDescription.setText(toOrderReviewsModel.getOrderReviewDescription());
        holder.binding.txtOrderRating.setText(String.valueOf(toOrderReviewsModel.getOrderRating()));

        holder.binding.allViewsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToProductDataActivityAndPassData(toOrderReviewsModel);
            }
        });


    }

    @Override
    public int getItemCount() {
        return toOrderReviewsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        UserOrderReviewsItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference userDocRef, orderReviewsDocRef;
        CollectionReference orderReviewsCol, orderReviewImagesCol;
        ArrayList<ToOrderReviewsModel> productReviewImagesModelArrayList;
        ProductReviewImagesAdapter productReviewImagesAdapter;
        Timestamp timeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserOrderReviewsItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
        }

    }


    public void GoToProductDataActivityAndPassData(ToOrderReviewsModel toOrderReviewsModel) {
        Intent intent = new Intent(context, ProductDataActivity.class);
        intent.putExtra("productTitle", toOrderReviewsModel.getProductTitle());
        intent.putExtra("productPrice", toOrderReviewsModel.getProductPrice());
        intent.putExtra("availableProducts", toOrderReviewsModel.getAvailableProducts());
        intent.putExtra("productDescription", toOrderReviewsModel.getProductDescription());
        intent.putExtra("productCategory", toOrderReviewsModel.getProductCategory());
        intent.putExtra("productImage", toOrderReviewsModel.getProductImage());
        intent.putExtra("key", toOrderReviewsModel.getProductKey());
        context.startActivity(intent);
    }

    public void AddOrderReviewImages(@NonNull ViewHolder holder) {

        holder.orderReviewImagesCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d("TAG", "onEvent: Error Fetching Images" + error);
                }

                for (DocumentChange document : value.getDocumentChanges()) {

                    if (document.getType() == DocumentChange.Type.ADDED) {

                        Log.d("TAG", "onEvent: Adding the Images Successfully");
                        holder.productReviewImagesModelArrayList.add(document.getDocument().toObject(ToOrderReviewsModel.class));

                    }

                    holder.productReviewImagesAdapter.notifyDataSetChanged();

                }


            }
        });


    }



}
