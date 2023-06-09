package com.example.mydocumentlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ScanActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imageView = (ImageView)findViewById(R.id.image_view);
        Uri image_uri = getIntent().getData();
        imageView.setImageURI(image_uri);
    }
}