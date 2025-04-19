package com.grocery.groceryapp.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ChatBotItemBinding;
import com.grocery.groceryapp.models.ChatBotModel;

import java.util.ArrayList;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {

    Context context;
    ArrayList<ChatBotModel> chatBotModelArrayList;

    public ChatBotAdapter(Context context, ArrayList<ChatBotModel> chatBotModelArrayList) {
        this.context = context;
        this.chatBotModelArrayList = chatBotModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_bot_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatBotModel chatBotModel = chatBotModelArrayList.get(position);

        AddUserImage(holder);
        CopyingTheText(holder);

        if (chatBotModel.getSentBy().equals(ChatBotModel.SENT_BY_ME)) {

            holder.binding.chatBotMessageHolder.setVisibility(View.GONE);
            holder.binding.userMessageHolder.setVisibility(View.VISIBLE);
            holder.binding.txtUserMessage.setText(chatBotModel.getMessage());

        } else {

            holder.binding.chatBotMessageHolder.setVisibility(View.VISIBLE);
            holder.binding.userMessageHolder.setVisibility(View.GONE);
            holder.binding.txtChatBotAnswer.setText(chatBotModel.getMessage());

        }





    }

    @Override
    public int getItemCount() {
        return chatBotModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ChatBotItemBinding binding;
        FirebaseAuth auth;
        FirebaseFirestore fireStore;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatBotItemBinding.bind(itemView);

            auth= FirebaseAuth.getInstance();
            fireStore = FirebaseFirestore.getInstance();

        }

    }


    public void AddUserImage(@NonNull ViewHolder holder) {

        holder.fireStore.collection("Users")
                .document(holder.auth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        Glide.with(context)
                                .load(value.getString("userImage"))
                                .placeholder(R.drawable.user_no_profile_icon)
                                .into(holder.binding.imgUserImage);

                    }
                });


    }

    public void CopyingTheText(@NonNull ViewHolder holder) {

        holder.binding.txtChatBotAnswer.setTextIsSelectable(true);
        holder.binding.txtUserMessage.setTextIsSelectable(true);

        // for All Bot Text Copied
        // Also User Copied the Selected Text google provide it.
        holder.binding.imgAllBotTextCopied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager)
                        context.getSystemService(Context.CLIPBOARD_SERVICE);

                String textToCopy = ((TextView) holder.binding.txtChatBotAnswer).getText().toString(); // Get text from TextView
                ClipData clip = ClipData.newPlainText("Label", textToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(v.getContext(), "All Text copied!", Toast.LENGTH_SHORT).show();

            }
        });

        // for All User Text Copied
        // Also User Copied the Selected Text google provide it.
        holder.binding.imgAllUserTextCopied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager)
                        context.getSystemService(Context.CLIPBOARD_SERVICE);

                String textToCopy = ((TextView) holder.binding.txtUserMessage).getText().toString(); // Get text from TextView
                ClipData clip = ClipData.newPlainText("Label", textToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(v.getContext(), "Text copied!", Toast.LENGTH_SHORT).show();

            }
        });


    }


}
