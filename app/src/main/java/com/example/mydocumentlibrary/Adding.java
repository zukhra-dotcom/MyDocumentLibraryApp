package com.example.mydocumentlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mydocumentlibrary.categories.PersonalPage;
import com.example.mydocumentlibrary.fetchCategories.FetchPersonalFiles;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Adding extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    EditText selectPDF;
    Button uploadPDF;
    ImageButton cameraScanButton;
    ImageView scanImageView;
    RadioGroup categoryRadioGroup1, categoryRadioGroup2;
    RadioButton personalRadio, educationalRadio, healthRadio, jobRadio, travelRadio, billsRadio, secretRadio;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");
        selectPDF = findViewById(R.id.selectFile);
        uploadPDF = findViewById(R.id.uploadFile);
        cameraScanButton = findViewById(R.id.camera_scan);
        scanImageView = findViewById(R.id.scannedImageView);
        storageReference = FirebaseStorage.getInstance().getReference();
//        databaseReference = FirebaseDatabase.getInstance().getReference(); //here not only one database node

        categoryRadioGroup1 = findViewById(R.id.radioGroup);
        categoryRadioGroup2 = findViewById(R.id.radioGroup2);

        personalRadio = findViewById(R.id.radioButtonPersonal);
        educationalRadio = findViewById(R.id.radioButtonEducational);
        healthRadio = findViewById(R.id.radioButtonHealth);
        jobRadio = findViewById(R.id.radioButtonJob);
        travelRadio = findViewById(R.id.radioButtonTravel);
        billsRadio = findViewById(R.id.radioButtonBills);
        secretRadio = findViewById(R.id.radioButtonSecret);

        selectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPDFFile();
                cameraScanButton.setEnabled(false);
            }
        });

        cameraScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check camera permission
                selectPDF.setEnabled(false);
                if (ContextCompat.checkSelfPermission(Adding.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Adding.this, new String[]{Manifest.permission.CAMERA},
                            PERMISSION_CODE);
                } else {
                    // Permission granted, start camera scanning
                    startCameraScan();
                }
            }
        });

        categoryRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonPersonal:
                        Toast.makeText(Adding.this, "Personal category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadPersonal/");
                        break;
                    case R.id.radioButtonEducational:
                        Toast.makeText(Adding.this, "Educational category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadEducational/");
                        break;
                    case R.id.radioButtonHealth:
                        Toast.makeText(Adding.this, "Health category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadHealth/");
                        break;
                }
            }
        });
        categoryRadioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonTravel:
                        Toast.makeText(Adding.this, "Travel category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadTravel/");
                        break;
                    case R.id.radioButtonBills:
                        Toast.makeText(Adding.this, "Bill category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadBill/");
                        break;
                    case R.id.radioButtonSecret:
                        Toast.makeText(Adding.this, "Secret category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadSecrets/");
                        break;
                    case R.id.radioButtonJob:
                        Toast.makeText(Adding.this, "Job category selected", Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("uploadJob/");
                        break;
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashboard:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    finish();
                    return true;
                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    finish();
                    return true;
                case R.id.notes:
                    startActivity(new Intent(getApplicationContext(), Notes.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    finish();
                    return true;
                case R.id.notification:
                    startActivity(new Intent(getApplicationContext(), Notifications.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    finish();
                    return true;
            }
            return false;
        });


    }

    private void startCameraScan() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        }
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
        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri pdfUri = data.getData();
            selectPDF.setText("File Selected");
            selectPDF.setText(data.getDataString()
                    .substring(data.getDataString().lastIndexOf("/") + 1));
            uploadPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDFFile(data.getData());
                }
            });
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            // Get the captured image
            scanImageView.setVisibility(View.VISIBLE);
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            Uri imageUri = data.getData();

            // Start the cropping activity
            CropImage.activity(imageUri)
                    .start(this);

            // Display the captured image in the ImageView
            ImageView scannedImageView = findViewById(R.id.scannedImageView);
            scannedImageView.setImageBitmap(imageBitmap);

            // Convert the image to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();


            // Upload the image to Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String imageName = "added_" + System.currentTimeMillis() + ".jpg";
            StorageReference imageRef = storageReference.child("added/" + uid + "/" + imageName);
            imageRef.putBytes(imageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Retrieve the download URL of the uploaded image
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Save the image URL in the Firebase Realtime Database

                                    String key = databaseReference.child(uid).push().getKey();
                                    String imageURL = uri.toString();
                                    databaseReference.child(uid).child(key).child("url").setValue(imageURL);
                                    // Perform additional processing or save other data related to the scanned image
                                    String createdDate = getCurrentDateTime();
                                    String name = "scanned" + key;

                                    HashMap<String, Object> documentData = new HashMap<>();
                                    documentData.put("createdDate", createdDate);
                                    documentData.put("name", name);
                                    documentData.put("url", imageURL);

                                    databaseReference.child(uid).child(key).setValue(documentData);
                                    Toast.makeText(Adding.this, "Scanned document uploaded to the storage", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void uploadPDFFile(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        //Store the document for each users
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference reference = storageReference.child("added/" + uid + "/" + "new" + System.currentTimeMillis() + ".file");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();

                        //While uploading new file identify uploadDate
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
                        String strDate = formatter.format(date);

                        PutPDF putPDF = new PutPDF(selectPDF.getText().toString(), uri.toString(), strDate);

                        //New code 08.05.2023 to store for each users here UID is as a key
                        databaseReference.child(uid).push().setValue(putPDF);

                        //it was before 08.05.2023. so in the top I added to store for each users
//                        databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);
                        Toast.makeText(Adding.this, "File is uploaded", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("File is uploading..." + (int) progress + "%");
                    }
                });
    }
}