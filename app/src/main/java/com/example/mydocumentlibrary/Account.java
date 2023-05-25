package com.example.mydocumentlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydocumentlibrary.authentication.LoginActivity;
import com.example.mydocumentlibrary.authentication.SignUpActivity;
import com.example.mydocumentlibrary.categories.SecretsPasscodeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button logoutBtn, deleteAccountBtn, findFriendsBtn, changeSecretsPass;
    private TextView userEmail;
    private String userID;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        logoutBtn = findViewById(R.id.logout_button);
        deleteAccountBtn = findViewById(R.id.delete_account_button);
        userEmail = findViewById(R.id.user_email);
        findFriendsBtn = findViewById(R.id.findFriends_button);
        changeSecretsPass = findViewById(R.id.secret_passcode_button);
        fab = findViewById(R.id.fab);

        //Saving from authentication to the database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String UsersID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = firebaseDatabase.getReference("Users").child(UsersID);
        String usersEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Users users = new Users(usersEmail, UsersID);
        usersRef.setValue(users);


        if(firebaseUser!=null){
            String email = firebaseUser.getEmail();
            userEmail.setText(email);
        }

        findFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, FindFriendActivity.class);
                startActivity(intent);
                finish();
            }
        });

        changeSecretsPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, PasscodeChanging.class);
                startActivity(intent);
                finish();
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                Intent intent = new Intent(Account.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(Account.this, "Successful logout", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!= null){
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(Account.this, SignUpActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(Account.this, "User Account deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Account.this, "Failed on deleting user account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashboard:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.account:
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
}