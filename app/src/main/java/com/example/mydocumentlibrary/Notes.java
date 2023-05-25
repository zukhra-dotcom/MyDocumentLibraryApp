package com.example.mydocumentlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Notes extends AppCompatActivity {

    private RecyclerView recyclerNotes;
    private NotesAdapter notesAdapter;
    private List<PutPDF> documentListInNotes;
    private DatabaseReference personalRef, educationalRef, billsRef, healthRef, jobRef, secretsRef, travelRef;
    FloatingActionButton fab;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerNotes = findViewById(R.id.recyclerNoteFiles);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        fab = findViewById(R.id.fab);


        documentListInNotes = new ArrayList<>();
//        notesAdapter = new NotesAdapter(documentListInNotes);
        recyclerNotes.setAdapter(notesAdapter);

        retrieveDocumentsFromDatabase();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.notes);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashboard:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    finish();
                    return true;
                case R.id.notes:
                    return true;
                case R.id.notification:
                    startActivity(new Intent(getApplicationContext(), Notifications.class));
                    finish();
                    return true;
            }
            return false;
        });

        fab.setOnClickListener(view -> {
            // Perform your desired action here
            startActivity(new Intent(getApplicationContext(), Adding.class));
            finish();
        });

    }

    private void retrieveDocumentsFromDatabase(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, handle accordingly
            return;
        }

        String userID = currentUser.getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Query for uploadPersonal documents
        personalRef = databaseRef.child("uploadPersonal").child(userID);
        educationalRef = databaseRef.child("uploadEducational").child(userID);
        billsRef = databaseRef.child("uploadBills").child(userID);
        healthRef = databaseRef.child("uploadHealth").child(userID);
        jobRef = databaseRef.child("uploadJob").child(userID);
        secretsRef = databaseRef.child("uploadSecrets").child(userID);
        travelRef = databaseRef.child("uploadTravel").child(userID);


        ValueEventListener personalValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, personalRef, "Personal");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        ValueEventListener educationalValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, educationalRef, "Educational");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving educational documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        ValueEventListener billsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, billsRef, "Bills");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        ValueEventListener healthValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, healthRef, "Health");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        ValueEventListener jobValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, jobRef, "Job");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        ValueEventListener secretsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, secretsRef, "Secrets");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        ValueEventListener travelValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    addDocumentToList(childSnapshot, travelRef, "Travel");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notes.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        personalRef.addListenerForSingleValueEvent(personalValueEventListener);
        educationalRef.addListenerForSingleValueEvent(educationalValueEventListener);
        billsRef.addListenerForSingleValueEvent(billsValueEventListener);
        healthRef.addListenerForSingleValueEvent(healthValueEventListener);
        jobRef.addListenerForSingleValueEvent(jobValueEventListener);
        secretsRef.addListenerForSingleValueEvent(secretsValueEventListener);
        travelRef.addListenerForSingleValueEvent(travelValueEventListener);
    }

    private void addDocumentToList(DataSnapshot snapshot, DatabaseReference documentRef, String fileType) {
        PutPDF putPDF = snapshot.getValue(PutPDF.class);
        if (putPDF != null && putPDF.getNotes() != null && !putPDF.getNotes().isEmpty()) {
            documentListInNotes.add(putPDF);
            Collections.sort(documentListInNotes, new Comparator<PutPDF>() {
                @Override
                public int compare(PutPDF document1, PutPDF document2) {
                    String notes1 = document1.getNotes();
                    String notes2 = document2.getNotes();
                    return notes1.compareToIgnoreCase(notes2);
                }
            });
            // Set the fileType value based on the database node
            if (documentRef.equals(personalRef)) {
                putPDF.setFileType("Personal");
            } else if (documentRef.equals(educationalRef)) {
                putPDF.setFileType("Educational");
            } else if (documentRef.equals(billsRef)) {
                putPDF.setFileType("Bills");
            } else if (documentRef.equals(healthRef)) {
                putPDF.setFileType("Health");
            } else if (documentRef.equals(jobRef)) {
                putPDF.setFileType("Job");
            } else if (documentRef.equals(secretsRef)) {
                putPDF.setFileType("Secrets");
            } else if (documentRef.equals(travelRef)) {
                putPDF.setFileType("Travel");
            } else {
                // Handle other cases or set a default file type
                putPDF.setFileType("Unknown");
            }

            if (notesAdapter == null) {
                notesAdapter = new NotesAdapter(documentListInNotes);
                recyclerNotes.setAdapter(notesAdapter);
            } else {
                notesAdapter.notifyDataSetChanged();
            }
        }
    }
}
