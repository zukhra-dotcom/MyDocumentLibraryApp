package com.example.mydocumentlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Notifications extends AppCompatActivity {

    private RecyclerView recyclerExpirations;
    private ExpirationsAdapter expirationAdapter;
    private List<PutPDF> documentListInExpirations;
    private DatabaseReference personalRef;
    private DatabaseReference educationalRef;
    private String title;
    private String deadlineDate;
    private String message;
    FloatingActionButton fab;
    String userID;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyChannel";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerExpirations = findViewById(R.id.recyclerExpirationDateFiles);
        recyclerExpirations.setLayoutManager(new LinearLayoutManager(this));

        documentListInExpirations = new ArrayList<>();
        expirationAdapter = new ExpirationsAdapter(documentListInExpirations);
        recyclerExpirations.setAdapter(expirationAdapter);
        fab = findViewById(R.id.fab);

        createNotificationChannel();
        retrieveDocumentsFromDatabase();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.notification);

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
                    startActivity(new Intent(getApplicationContext(), Notes.class));
                    finish();
                    return true;
                case R.id.notification:
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Notification Channel";
            String channelDescription = "Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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

        ChildEventListener personalChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                addDocumentToList(snapshot, personalRef);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle document changes if necessary
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle document removal if necessary
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle document movement if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notifications.this, "Error in retrieving personal documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        ChildEventListener educationalChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                addDocumentToList(snapshot, educationalRef);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle document changes if necessary
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle document removal if necessary
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle document movement if necessary
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Notifications.this, "Error in retrieving educational documents: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };


        personalRef.addChildEventListener(personalChildEventListener);
        educationalRef.addChildEventListener(educationalChildEventListener);
    }

    private void addDocumentToList(DataSnapshot snapshot, DatabaseReference documentRef) {
        PutPDF putPDF = snapshot.getValue(PutPDF.class);
        if (putPDF != null && putPDF.getDeadlineDate() != null && !putPDF.getDeadlineDate().isEmpty()) {
            documentListInExpirations.add(putPDF);
            Collections.sort(documentListInExpirations, new Comparator<PutPDF>() {
                @Override
                public int compare(PutPDF document1, PutPDF document2) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    try {
                        Date deadlineDate1 = dateFormat.parse(document1.getDeadlineDate());
                        Date deadlineDate2 = dateFormat.parse(document2.getDeadlineDate());
                        return deadlineDate1.compareTo(deadlineDate2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            View itemView = LayoutInflater.from(Notifications.this).inflate(R.layout.single_view_files, null);
            TextView fileTypeTextView = itemView.findViewById(R.id.file_type);
            if (documentRef.equals(personalRef)) {
                fileTypeTextView.setText("Personal");
            } else if (documentRef.equals(educationalRef)) {
                fileTypeTextView.setText("Educational");
            }

            checkAndNotifyExpirations(putPDF);
            expirationAdapter.notifyDataSetChanged();
        }
    }

    private void checkAndNotifyExpirations(PutPDF putPDF) {
        Log.d("Notification", "checkAndNotifyExpirations() called");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date expirationDate = dateFormat.parse(putPDF.getDeadlineDate());
            Date today = new Date();

            if (expirationDate != null && today.before(expirationDate)) {
                long diffInMillis = expirationDate.getTime() - today.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

                if (diffInDays == 2) {
                    // Generate a unique notification ID for each document
                    int notificationId = putPDF.getName().hashCode();

                    // Show the notification
                    showNotification(putPDF.getName(), putPDF.getDeadlineDate(), putPDF.getNotes(), notificationId);
                }
            }
            else if (expirationDate != null && today.after(expirationDate)) {
                String expiredNote = putPDF.getNotes() + " EXPIRED!!!";
                putPDF.setNotes(expiredNote);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String title, String deadlineDate, String message, int notificationId) {
        // Retrieve the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = dateFormat.format(new Date());

        try {
            // Parse the deadline date
            Date deadline = dateFormat.parse(deadlineDate);

            // Calculate the number of days remaining until the deadline
            long diffInMillies = deadline.getTime() - dateFormat.parse(currentDate).getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diffInDays == 2) {
                // Show the notification if there are 2 days remaining until the deadline
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Your document: " + title + " expires in 2 days!!!!")
                        .setContentText("on " + deadlineDate)
                        .setContentText("notes: " + message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM);

                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, PERMISSION_REQUEST_CODE);
                } else {
                    // Permission is already granted, show the notification
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(notificationId, builder.build());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, show the notifications

                for (int i = 0; i < documentListInExpirations.size(); i++) {
                    PutPDF document = documentListInExpirations.get(i);
                    showNotification(document.getName(), document.getDeadlineDate(), document.getNotes(), i);
                }
            } else {
                // Permission is not granted, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Permission not granted for posting notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }
}