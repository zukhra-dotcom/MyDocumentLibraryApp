package com.example.mydocumentlibrary.categories;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydocumentlibrary.R;

public class FriendMyViewHolder extends RecyclerView.ViewHolder {
    TextView email, uid;
    Spinner relationship;
    public FriendMyViewHolder(@NonNull View itemView) {
        super(itemView);
        email = itemView.findViewById(R.id.user_email_friend);
        uid = itemView.findViewById(R.id.user_uid_friend);
    }
}
