package com.example.mydocumentlibrary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mydocumentlibrary.categories.BillsPage;
import com.example.mydocumentlibrary.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;

public class QuickPicture extends AppCompatActivity {

    //view binding
    private ActivityMainBinding binding;

    private Button moveToMain;

    Button quickPictureButtonCapture;
    ImageView quickPictureImage;
    // sharing
    Button shareImageBtn;
    Button shareTextBtn;
    Button shareImageTextBtn;
    TextInputEditText textNote;

    private Uri imageUri = null;
    private String textToShare = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_quick_picture);

        moveToMain = findViewById(R.id.previous_main);
        moveToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuickPicture.this, MainActivity.class);
                startActivity(intent);
            }
        });

        quickPictureButtonCapture = findViewById(R.id.captureImage);
        quickPictureImage = findViewById(R.id.quickPictureImage);
        shareImageBtn = findViewById(R.id.shareImage);
        shareTextBtn = findViewById(R.id.shareText);
        shareImageTextBtn = findViewById(R.id.shareTextPicture);
        textNote = findViewById(R.id.textNote);

        //request for camera permission
        if(ContextCompat.checkSelfPermission(QuickPicture.this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(QuickPicture.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        quickPictureButtonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });


        //from youtube to share
        //handle click, get from gallery
        quickPictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        shareTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToShare = textNote.getText().toString().trim();
                if(TextUtils.isEmpty(textToShare)){
                    Toast.makeText(QuickPicture.this, "Enter text please...", Toast.LENGTH_LONG).show();
                }
                shareText();
            }
        });

        shareImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri == null) {
                    Toast.makeText(QuickPicture.this, "Pick image please...", Toast.LENGTH_LONG).show();
                }
                else{
                    shareImage();
                }
            }
        });


        shareImageTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting text
                textToShare = textNote.getText().toString().trim();
                //checking if text is empty or not
                if(TextUtils.isEmpty(textToShare)){
                    Toast.makeText(QuickPicture.this, "Enter text please...", Toast.LENGTH_LONG).show();
                }
                else if(imageUri == null){
                    Toast.makeText(QuickPicture.this, "Pick image please...", Toast.LENGTH_LONG).show();
                }
                else{
                    shareImageText();
                }
            }
        });
    }

    private void pickImage() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityLauncherResult.launch(intent);

    }

    private void shareText(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        //for sharing by email
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void shareImage(){
        Uri contentUri = getContentUri();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        //for sharing by email
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void shareImageText(){
        Uri contentUri = getContentUri();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        //for sharing by email
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private Uri getContentUri(){
        Bitmap bitmap = null;
        //get bitmap from uri
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            else{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
        }
        catch (Exception e){
            showToast("" + e.getMessage());
        }

        File imagesFolder = new File(getCacheDir(), "images");
        Uri contentUri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            stream.flush();
            stream.close();
            contentUri = FileProvider.getUriForFile(this, "com.example.mydocumentlibrary.fileProvider", file);
        }
        catch (Exception e){
            showToast("" + e.getMessage());
        }
        return contentUri;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private ActivityResultLauncher<Intent> galleryActivityLauncherResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //image picked
                        Toast.makeText(QuickPicture.this, "Image picked...", Toast.LENGTH_LONG).show();
                        //get image uri
                        Intent data = result.getData();
                        imageUri = data.getData();
                        //set to imageview
                        quickPictureImage.setImageURI(imageUri);
                    }
                    else{
                        //cancelled
                        Toast.makeText(QuickPicture.this, "Canceled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            quickPictureImage.setImageBitmap(bitmap);
        }
    }
}