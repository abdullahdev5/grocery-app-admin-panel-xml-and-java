package com.grocery.groceryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.SearchDataActivity;
import com.grocery.groceryapp.databinding.UserSearchesItemBinding;
import com.grocery.groceryapp.models.UserSearchesModel;

import java.util.ArrayList;

public class UserSearchesAdapter extends RecyclerView.Adapter<UserSearchesAdapter.ViewHolder> {

    Context context;
    ArrayList<UserSearchesModel> userSearchesModelArrayList;

    public UserSearchesAdapter(Context context, ArrayList<UserSearchesModel> userSearchesModelArrayList) {
        this.context = context;
        this.userSearchesModelArrayList = userSearchesModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_searches_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserSearchesModel userSearchesModel = userSearchesModelArrayList.get(position);

        holder.binding.txtUserSearch.setText(userSearchesModel.getText());

        holder.binding.imgDeleteUserSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteUserSearch(userSearchesModel, holder);
            }
        });

        holder.binding.userSearchHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToSearchDataActivity(userSearchesModel);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userSearchesModelArrayList.size();
    }


    public void GoToSearchDataActivity(UserSearchesModel userSearchesModel) {
        Intent intent = new Intent(context, SearchDataActivity.class);
        intent.putExtra("query", userSearchesModel.getText());
        context.startActivity(intent);
    }

    public void DeleteUserSearch(UserSearchesModel userSearchesModel, @NonNull ViewHolder holder) {

        String userId = holder.auth.getCurrentUser().getUid();

        holder.userDocRef = holder.fireStore.collection("Users").document(userId);
        holder.userSearchesCol = holder.userDocRef.collection("UserSearches");

        String key = userSearchesModel.getKey();

        holder.userSearchesCol.document(key)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

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

        UserSearchesItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference userDocRef;
        CollectionReference userSearchesCol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserSearchesItemBinding.bind(itemView);
            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

        }
    }

}
