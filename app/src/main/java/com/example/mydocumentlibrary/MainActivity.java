package com.example.mydocumentlibrary;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mydocumentlibrary.categories.BillsPage;
import com.example.mydocumentlibrary.categories.EducationPage;
import com.example.mydocumentlibrary.categories.FriendsPage;
import com.example.mydocumentlibrary.categories.HealthPage;
import com.example.mydocumentlibrary.categories.JobPage;
import com.example.mydocumentlibrary.categories.PersonalPage;
import com.example.mydocumentlibrary.categories.SecretsPage;
import com.example.mydocumentlibrary.categories.SecretsPasscodeActivity;
import com.example.mydocumentlibrary.categories.TravelPage;
import com.example.mydocumentlibrary.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    //Navigation Bar activities
    ActivityMainBinding binding;

    //Moving from main page to categories
    private ImageButton moveToPersonalPage;
    private ImageButton moveToEducationPage;
    private ImageButton moveToJobPage;
    private ImageButton moveToFriendsPage;
    private ImageButton moveToTravelPage;
    private ImageButton moveToHealthPage;
    private ImageButton moveToBillsPage;
    private ImageButton moveToSecretPage;

    //Moving to QuickPicture page
    Button moveToQuickPicture;


    //Adding new scan file
    FloatingActionButton floatingActionButton;

//    //QuickPicture Button
//    Button quickPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashboard:
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
        //Navigation Bar activities
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

//        replaceFragment(new NavigationFragment());
//        binding.bottomNavigationView.setBackground(null);

//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()){
//                case R.id.navigation_frag:
//                    replaceFragment(new NavigationFragment());
//                    break;
//
//                case R.id.account:
//                    replaceFragment(new AccountFragment());
//                    break;
//
//                case R.id.fab:
//                    replaceFragment(new AddingNewFragment());
//                    break;
//
//                case R.id.notes:
//                    replaceFragment(new NotesFragment());
//                    break;
//
//                case R.id.notification:
//                    replaceFragment(new NotificationFragment());
//                    break;
//            }
//            return true;
//        });


        //setContentView(R.layout.activity_main); это было в начале автоматически

        //moving from main nav page to personal page
        moveToPersonalPage = findViewById(R.id.personalPage);
        moveToPersonalPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalPage.class);
                startActivity(intent);
            }
        });

        moveToEducationPage = findViewById(R.id.educationPage);
        moveToEducationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EducationPage.class);
                startActivity(intent);
            }
        });

        moveToJobPage = findViewById(R.id.jobPage);
        moveToJobPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JobPage.class);
                startActivity(intent);
            }
        });

        moveToHealthPage = findViewById(R.id.healthPage);
        moveToHealthPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HealthPage.class);
                startActivity(intent);
            }
        });

        moveToTravelPage = findViewById(R.id.travelPage);
        moveToTravelPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TravelPage.class);
                startActivity(intent);
            }
        });

        moveToBillsPage = findViewById(R.id.billPage);
        moveToBillsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BillsPage.class);
                startActivity(intent);
            }
        });

        moveToFriendsPage = findViewById(R.id.friendsPage);
        moveToFriendsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FriendsPage.class);
                startActivity(intent);
            }
        });

        moveToSecretPage = findViewById(R.id.secretsPage);
        moveToSecretPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecretsPasscodeActivity.class);
                startActivity(intent);
            }
        });

        //Move to QuickPicture
        moveToQuickPicture = findViewById(R.id.quickPictureButton);
        moveToQuickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuickPicture.class);
                startActivity(intent);
            }
        });
    }

//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, fragment);
//        fragmentTransaction.commit();
//    }
}