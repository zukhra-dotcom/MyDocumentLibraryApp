package com.example.mydocumentlibrary.categories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.MainActivity;
import com.example.mydocumentlibrary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SecretsPasscodeActivity extends AppCompatActivity {
    private TextView enteringLocked;
    private EditText password;
    private Button enter, moveToMain;
    private TextView attempts, defaultPass;
    private TextView numberOfAttempts;

    //number of attempts to enter
    int numberOfRemainingLoginAttempts = 3;
    private DatabaseReference passwordRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secrets_passcode);

        password = (EditText) findViewById(R.id.edit_password);
        enter = (Button) findViewById(R.id.button_enter);
        moveToMain = (Button) findViewById(R.id.previous_main);
        enteringLocked = (TextView) findViewById(R.id.entering_locked);
        attempts = (TextView) findViewById(R.id.attempts);
        defaultPass = (TextView) findViewById(R.id.password_textDefault);
        numberOfAttempts = (TextView) findViewById(R.id.number_of_attempts);
        numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        passwordRef = FirebaseDatabase.getInstance().getReference().child("Passwords").child(uid).child("passwordSecrets");

        moveToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecretsPasscodeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void Login(View view) {

        String enteredPassword = password.getText().toString();
        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String storedPassword = dataSnapshot.getValue(String.class);

                if (storedPassword != null && enteredPassword.equals(storedPassword)) {
                    defaultPass.setVisibility(View.GONE);
                    Toast.makeText(SecretsPasscodeActivity.this, "Successfully entered", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SecretsPasscodeActivity.this, SecretsPage.class);
                    startActivity(intent);
                } else {
                    defaultPass.setVisibility(View.VISIBLE);
                    Toast.makeText(SecretsPasscodeActivity.this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
                    numberOfRemainingLoginAttempts--;

                    attempts.setVisibility(View.VISIBLE);
                    numberOfAttempts.setVisibility(View.VISIBLE);
                    numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));

                    if (numberOfRemainingLoginAttempts == 0) {
                        enter.setEnabled(false);
                        enteringLocked.setVisibility(View.VISIBLE);
                        enteringLocked.setBackgroundColor(Color.RED);
                        enteringLocked.setText("Entering blocked!!!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SecretsPasscodeActivity.this, "Failed to read password from the database", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
