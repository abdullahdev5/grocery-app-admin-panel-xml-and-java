package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ProductReviewImagesItemBinding;
import com.grocery.groceryapp.models.ToOrderReviewsModel;

import java.util.ArrayList;

public class ProductReviewImagesAdapter extends RecyclerView.Adapter<ProductReviewImagesAdapter.ViewHolder> {

    Context context;
    ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList;


    public ProductReviewImagesAdapter(Context context, ArrayList<ToOrderReviewsModel> toOrderReviewsModelArrayList) {
        this.context = context;
        this.toOrderReviewsModelArrayList = toOrderReviewsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_review_images_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ToOrderReviewsModel toOrderReviewsModel = toOrderReviewsModelArrayList.get(position);

        Glide.with(context)
                .load(toOrderReviewsModel.getOrderReviewImage())
                .placeholder(context.getResources().getDrawable(R.drawable.loading_image))
                .into(holder.binding.imgProductReviewImage);

        Log.d("TAG", "onBindViewHolder: Fetching Images on images Adapter" + toOrderReviewsModel.getOrderReviewImage());



    }

    @Override
    public int getItemCount() {
        return toOrderReviewsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ProductReviewImagesItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProductReviewImagesItemBinding.bind(itemView);

        }

    }


}
