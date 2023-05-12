package com.example.mydocumentlibrary.categories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydocumentlibrary.FindFriendViewHolder;
import com.example.mydocumentlibrary.Friends;
import com.example.mydocumentlibrary.fetchCategories.FetchFriendsFiles;
import com.example.mydocumentlibrary.MainActivity;
import com.example.mydocumentlibrary.PutPDF;
import com.example.mydocumentlibrary.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FriendsPage extends AppCompatActivity {

    private Button moveToMain;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Friends>options;
    FirebaseRecyclerAdapter<Friends, FriendMyViewHolder>adapter;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_page);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        LoadFriends("");

        moveToMain = findViewById(R.id.previous_main);
        moveToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void LoadFriends(String s) {
        Query query = databaseReference.child(firebaseUser.getUid()).orderByChild("email").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendMyViewHolder holder, int position, @NonNull Friends model) {
                holder.email.setText(model.getEmail());
                holder.uid.setText(model.getUid());
            }

            @NonNull
            @Override
            public FriendMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend, parent, false);
                return new FriendMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}