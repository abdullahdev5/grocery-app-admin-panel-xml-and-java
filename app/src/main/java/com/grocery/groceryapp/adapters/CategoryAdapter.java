package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.SearchDataActivity;
import com.grocery.groceryapp.databinding.CategoryItemBinding;
import com.grocery.groceryapp.models.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<CategoryModel> categoryModelArrayList;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModelArrayList) {
        this.context = context;
        this.categoryModelArrayList = categoryModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel categoryModel = categoryModelArrayList.get(position);

        Glide.with(context)
                .load(categoryModel.getIcon())
                .placeholder(context.getResources().getDrawable(R.drawable.loading_image))
                .into(holder.binding.imgCategoryImage);



        holder.binding.imgCategoryImage.setBorderColor(categoryModel.getColor());


        holder.binding.txtCategoryName.setText(categoryModel.getName());

        holder.binding.categoryHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToSearchDataActivityAndPassCategoryText(categoryModel);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    public void GoToSearchDataActivityAndPassCategoryText(CategoryModel categoryModel) {
        Intent intent = new Intent(context, SearchDataActivity.class);
        intent.putExtra("query", categoryModel.getName());
        context.startActivity(intent);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CategoryItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CategoryItemBinding.bind(itemView);

        }



    }



}