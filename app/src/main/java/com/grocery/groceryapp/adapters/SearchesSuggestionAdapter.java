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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.SearchDataActivity;
import com.grocery.groceryapp.activities.UserSearchesActivity;
import com.grocery.groceryapp.databinding.SearchesSuggestionItemBinding;
import com.grocery.groceryapp.models.SearchesSuggestionModel;
import com.grocery.groceryapp.models.UserSearchesModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchesSuggestionAdapter extends RecyclerView.Adapter<SearchesSuggestionAdapter.ViewHolder> {

    Context context;
    ArrayList<SearchesSuggestionModel> searchesSuggestionModelArrayList;

    public SearchesSuggestionAdapter(Context context, ArrayList<SearchesSuggestionModel> searchesSuggestionModelArrayList) {
        this.context = context;
        this.searchesSuggestionModelArrayList = searchesSuggestionModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searches_suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SearchesSuggestionModel searchesSuggestionModel = searchesSuggestionModelArrayList.get(position);

        holder.binding.txtShortProductName.setText(searchesSuggestionModel.getShortProductName());

        // set the User SEarches Collection
        String uid = holder.auth.getCurrentUser().getUid();

        holder.usersDocRef = holder.fireStore.collection("Users").document(uid);

        holder.userSearchesCol = holder.usersDocRef.collection("UserSearches");

        holder.binding.shortProductNameHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToSearchDataActivity(searchesSuggestionModel);
                StoreUserSearchesOnDatabase(searchesSuggestionModel.getShortProductName(), holder);
            }
        });



    }

    @Override
    public int getItemCount() {
        return searchesSuggestionModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        SearchesSuggestionItemBinding binding;
        FirebaseFirestore fireStore;
        FirebaseAuth auth;
        DocumentReference usersDocRef;
        CollectionReference userSearchesCol;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchesSuggestionItemBinding.bind(itemView);

            fireStore = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

        }

    }

    public void GoToSearchDataActivity(SearchesSuggestionModel searchesSuggestionModel) {
        Intent intent = new Intent(context, SearchDataActivity.class);
        intent.putExtra("query", searchesSuggestionModel.getShortProductName());
        context.startActivity(intent);
    }

    public void searchDataList(ArrayList<SearchesSuggestionModel> searchList) { // QuotesData is Model Class that Holds Data.
        searchesSuggestionModelArrayList = searchList; // quotesDataArrayList is the Variable of ArrayList<Model Class Name> quotesDataArrayList;
        notifyDataSetChanged();
    }

    public void StoreUserSearchesOnDatabase(String text, @NonNull ViewHolder holder) {

        String userSearchesKey = holder.userSearchesCol.document().getId();

        Map<String, Object> addUserSearch = new HashMap<>();
        addUserSearch.put("text", text);
        addUserSearch.put("key", userSearchesKey);
        addUserSearch.put("timeStamp", FieldValue.serverTimestamp());


        holder.userSearchesCol.document(userSearchesKey)
                .set(addUserSearch)
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


}