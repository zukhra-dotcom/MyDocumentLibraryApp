package com.example.mydocumentlibrary.categories;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mydocumentlibrary.fetchCategories.FetchPersonalFiles;
import com.example.mydocumentlibrary.MainActivity;
import com.example.mydocumentlibrary.PutPDF;
import com.example.mydocumentlibrary.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PersonalPage extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button moveToMain;
    EditText selectPDF, writeNote, writeOriginal;
    TextView showDeadlineText, createdDateText;
    Switch permissionSwitch;
    int y, m, d;
    Button uploadPDF, createDeadline;
    ImageButton cameraScanButton;
    ImageView scanImageView;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        moveToMain = findViewById(R.id.previous_main);
        moveToMain = findViewById(R.id.previous_main);
        moveToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        selectPDF = findViewById(R.id.selectFile);
        uploadPDF = findViewById(R.id.uploadFile);
        writeNote = findViewById(R.id.writeNoteFile);
        writeOriginal = findViewById(R.id.originalFile);
        createDeadline = findViewById(R.id.createDeadlineFile);
        showDeadlineText = findViewById(R.id.showDeadlineText);
        permissionSwitch = findViewById(R.id.permissionSwitchFile);
        cameraScanButton = findViewById(R.id.camera_scan);
        scanImageView = findViewById(R.id.scannedImageView);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadPersonal/");

//        uploadPDF.setEnabled(false);
//        createDeadline.setEnabled(false);
//        //
//        permissionSwitch.setEnabled(false);
        scanImageView.setVisibility(View.GONE);
        showDeadlineText.setVisibility(View.GONE);

        selectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPDFFile();
            }
        });

        cameraScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check camera permission
                if (ContextCompat.checkSelfPermission(PersonalPage.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonalPage.this, new String[]{Manifest.permission.CAMERA},
                            PERMISSION_CODE);
                } else {
                    // Permission granted, start camera scanning
                    startCameraScan();
                }
            }
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
//            uploadPDF.setEnabled(true);
//            createDeadline.setEnabled(true);
//            //
//            permissionSwitch.setEnabled(true);
            Uri pdfUri = data.getData();
            selectPDF.setText("File Selected");
            showDeadlineText.setVisibility(View.VISIBLE);
            createDeadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.WEEK_OF_YEAR, 2);
                    y = calendar.get(Calendar.YEAR);
                    m = calendar.get(Calendar.MONTH);
                    d = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalPage.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //checking if selected date from the calendar is after today`s date
                            Calendar selectedCalendar = Calendar.getInstance();
                            selectedCalendar.set(Calendar.YEAR, year);
                            selectedCalendar.set(Calendar.MONTH, month);
                            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            if (selectedCalendar.before(calendar)) {
                                Toast.makeText(PersonalPage.this, "Please select a date on or after today", Toast.LENGTH_SHORT).show();
                            } else {
                                showDeadlineText.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(selectedCalendar.getTime()));
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

        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            // Get the captured image
            scanImageView.setVisibility(View.VISIBLE);
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            Uri imageUri = data.getData();

            // Start the cropping activity
            CropImage.activity(imageUri)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Get the cropped image URI
                Uri croppedImageUri = result.getUri();

                // Display the cropped image in the ImageView
                ImageView scannedImageView = findViewById(R.id.scannedImageView);
                scannedImageView.setImageURI(croppedImageUri);

                // Convert the cropped image to a byte array
                Bitmap croppedImageBitmap = null;
                try {
                    croppedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                croppedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Upload the cropped image to Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String imageName = "personal_" + System.currentTimeMillis() + ".jpeg";
                StorageReference imageRef = storageReference.child("personal/" + uid + "/" + imageName);
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
                                        String deadlineDate = showDeadlineText.getText().toString();
                                        String name = "scanned" + key;
                                        String notes = writeNote.getText().toString();
                                        String originalDoc = writeOriginal.getText().toString();
                                        boolean permissionForFriends = permissionSwitch.isChecked();

                                        HashMap<String, Object> documentData = new HashMap<>();
                                        documentData.put("createdDate", createdDate);
                                        documentData.put("deadlineDate", deadlineDate);
                                        documentData.put("name", name);
                                        documentData.put("notes", notes);
                                        documentData.put("originalDoc", originalDoc);
                                        documentData.put("permissionForFriends", permissionForFriends);
                                        documentData.put("url", imageURL);

                                        databaseReference.child(uid).child(key).setValue(documentData);
                                        Toast.makeText(PersonalPage.this, "Scanned document uploaded to the storage", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle crop error
                Toast.makeText(getApplicationContext(), "Crop failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void uploadPDFFile(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        //Store the document for each users
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference reference = storageReference.child("personal/" + uid + "/" + "personal" + System.currentTimeMillis() + ".file");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();

                        //While uploading new file identify uploadDate
                        Date date = new Date();
                        //SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        String strDate = formatter.format(date);

                        permissionSwitch.setEnabled(true);
                        boolean permissionForFriends = permissionSwitch.isChecked();
                        PutPDF putPDF = new PutPDF(selectPDF.getText().toString(), uri.toString(), writeNote.getText().toString(), writeOriginal.getText().toString(), strDate, showDeadlineText.getText().toString(), permissionForFriends);

                        //New code 08.05.2023 to store for each users here UID is as a key
                        databaseReference.child(uid).push().setValue(putPDF);

                        //it was before 08.05.2023. so in the top I added to store for each users
//                        databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);
                        Toast.makeText(PersonalPage.this, "File is uploaded", Toast.LENGTH_LONG).show();
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

    public void fetchFile(View view) {
        startActivity(new Intent(getApplicationContext(), FetchPersonalFiles.class));
    }

}