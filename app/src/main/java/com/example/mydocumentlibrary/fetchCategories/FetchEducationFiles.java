package com.example.mydocumentlibrary.fetchCategories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mydocumentlibrary.PutPDF;
import com.example.mydocumentlibrary.R;
import com.example.mydocumentlibrary.categories.EducationPage;
import com.example.mydocumentlibrary.categories.PersonalPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FetchEducationFiles extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;
    List<PutPDF> uploadedPDF;
    private Button moveToEducationPage;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_education_files);

        //Added 08.05.2023 to store data for each users
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        moveToEducationPage = findViewById(R.id.previous_page);
        moveToEducationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FetchEducationFiles.this, EducationPage.class);
                startActivity(intent);
            }
        });

        listView = findViewById(R.id.listView);
        uploadedPDF = new ArrayList<>();

        retrievePDFFiles();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PutPDF putPDF = uploadedPDF.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(putPDF.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void retrievePDFFiles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadEducational");
        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Added 08.05.2023 to store data for each users
                uploadedPDF.clear();

                for(DataSnapshot ds:snapshot.getChildren()){
                    PutPDF putPDF = ds.getValue(com.example.mydocumentlibrary.PutPDF.class);
                    uploadedPDF.add(putPDF);
                }
                String[] fileName = new String[uploadedPDF.size()];
                for(int i = 0; i < fileName.length; i++){
                    fileName[i] = uploadedPDF.get(i).getName();
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, fileName){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view
                                .findViewById(android.R.id.text1);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(20);
                        return view;
                    }
                };
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}