package com.example.mydocumentlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mydocumentlibrary.categories.SecretsPage;
import com.example.mydocumentlibrary.categories.SecretsPasscodeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasscodeChanging extends AppCompatActivity {

    EditText oldPass, newPass, verifyPass;
    Button updatePass;
    private DatabaseReference databaseReference;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_changing);

        oldPass = findViewById(R.id.oldPassEdit);
        newPass = findViewById(R.id.newPassEdit);
        verifyPass = findViewById(R.id.newPassVerifyEdit);
        updatePass = findViewById(R.id.updatePass);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Passwords").child(uid);

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword(v);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        fab = findViewById(R.id.fab);

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

    public void updatePassword(View view) {
        String oldPassword = oldPass.getText().toString();
        String newPassword = newPass.getText().toString();
        String verifyPassword = verifyPass.getText().toString();

        // Perform validation and checks for the old password, new password, and verification
        if (oldPassword.isEmpty() || newPassword.isEmpty() || verifyPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(verifyPassword)) {
            Toast.makeText(getApplicationContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // Here, you can compare the old password with the existing password stored in your database
            // If the old password matches, you can proceed to update the password
            DatabaseReference existPasswordRef = databaseReference.child("passwordSecrets");

            existPasswordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String existPassword = dataSnapshot.getValue(String.class);

                    if (existPassword != null && existPassword.equals(oldPassword)) {
                        existPasswordRef.setValue(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Save the new password in the "Passwords" node with the user's ID as the key
                                        databaseReference.child("passwordSecrets").setValue(newPassword)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(PasscodeChanging.this, SecretsPage.class);
                                                        startActivity(intent);
                                                        finish(); // Optional: Finish the current activity to prevent going back
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Old Password is not correct" + existPassword, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Failed to read existing password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}