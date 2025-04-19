package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.BuyingProductsItemBinding;
import com.grocery.groceryapp.models.BuyingProductsModel;

import java.util.ArrayList;

public class BuyingProductsAdapter extends RecyclerView.Adapter<BuyingProductsAdapter.ViewHolder> {


    Context context;
    ArrayList<BuyingProductsModel> buyingProductsModelArrayList;

    public BuyingProductsAdapter(Context context, ArrayList<BuyingProductsModel> buyingProductsModelArrayList) {
        this.context = context;
        this.buyingProductsModelArrayList = buyingProductsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.buying_products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BuyingProductsModel buyingProductsModel = buyingProductsModelArrayList.get(position);

        Glide.with(context)
                .load(buyingProductsModel.getProductImage())
                .placeholder(R.drawable.loading_image)
                .into(holder.binding.imgProductImage);


        holder.binding.txtProductTitle.setText(buyingProductsModel.getProductTitle());
        holder.binding.txtProductPrice.setText(String.valueOf(buyingProductsModel.getProductPrice()));
        holder.binding.txtProductItems.setText(String.valueOf(buyingProductsModel.getProductItems()));

    }

    @Override
    public int getItemCount() {
        return buyingProductsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        BuyingProductsItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = BuyingProductsItemBinding.bind(itemView);

        }

    }

}
