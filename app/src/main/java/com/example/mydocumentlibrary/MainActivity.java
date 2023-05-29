package com.example.mydocumentlibrary;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    //storing for all users SecretsPasscode = "secret" then it would be updated for each user
    private DatabaseReference userRef;
    private DatabaseReference passwordRef;
    FloatingActionButton fab;

    //Moving to QuickPicture page
    Button moveToQuickPicture;

    private RecyclerView recyclerExpirations;
    private ExpirationsAdapter expirationAdapter;
    private List<PutPDF> documentListInExpirations;
    private DatabaseReference personalRef;
    private DatabaseReference educationalRef;
    private String title;
    private String deadlineDate;
    private String message;
    String userID;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyChannel";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_NOTIFICATION_POLICY_ACCESS = 1;


    //Adding new scan file
    FloatingActionButton floatingActionButton;

//    //QuickPicture Button
//    Button quickPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        passwordRef = FirebaseDatabase.getInstance().getReference().child("Passwords");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        fab = findViewById(R.id.fab);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashboard:
                    return true;
                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    finish();
                    return true;
                case R.id.notes:
                    startActivity(new Intent(getApplicationContext(), Notes.class));
                    finish();
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

        //storing default password for all users
        storeDefaultPasswordForAllUsers();


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

    private void storeDefaultPasswordForAllUsers() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (!snapshot.exists()){
                    String userID = userSnapshot.getKey();
                    DatabaseReference userPasswordRef = passwordRef.child(userID).child("passwordSecrets");
                    userPasswordRef.setValue("secret");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to add passwords for users", Toast.LENGTH_SHORT).show();
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