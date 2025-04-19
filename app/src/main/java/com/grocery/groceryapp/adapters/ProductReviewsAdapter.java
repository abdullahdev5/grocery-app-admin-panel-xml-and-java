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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.BigImagesOfProductReviewActivity;
import com.grocery.groceryapp.databinding.ProductReviewsItemBinding;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProductReviewsAdapter extends RecyclerView.Adapter<ProductReviewsAdapter.ViewHolder> {

    Context context;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;
    private int visibleItemCount = 1;
    String productKey;

    public ProductReviewsAdapter(Context context, ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList, String productKey) {
        this.context = context;
        this.toOrderReviewsModelArrayList = toOrderReviewsModelArrayList;
        this.productKey = productKey;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_reviews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ToOrderReviewsModel toOrderReviewsModel = toOrderReviewsModelArrayList.get(position);

        // Adding the Time and Date from TimeStamp
        holder.timeStamp = toOrderReviewsModel.getTimeStamp();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.binding.txtDate.setText(dateFormatter.format(holder.timeStamp.toDate()));

        // set the Description and rating
        holder.binding.txtProductReviewDescription.setText(toOrderReviewsModel.getOrderReviewDescription());
        holder.binding.txtOrderRating.setText(String.valueOf(toOrderReviewsModel.getOrderRating()));


        // Setting the Collection of product Review Images in the Product Collection
        holder.productCol = holder.fireStore.collection("Product");
        holder.productDocRef = holder.productCol.document(productKey);
        holder.productReviewsCol = holder.productDocRef.collection("ProductReviews");
        holder.productReviewsDocRef = holder.productReviewsCol.document(toOrderReviewsModel.getKey());
        holder.productReviewImagesCol = holder.productReviewsDocRef.collection("ProductReviewImages");

        // set the Recycler View of Product Review Images
        holder.productReviewImagesModelArrayList = new ArrayList<>();
        holder.binding.rvProductReviewImages.setHasFixedSize(true);
        holder.binding.rvProductReviewImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.productReviewImagesAdapter = new ProductReviewImagesAdapter(context, holder.productReviewImagesModelArrayList);
        holder.binding.rvProductReviewImages.setAdapter(holder.productReviewImagesAdapter);
        AddProductReviewImages(holder);

        Log.d("TAG", "onBindViewHolder: Fetching Images on images Adapter" + toOrderReviewsModel.getOrderReviewImage());


        holder.binding.allViewsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToBigImagesOfProductReviewActivityAndPassData(toOrderReviewsModel);
            }
        });



    }

    @Override
    public int getItemCount() {
        return Math.min(visibleItemCount, toOrderReviewsModelArrayList.size());
    }

    public void showMoreItems() {
        visibleItemCount = toOrderReviewsModelArrayList.size(); //Math.min(visibleItemCount + 1, toOrderReviewsModelArrayList.size());
        notifyDataSetChanged();
    }

    public void lessItems() {
        visibleItemCount = Math.min(visibleItemCount - toOrderReviewsModelArrayList.size() + 1, toOrderReviewsModelArrayList.size());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ProductReviewsItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference productDocRef, productReviewsDocRef;
        CollectionReference productCol, productReviewsCol, productReviewImagesCol;
        ArrayList<ToOrderReviewsModel> productReviewImagesModelArrayList;
        ProductReviewImagesAdapter productReviewImagesAdapter;
        Timestamp timeStamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductReviewsItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
        }

    }


    public void AddProductReviewImages(@NonNull ViewHolder holder) {

        holder.productReviewImagesCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void GoToBigImagesOfProductReviewActivityAndPassData(ToOrderReviewsModel toOrderReviewsModel) {

        Intent intent = new Intent(context, BigImagesOfProductReviewActivity.class);
        intent.putExtra("orderReviewDescription", toOrderReviewsModel.getOrderReviewDescription());
        intent.putExtra("orderRating", toOrderReviewsModel.getOrderRating());
        intent.putExtra("orderReviewImage", toOrderReviewsModel.getOrderReviewImage());
        intent.putExtra("key", toOrderReviewsModel.getKey());
        intent.putExtra("productKey", productKey);
        context.startActivity(intent);
        Animatoo.INSTANCE.animateSlideUp(context);

    }



}
