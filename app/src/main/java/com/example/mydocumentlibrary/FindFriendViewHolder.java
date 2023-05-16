package com.example.mydocumentlibrary;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder { //single_view_friends.xml

    TextView userEmail, userID;
//    Spinner userRelationship;
    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        userEmail = itemView.findViewById(R.id.user_email_friend);
        userID = itemView.findViewById(R.id.user_uid_friend);
//        userRelationship = itemView.findViewById(R.id.user_relationship_friend);
    }
}
