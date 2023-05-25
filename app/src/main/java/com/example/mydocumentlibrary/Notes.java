package com.example.mydocumentlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private DatabaseReference personalRef;
    private DatabaseReference educationalRef;
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

        personalRef.addListenerForSingleValueEvent(personalValueEventListener);
        educationalRef.addListenerForSingleValueEvent(educationalValueEventListener);
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
            putPDF.setFileType(fileType);

            if (notesAdapter == null) {
                notesAdapter = new NotesAdapter(documentListInNotes);
                recyclerNotes.setAdapter(notesAdapter);
            } else {
                notesAdapter.notifyDataSetChanged();
            }
        }
    }
}
