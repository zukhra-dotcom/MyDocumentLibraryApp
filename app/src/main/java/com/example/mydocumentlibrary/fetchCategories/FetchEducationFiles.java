package com.example.mydocumentlibrary.fetchCategories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.FileMyViewHolder;
import com.example.mydocumentlibrary.PutPDF;
import com.example.mydocumentlibrary.R;
import com.example.mydocumentlibrary.categories.EducationPage;
import com.example.mydocumentlibrary.categories.PersonalPage;
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

public class FetchEducationFiles extends AppCompatActivity {

    DatabaseReference databaseReference, userRef;
    List<PutPDF> uploadedPDF;
    private Button moveToEducationPage;
    //Added 08.05.2023 to store data for each users
    private String userID;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<PutPDF> options;
    FirebaseRecyclerAdapter<PutPDF, FileMyViewHolder> adapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_education_files);

        //Added 08.05.2023 to store data for each users
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("uploadEducational");

        moveToEducationPage = findViewById(R.id.previous_page);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.recyclerViewFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        moveToEducationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FetchEducationFiles.this, EducationPage.class);
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
                holder.deadline.setText(model.getDeadlineDate());

                //Change deadline Button and Delete Button
                ImageButton deadlineChangeBtn = holder.itemView.findViewById(R.id.file_deadline);
                ImageButton deleteFileBtn = holder.itemView.findViewById(R.id.file_delete);

                deadlineChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentDeadlineDate = holder.deadline.getText().toString();
                        Calendar updatedDeadline = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
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
                                selectedCalendar2.set(year, month, dayOfMonth);

                                // Update the deadline date in the holder with the selected date
                                holder.deadline.setText(dateFormat.format(selectedCalendar2.getTime()));

                                DatabaseReference deadlineRef = FirebaseDatabase.getInstance().getReference()
                                        .child("uploadEducational").child(userID).child(getRef(holder.getAdapterPosition()).getKey()).child("deadlineDate");
                                deadlineRef.setValue(dateFormat.format(selectedCalendar2.getTime()));
                                Toast.makeText(FetchEducationFiles.this, "Deadline updated", Toast.LENGTH_SHORT).show();
                            }
                        },
                                updatedDeadline.get(Calendar.YEAR),
                                updatedDeadline.get(Calendar.MONTH),
                                updatedDeadline.get(Calendar.DAY_OF_MONTH)
                        );
                        datePickerDialog.show();
                    }
                });

                //If changed the EditText notes, then save newer version in the database. If not just leave.
                EditText notesFile = holder.itemView.findViewById(R.id.file_notes);
                notesFile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //Do nothing
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String updatedNote = s.toString();
                        DatabaseReference notesRef = FirebaseDatabase.getInstance().getReference()
                                .child("uploadEducational").child(userID).child(getRef(holder.getAdapterPosition()).getKey()).child("notes");
                        notesRef.setValue(updatedNote);
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
                                .child("uploadEducational").child(userID).child(getRef(holder.getAdapterPosition()).getKey());
                        fileRef.removeValue();
                        Toast.makeText(getApplicationContext(), "File deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                });

                //download the document by clicking to the CardView to the mobile downloads path
                holder.itemView.setOnClickListener(new View.OnClickListener() {
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