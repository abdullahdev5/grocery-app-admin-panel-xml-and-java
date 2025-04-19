package com.grocery.groceryapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.activities.AboutUsActivity;
import com.grocery.groceryapp.activities.BuyedProductsActivity;
import com.grocery.groceryapp.activities.ChatBotActivity;
import com.grocery.groceryapp.activities.LogInActivity;
import com.grocery.groceryapp.activities.MainActivity;
import com.grocery.groceryapp.activities.OrderReviewActivity;
import com.grocery.groceryapp.activities.OrderStatusActivity;
import com.grocery.groceryapp.activities.ToShipAndDeliveryActivity;
import com.grocery.groceryapp.databinding.FragmentHomeBinding;
import com.grocery.groceryapp.databinding.FragmentProfileBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private int GALLERY_REQ_CODE = 45764;
    private FirebaseFirestore fireStore;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DocumentReference userDocRef;
    private CollectionReference wishListProductsCol, cartProductsCol, buyedProductsCol;
    private Uri imageFileUri;


    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference();

        String uid = auth.getCurrentUser().getUid();
        userDocRef = fireStore.collection("Users").document(uid);

        // Wishlist Products
        wishListProductsCol = userDocRef.collection("WishListProducts");

        // Cart Products
        cartProductsCol = userDocRef.collection("CartProducts");

        // Buyed Products
        buyedProductsCol = userDocRef.collection("BuyedProducts");


        ShowUserImageAndUsername();

        ShowTheNumberOfWishListProducts(); // Number of Wishlist Products
        ShowTheNumberOfCartProducts(); // Number of Cart Products


        binding.imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        binding.wishListsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIfWishListProductNotEqualZeroAndOpenWishListBottomSheet();
            }
        });

        binding.wishListNumberHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIfWishListProductNotEqualZeroAndOpenWishListBottomSheet();
            }
        });

        binding.cartHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIfCartProductNotEqualZeroAndOpenCartProductsBottomSheet();
            }
        });

        binding.cartNumberHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIfCartProductNotEqualZeroAndOpenCartProductsBottomSheet();
            }
        });

        binding.ordersHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckValidationAndGoToBuyedProductsActivity();
            }
        });

        binding.toShippedHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToShipAndDeliveryActivity.class);
                intent.putExtra("orderStatus", getResources().getString(R.string.shipped));
                startActivity(intent);
                Animatoo.INSTANCE.animateInAndOut(getContext());
            }
        });

        binding.toDeliveredHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToShipAndDeliveryActivity.class);
                intent.putExtra("orderStatus", getResources().getString(R.string.delivered));
                startActivity(intent);
                Animatoo.INSTANCE.animateInAndOut(getContext());
            }
        });

        binding.toReviewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderReviewActivity.class);
                startActivity(intent);
                Animatoo.INSTANCE.animateInAndOut(getContext());
            }
        });

        binding.aboutUsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutUsActivity.class);
                startActivity(intent);
                Animatoo.INSTANCE.animateInAndOut(getContext());
            }
        });


        binding.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu(v);
            }
        });

        binding.fabChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogAndGoToChatBotActivity();
            }
        });

        binding.fabWhatsAppChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogAndGoToSpecificWhatsAppContactForAnyHelp();
            }
        });



        return view;
    }

    private void ShowDialogAndGoToSpecificWhatsAppContactForAnyHelp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Message");
        builder.setMessage("If You don't get help with the chat Bot so contact the helper on Whats App.");

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                GoToSpecificWhatsAppContactForAnyHelp();

            }
        });

        builder.show();

    }

    private void GoToSpecificWhatsAppContactForAnyHelp() {

        String phoneNumber = ""; // Helper Phone Number.

        // Create a Uri with the appropriate WhatsApp Scheme
        Uri uri = Uri.parse("https://wa.me/" + phoneNumber);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        // set Flags to ensure that the intent is treated as a new Task
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Check if there's an App available to handle the intent
        if (getContext().getPackageManager() != null) {
            startActivity(intent);

        } else {
            Toast.makeText(getContext(), "Make Sure You have installed whats App in Your Phone", Toast.LENGTH_SHORT).show();
        }



    }
    private void ShowDialogAndGoToChatBotActivity() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Message");
        builder.setMessage("If You Want Any Help for Products So Ask Chat Bot to help.");

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getContext(), ChatBotActivity.class);
                startActivity(intent);
                Animatoo.INSTANCE.animateSlideUp(getContext());

            }
        });

        builder.show();

    }

    private void ShowDialogWhenClickOnLogOutMenuItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert!");
        builder.setMessage("Are you sure want to Log Out?");

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                Intent intent = new Intent(getContext(), LogInActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });

        builder.show();

    }

    private void PopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.nav_wishlistItems) {

                    CheckIfWishListProductNotEqualZeroAndOpenWishListBottomSheet();

                    return true;
                }
                if (item.getItemId() == R.id.nav_cartItems) {

                    CheckIfCartProductNotEqualZeroAndOpenCartProductsBottomSheet();

                    return true;
                }
                if (item.getItemId() == R.id.nav_allOrders) {

                    CheckValidationAndGoToBuyedProductsActivity();

                    return true;
                }
                if (item.getItemId() == R.id.nav_toShipped) {

                    Intent intent = new Intent(getContext(), ToShipAndDeliveryActivity.class);
                    intent.putExtra("orderStatus", getResources().getString(R.string.shipped));
                    startActivity(intent);
                    Animatoo.INSTANCE.animateInAndOut(getContext());

                    return true;
                }
                if (item.getItemId() == R.id.nav_toDelivered) {

                    Intent intent = new Intent(getContext(), ToShipAndDeliveryActivity.class);
                    intent.putExtra("orderStatus", getResources().getString(R.string.delivered));
                    startActivity(intent);
                    Animatoo.INSTANCE.animateInAndOut(getContext());

                    return true;
                }
                if (item.getItemId() == R.id.nav_toReview) {

                    Intent intent = new Intent(getContext(), OrderReviewActivity.class);
                    startActivity(intent);
                    Animatoo.INSTANCE.animateInAndOut(getContext());

                    return true;
                }
                if (item.getItemId() == R.id.nav_chatBot) {

                    ShowDialogAndGoToChatBotActivity();

                    return true;
                }
                if (item.getItemId() == R.id.nav_logOut) {

                    ShowDialogWhenClickOnLogOutMenuItem();

                    return true;
                }


                return false;
            }
        });


        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.show();


    }

    private void CheckValidationAndGoToBuyedProductsActivity() {

        Query query = buyedProductsCol.count().getQuery();

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int count = task.getResult().getDocuments().size();

                            if (count == 0) {
                                Toast.makeText(getContext(), "You Don't have Any Order Yet!", Toast.LENGTH_SHORT).show();

                            } else {
                                Intent intent = new Intent(getContext(), BuyedProductsActivity.class);
                                startActivity(intent);
                                Animatoo.INSTANCE.animateShrink(getContext());
                            }

                        }

                    }
                });

    }



    private void ShowTheNumberOfWishListProducts() {

        Query query = wishListProductsCol.count().getQuery();

        query.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    int count = task.getResult().getDocuments().size();

                    binding.txtWishListProductsNumber.setText(String.valueOf(count));


                    }

                }
            });

    }

    private void CheckIfWishListProductNotEqualZeroAndOpenWishListBottomSheet() {

        Query query = wishListProductsCol.count().getQuery();

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int count = task.getResult().getDocuments().size();

                            if (count == 0) {

                                Toast.makeText(getContext(), "You don't have Any Product to the Wish List Yet!", Toast.LENGTH_SHORT).show();

                            } else {
                                WishListProductsBottomSheetFragment wishListProductsBottomSheetFragment =
                                        new WishListProductsBottomSheetFragment();

                                wishListProductsBottomSheetFragment.show(getActivity().getSupportFragmentManager(),
                                        wishListProductsBottomSheetFragment.getTag());

                            }

                        }

                    }
                });

    }



    private void ShowTheNumberOfCartProducts() {

        Query query = cartProductsCol.count().getQuery();

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int count = task.getResult().getDocuments().size();

                            binding.txtCartProductsNumber.setText(String.valueOf(count));


                        }

                    }
                });

    }

    private void CheckIfCartProductNotEqualZeroAndOpenCartProductsBottomSheet() {

        Query query = cartProductsCol.count().getQuery();

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            int count = task.getResult().getDocuments().size();

                            if (count == 0) {

                                Toast.makeText(getContext(), "You don't have Any Product to the Cart List Yet!", Toast.LENGTH_SHORT).show();

                            } else {
                                CartProductsBottomSheetFragment cartProductsBottomSheetFragment =
                                        new CartProductsBottomSheetFragment();

                                cartProductsBottomSheetFragment.show(getActivity().getSupportFragmentManager(),
                                        cartProductsBottomSheetFragment.getTag());

                            }

                        }

                    }
                });

    }



    private void OpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY_REQ_CODE) {

                if (requestCode == GALLERY_REQ_CODE) {

                    imageFileUri = data.getData();

                    binding.imgUserImage.setImageURI(imageFileUri);

                    StoreUserImageOnDataBase(imageFileUri);


                }

            }

        }




    }



    private void StoreUserImageOnDataBase(Uri imageFileUri) {

        if (imageFileUri != null) {

            String uid = auth.getCurrentUser().getUid();


            StorageReference ref = storageReference.child("UserImages/").child(uid);

            // imageFileUri I Created in the Top Side of Class and Set it to null
            ref.putFile(imageFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Map<String, Object> storeUserImage = new HashMap<>();
                            storeUserImage.put("userImage", uri.toString());


                            fireStore.collection("Users")
                                    .document(uid)
                                    .update(storeUserImage)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }



    }



    private void ShowUserImageAndUsername() {

        String uid = auth.getCurrentUser().getUid();

        fireStore.collection("Users")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        Glide.with(getContext())
                                .load(value.getString("userImage"))
                                .placeholder(R.drawable.user_no_profile_icon)
                                .into(binding.imgUserImage);

                        binding.txtUsername.setText(value.getString("fullName"));


                    }
                });




    }





}