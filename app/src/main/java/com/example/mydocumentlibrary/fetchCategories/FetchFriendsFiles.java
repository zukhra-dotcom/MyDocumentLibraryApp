package com.example.mydocumentlibrary.fetchCategories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.FileMyViewHolder;
import com.example.mydocumentlibrary.PutPDF;
import com.example.mydocumentlibrary.R;
import com.example.mydocumentlibrary.categories.FriendsPage;
import com.example.mydocumentlibrary.categories.PersonalPage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FetchFriendsFiles extends AppCompatActivity {
    DatabaseReference databaseReference, userRef;
    List<PutPDF> uploadedPDF;
    private Button moveToFriendsPage;
    private String userID;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<PutPDF> options;
    FirebaseRecyclerAdapter<PutPDF, FileMyViewHolder> adapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_friends_files);

        //Added 08.05.2023 to store data for each users
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.recyclerViewFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moveToFriendsPage = findViewById(R.id.previous_page);
        moveToFriendsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FetchFriendsFiles.this, FriendsPage.class);
                startActivity(intent);
            }
        });
        uploadedPDF = new ArrayList<>();
        FetchFiles();
    }


    private void FetchFiles() {
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(userID);
//        DatabaseReference uploadRef = FirebaseDatabase.getInstance().getReference().child("uploadPersonal");

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    List<String> friendUserIDs = new ArrayList<>();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String friendUserID = friendSnapshot.getKey();
//                        friendUserIDs.add(friendUserID);
                    DatabaseReference friendFilesRef = FirebaseDatabase.getInstance().getReference().child("uploadPersonal").child(friendUserID);

                    options = new FirebaseRecyclerOptions.Builder<PutPDF>().setQuery(friendFilesRef, PutPDF.class).build();
                    adapter = new FirebaseRecyclerAdapter<PutPDF, FileMyViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FileMyViewHolder holder, int position, @NonNull PutPDF model) {
                            ImageButton downloadFileBtn = null;
                            if (model.isPermissionForFriends()) {
                                holder.name.setText(model.getName());
                                holder.date.setText(model.getCreatedDate());
                                holder.notes.setText(model.getNotes());
                                holder.deadline.setText(model.getDeadlineDate());

                                ImageButton deadlineChangeBtn = holder.itemView.findViewById(R.id.file_deadline);
                                ImageButton deleteFileBtn = holder.itemView.findViewById(R.id.file_delete);
                                Button updateOriginalFile = holder.itemView.findViewById(R.id.updateOriginBtn);
                                Button updateNoteBtn = holder.itemView.findViewById(R.id.updateNoteBtn);
                                downloadFileBtn = holder.itemView.findViewById(R.id.file_download);
                                EditText notesFileTxt = holder.itemView.findViewById(R.id.file_notes);
                                EditText originalFileTxt = holder.itemView.findViewById(R.id.file_original);
                                Switch permissionFileSwt = holder.itemView.findViewById(R.id.file_permissionSwitch);


                                updateNoteBtn.setId(R.id.file_download);
                                downloadFileBtn.setId(R.id.updateNoteBtn);
                                deadlineChangeBtn.setVisibility(View.GONE);
                                deleteFileBtn.setVisibility(View.GONE);
                                updateNoteBtn.setVisibility(View.GONE);
                                updateOriginalFile.setVisibility(View.GONE);
                                notesFileTxt.setEnabled(false);
                                originalFileTxt.setVisibility(View.GONE);
                                permissionFileSwt.setVisibility(View.GONE);
                            } else {
                                // Hiding the view for documents without permission
                                holder.itemView.setVisibility(View.GONE);
                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                            }

                            downloadFileBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String fileUrl = model.getUrl();
                                    String fileName = model.getName();
                                    String destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/Friends";
                                    File destinationFolder = new File(destinationDirectory);
                                    if (!destinationFolder.exists()) {
                                        destinationFolder.mkdirs();
                                    }
                                    String destinationPath = destinationDirectory + "/" + fileName + ".pdf";

                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl))
                                            .setTitle(fileName)
                                            .setDescription("Downloading File...")
                                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                            .setDestinationUri(Uri.fromFile(new File(destinationPath))); // Set the destination URI

                                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    downloadManager.enqueue(request);
                                }
                            });

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String fileUrl = model.getUrl();

                                    // Assuming the fileUrl is a direct link to a PDF document

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        // Handle exception when no PDF viewer application is available
                                        Toast.makeText(getApplicationContext(), "No PDF viewer application found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public FileMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_files, parent, false);
                            return new FileMyViewHolder(view);
                        }
                    };
                    adapter.startListening();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FetchFriendsFiles.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

