package com.example.mydocumentlibrary.categories;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;

import com.example.mydocumentlibrary.fetchCategories.FetchEducationFiles;
import com.example.mydocumentlibrary.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.MainActivity;
import com.example.mydocumentlibrary.PutPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EducationPage extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Uri imageUri;
    private Button moveToMain;
    EditText selectPDF, writeNote, writeOriginal;
    TextView showDeadlineText;
    int y, m, d;
    Button uploadPDF, createDeadline;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_page);

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        moveToMain = findViewById(R.id.previous_main);
        moveToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EducationPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        selectPDF = findViewById(R.id.selectFile);
        uploadPDF = findViewById(R.id.uploadFile);
        writeNote = findViewById(R.id.writeNoteFile);
        writeOriginal = findViewById(R.id.originalFile);
        createDeadline = findViewById(R.id.createDeadlineFile);
        showDeadlineText = findViewById(R.id.showDeadlineText);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadEducational/");

        uploadPDF.setEnabled(false);
        createDeadline.setEnabled(false);
        showDeadlineText.setVisibility(View.GONE);

        selectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDFFile();
            }
        });
    }

    private void selectPDFFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF file selected"), 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uploadPDF.setEnabled(true);
            createDeadline.setEnabled(true);
            showDeadlineText.setVisibility(View.VISIBLE);
            createDeadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    y = calendar.get(Calendar.YEAR);
                    m = calendar.get(Calendar.MONTH);
                    d = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(EducationPage.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //checking if selected date from the calendar is after today`s date
                            Calendar selectedCalendar = Calendar.getInstance();
                            selectedCalendar.set(Calendar.YEAR, year);
                            selectedCalendar.set(Calendar.MONTH, month);
                            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            if(selectedCalendar.before(calendar)){
                                Toast.makeText(EducationPage.this, "Please select a date on or after today", Toast.LENGTH_SHORT).show();
                            } else {
                                showDeadlineText.setText(dayOfMonth + "." + month + "." + year);
                            }
                        }
                    }, y, m, d);
                    datePickerDialog.show();
                }
            });

            selectPDF.setText(data.getDataString()
                    .substring(data.getDataString().lastIndexOf("/") + 1));
            uploadPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDFFile(data.getData());
                }
            });
        }
    }

    private void uploadPDFFile(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        //Store the document for each users
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference reference = storageReference.child("educational/" + uid + "/" + "educational" + System.currentTimeMillis() + ".file");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();

                        //While uploading new file identify uploadDate
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                        String strDate = formatter.format(date);

                        //this code is primerno correct, then need to be updated + permissionForFriends 16.05.2023 20:25
//                        PutPDF putPDF = new PutPDF(selectPDF.getText().toString(), uri.toString(), writeNote.getText().toString(), writeOriginal.getText().toString(), strDate, showDeadlineText.getText().toString());
//
//                        //To store for each users here UID is as a key
//                        databaseReference.child(uid).push().setValue(putPDF);
//
//                        Toast.makeText(EducationPage.this, "PDF File is uploaded", Toast.LENGTH_LONG).show();
//                        progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        progressDialog.setMessage("File is uploading..." + (int) progress + "%");
                    }
                });
    }

    //check permissions of camera button
    public void check(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA)==
                    PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                    PackageManager.PERMISSION_DENIED)
            {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
            else{
                OpenCamera();
            }
        }
        else{
            OpenCamera();
        }
    }

    //Method to use camera
    private void OpenCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "from the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    public void fetchFile(View view) {
        startActivity(new Intent(getApplicationContext(), FetchEducationFiles.class));
    }
}