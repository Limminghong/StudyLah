package com.example.user.studylah;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private TextView mTextViewSignup;
    private Button mButtonLogin;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private CheckBox mRemember;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        // Initialise widgets
        mButtonLogin = findViewById(R.id.buttonLogin);
        mEditTextEmail = findViewById(R.id.editTextEmailLogin);
        mEditTextPw = findViewById(R.id.editTextPasswordLogin);
        mTextViewSignup = findViewById(R.id.textViewSignup);
        mRemember = findViewById(R.id.checkBoxRmbMe);

        // Initialise Paper
        Paper.init(this);

        //button animation
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        mTextViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Check if email and password has been remembered
        String paperEmail = Paper.book().read("Email");
        String paperPwd = Paper.book().read("Password");
        if(paperEmail != null && paperPwd != null) {
            mEditTextEmail.setText(paperEmail);
            mEditTextPw.setText(paperPwd);
        }

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);

                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEditTextPw.getText().toString().trim();
                // Check if email is empty
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                }

                // Check if password is empty
                else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }

                // Set your own additional constraints

                else {
                    // Authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()) {
                                        // Error occurred
                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(mRemember.isChecked()){
                                            Paper.book().write("Email", email);
                                            Paper.book().write("Password", password);
                                        }

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}
