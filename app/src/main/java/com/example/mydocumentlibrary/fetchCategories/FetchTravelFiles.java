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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.FileMyViewHolder;
import com.example.mydocumentlibrary.PutPDF;
import com.example.mydocumentlibrary.R;
import com.example.mydocumentlibrary.categories.SecretsPage;
import com.example.mydocumentlibrary.categories.TravelPage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FetchTravelFiles extends AppCompatActivity {

    DatabaseReference databaseReference, userRef;
    List<PutPDF> uploadedPDF;
    private Button moveToTravelPage;
    private String userID;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<PutPDF> options;
    FirebaseRecyclerAdapter<PutPDF, FileMyViewHolder> adapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_travel_files);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadTravel");

        moveToTravelPage = findViewById(R.id.previous_page);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.recyclerViewFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        moveToTravelPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FetchTravelFiles.this, TravelPage.class);
                startActivity(intent);
            }
        });

        uploadedPDF = new ArrayList<>();

        FetchFiles();
    }

    private void FetchFiles() {
        options = new FirebaseRecyclerOptions.Builder<PutPDF>().setQuery(databaseReference.child(userID), PutPDF.class).build();
        adapter = new FirebaseRecyclerAdapter<PutPDF, FileMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FileMyViewHolder holder, int position, @NonNull PutPDF model) {

                //initializing the data as textView, ... others to the single_view_files.xml
                holder.name.setText(model.getName());
                holder.date.setText(model.getCreatedDate());
                holder.notes.setText(model.getNotes());
                holder.original.setText(model.getOriginalDoc());
                holder.deadline.setText(model.getDeadlineDate());
                holder.category.setVisibility(View.GONE);
                holder.permissionFriends.setChecked(model.isPermissionForFriends());

                holder.permissionFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Update the model with the new permission status
                        model.setPermissionForFriends(isChecked);

                        // Update the permission status in the database
                        DatabaseReference permissionRef = databaseReference.child(userID).child(getRef(holder.getAdapterPosition()).getKey()).child("permissionForFriends");
                        permissionRef.setValue(isChecked);

                        // Show or hide the associated views based on the permission status
                        if (isChecked) {
                            holder.permissionFriends.setVisibility(View.VISIBLE);
                        } else {
                            holder.permissionFriends.setVisibility(View.VISIBLE);
                        }
                    }
                });

                //Change deadline Button and Delete Button
                ImageButton deadlineChangeBtn = holder.itemView.findViewById(R.id.file_deadline);
                ImageButton deleteFileBtn = holder.itemView.findViewById(R.id.file_delete);

                deadlineChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentDeadlineDate = holder.deadline.getText().toString();
                        Calendar updatedDeadline = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            updatedDeadline.setTime(dateFormat.parse(currentDeadlineDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            updatedDeadline = Calendar.getInstance();
                        }
                        // Create a DatePickerDialog to allow the user to select a new deadline date
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                v.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Create a new Calendar object with the selected deadline date
                                Calendar selectedCalendar2 = Calendar.getInstance();
                                selectedCalendar2.add(Calendar.WEEK_OF_YEAR, 2);
                                selectedCalendar2.set(year, month, dayOfMonth);

                                // Update the deadline date in the holder with the selected date
                                holder.deadline.setText(dateFormat.format(selectedCalendar2.getTime()));

                                DatabaseReference deadlineRef = FirebaseDatabase.getInstance().getReference()
                                        .child("uploadTravel").child(userID).child(getRef(holder.getAdapterPosition()).getKey()).child("deadlineDate");
                                deadlineRef.setValue(dateFormat.format(selectedCalendar2.getTime()));
                                Toast.makeText(FetchTravelFiles.this, "Deadline updated", Toast.LENGTH_SHORT).show();
                            }
                        },
                                updatedDeadline.get(Calendar.YEAR),
                                updatedDeadline.get(Calendar.MONTH),
                                updatedDeadline.get(Calendar.DAY_OF_MONTH)

                        );
                        datePickerDialog.show();
                    }
                });

                Button updateNoteButton = holder.itemView.findViewById(R.id.updateNoteBtn);
                Button updateOriginButton = holder.itemView.findViewById(R.id.updateOriginBtn);
                ImageButton downloadFileBtn = holder.itemView.findViewById(R.id.file_download);
                TextView fileTypeText = holder.itemView.findViewById(R.id.file_type);
                fileTypeText.setVisibility(View.GONE);


                //If changed the EditText notes, then save newer version in the database. If not just leave.
                EditText notesFile = holder.itemView.findViewById(R.id.file_notes);
                EditText originalFile = holder.itemView.findViewById(R.id.file_original);

                updateNoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the updated note from the EditText
                        String updatedNote = notesFile.getText().toString();

                        // Update the note in the database
                        DatabaseReference notesRef = FirebaseDatabase.getInstance().getReference()
                                .child("uploadTravel").child(userID).child(getRef(holder.getAdapterPosition()).getKey()).child("notes");
                        notesRef.setValue(updatedNote);

                        // Show a message or perform any desired action to indicate the successful update
                        Toast.makeText(getApplicationContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                });

                notesFile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                updateOriginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the updated note from the EditText
                        String updatedOriginal = originalFile.getText().toString();

                        // Update the note in the database
                        DatabaseReference originalRef = FirebaseDatabase.getInstance().getReference()
                                .child("uploadTravel").child(userID).child(getRef(holder.getAdapterPosition()).getKey()).child("originalDoc");
                        originalRef.setValue(updatedOriginal);

                        // Show a message or perform any desired action to indicate the successful update
                        Toast.makeText(getApplicationContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
                originalFile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                deleteFileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Remove the file from the database
                        DatabaseReference fileRef = FirebaseDatabase.getInstance().getReference()
                                .child("uploadTravel").child(userID).child(getRef(holder.getAdapterPosition()).getKey());
                        fileRef.removeValue();
                        Toast.makeText(getApplicationContext(), "File deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                });

                downloadFileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileUrl = model.getUrl();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl))
                                .setTitle(model.getName())
                                .setDescription("Downloading File...")
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, model.getName());
                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadManager.enqueue(request);
                    }
                });

                //download the document by clicking to the CardView to the mobile downloads path
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileUrl = model.getUrl();

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No PDF viewer application found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public FileMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //initializing single_view_files.xml
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_files, parent, false);
                return new FileMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}