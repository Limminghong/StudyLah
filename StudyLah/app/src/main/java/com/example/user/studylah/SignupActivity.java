package com.example.user.studylah;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SignupActivity extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mBtnSignup;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialise widgets
        mEditTextEmail = findViewById(R.id.editTextEmailSignup);
        mEditTextPw = findViewById(R.id.editTextPasswordSignup);
        mBtnSignup = findViewById(R.id.buttonSignup);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        //button animation
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();
                // Check if email is empty
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                }

                // Check if password is empty
                else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }

                // Set your own additional constraints

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
                                        signUpSuccess();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void signUpSuccess() {
        // Signup successful, got to main activity
        Intent intent = new Intent(SignupActivity.this, CreateProfile.class);
        startActivity(intent);
        // End the activity
        finish();
    }
}
