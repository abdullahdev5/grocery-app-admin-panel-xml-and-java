package com.grocery.groceryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.adapters.SearchesSuggestionAdapter;
import com.grocery.groceryapp.adapters.UserSearchesAdapter;
import com.grocery.groceryapp.databinding.ActivityUserSearchesBinding;
import com.grocery.groceryapp.models.SearchesSuggestionModel;
import com.grocery.groceryapp.models.UserSearchesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSearchesActivity extends AppCompatActivity {

    ActivityUserSearchesBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth auth;
    DocumentReference usersDocRef;
    CollectionReference userSearchesCol;
    ArrayList<UserSearchesModel> userSearchesModelArrayList;
    UserSearchesAdapter userSearchesAdapter;
    ArrayList<SearchesSuggestionModel> searchesSuggestionModelArrayList;
    SearchesSuggestionAdapter searchesSuggestionAdapter;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserSearchesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        binding.searchView.requestFocus();

        String uid = auth.getCurrentUser().getUid();

        usersDocRef = fireStore.collection("Users").document(uid);

        userSearchesCol = usersDocRef.collection("UserSearches");


        // User Searches
        binding.rvUserSearches.setHasFixedSize(true);
        userSearchesModelArrayList = new ArrayList<>();
        binding.rvUserSearches.setLayoutManager(new LinearLayoutManager(this));
        userSearchesAdapter = new UserSearchesAdapter(this, userSearchesModelArrayList);
        binding.rvUserSearches.setAdapter(userSearchesAdapter);
        AddUserSearches();
        RemoveUserSearch();

        // Search Suggestions
        binding.rvSearchesSuggestion.setHasFixedSize(true);
        binding.rvSearchesSuggestion.setLayoutManager(new LinearLayoutManager(this));
        searchesSuggestionModelArrayList = new ArrayList<>();
        searchesSuggestionAdapter = new SearchesSuggestionAdapter(this, searchesSuggestionModelArrayList);
        binding.rvSearchesSuggestion.setAdapter(searchesSuggestionAdapter);
        AddUSearchesSuggestion();


        // get the Passing Intent for Show the Text Query
        intent = getIntent();
        binding.searchView.setQuery(intent.getStringExtra("query"), false);


        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 35) {
                    Toast.makeText(UserSearchesActivity.this, "Please Search Less", Toast.LENGTH_SHORT).show();
                } else {
                    binding.rvSearchesSuggestion.setVisibility(View.GONE);
                    StoreUserSearchesOnDatabase(query);
                    GoToSearchDataActivity(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                binding.rvSearchesSuggestion.setVisibility(View.VISIBLE);
                binding.rvUserSearches.setVisibility(View.GONE);
                SearchList(newText);

                if (newText.isEmpty()) {
                    binding.rvUserSearches.setVisibility(View.VISIBLE);
                    binding.rvSearchesSuggestion.setVisibility(View.GONE);
                }

                return false;
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }


    private void SearchList(String text) {

        ArrayList<SearchesSuggestionModel> searchList = new ArrayList<>(); // QuotesData is a Model Class that Holds the Data.

        for (SearchesSuggestionModel searchesSuggestionModel : searchesSuggestionModelArrayList) { // Quand otesData is a Model Class that Holds the Data. and quotesDataArrayList is the Variable of ArrayList<Model Class Name> quotesDataArrayList;

            if (searchesSuggestionModel.getShortProductName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(searchesSuggestionModel);
            }

        }

        searchesSuggestionAdapter.searchDataList(searchList);

    }

    private void StoreUserSearchesOnDatabase(String text) {

        String userSearchesKey = userSearchesCol.document().getId();

        Map<String, Object> addUserSearch = new HashMap<>();
        addUserSearch.put("text", text);
        addUserSearch.put("key", userSearchesKey);
        addUserSearch.put("timeStamp", FieldValue.serverTimestamp());


        userSearchesCol.document(userSearchesKey)
                .set(addUserSearch)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserSearchesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void AddUserSearches() {

        userSearchesCol.orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                userSearchesModelArrayList.add(document.getDocument().toObject(UserSearchesModel.class));

                            }

                            userSearchesAdapter.notifyDataSetChanged();


                        }


                    }
                });

    }

    private void RemoveUserSearch() {

        userSearchesCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.REMOVED) {

                                String removedDocId = document.getDocument().getId();
                                int indexOfRemovedItem = -1;
                                for (int i = 0 ; i < userSearchesModelArrayList.size() ; i++) {

                                    UserSearchesModel item = userSearchesModelArrayList.get(i);
                                    if (item.getKey().equals(removedDocId)) {
                                        indexOfRemovedItem = i;
                                        break;
                                    }

                                }

                                if (indexOfRemovedItem != -1) {
                                    userSearchesModelArrayList.remove(indexOfRemovedItem);
                                    userSearchesModelArrayList.remove(document.getDocument().toObject(UserSearchesModel.class));
                                }

                            }

                            userSearchesAdapter.notifyDataSetChanged();


                        }

                    }
                });


    }


    private void GoToSearchDataActivity(String query) {
        Intent intent = new Intent(getApplicationContext(), SearchDataActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }


    private void AddUSearchesSuggestion() {

        fireStore.collection("Product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                searchesSuggestionModelArrayList.add(document.getDocument().toObject(SearchesSuggestionModel.class));

                            }

                            searchesSuggestionAdapter.notifyDataSetChanged();


                        }


                    }
                });

    }



}