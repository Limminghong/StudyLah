package com.example.user.studylah;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText mEditTextUsername;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mBtnSignup;
    private FirebaseAuth auth;

    private Map<String, Integer> nameChecker = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialise widgets
        mEditTextEmail = findViewById(R.id.editTextEmailSignup);
        mEditTextPw = findViewById(R.id.editTextPasswordSignup);
        mEditTextUsername = (EditText)findViewById(R.id.editTextUsername);
        mBtnSignup = findViewById(R.id.buttonSignup);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        // Populate nameChecker
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("username");

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String username = ds.getKey();
                    nameChecker.put(username, 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //button animation
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEditTextPw.getText().toString().trim();
                final String username = mEditTextUsername.getText().toString();
                // Check if email is empty
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                }

                // Check if password is empty
                else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(username)) {
                    Toast.makeText(SignupActivity.this, "Required Field Empty", Toast.LENGTH_SHORT).show();
                }

                else if(nameChecker.containsKey(username)) {
                    Toast.makeText(SignupActivity.this, "Username Has Been Used", Toast.LENGTH_SHORT).show();
                }

                else {
                    // Create a new user
                    auth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // User sign up successful
                                        signUpSuccess(username, email);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void signUpSuccess(String username, String email) {
        // Save username
        FirebaseUser user = auth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        user.updateProfile(profileUpdates);
        // Create user profile and add to database
        String uid = user.getUid();
        createProfile(uid, username, email);
        saveUsername(username);
        // Signup successful, got to main activity
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        // End the activity
        finish();
    }

    private void createProfile(String uid, String uName, String email) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(uName, "default", "default", email, "Default bio");
        mDatabase.child(uid).setValue(user);
    }

    private void saveUsername(String username) {
        DatabaseReference usernameDatabase = FirebaseDatabase.getInstance().getReference("username");
        Map names = new HashMap<>();
        names.put(username, true);
        usernameDatabase.updateChildren(names);
    }
}
