package com.example.user.studylah;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private EditText mEmail;
    private Button mResetPassword;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialise widgets
        mEmail = findViewById(R.id.resetEmail);
        mResetPassword = findViewById(R.id.buttonReset);

        // Check if email is valid, if so, send email
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String resetEmail = mEmail.getText().toString().trim();

                if(TextUtils.isEmpty(resetEmail)) {
                    Toast.makeText(ForgetPassword.this, "Please Enter Your Email Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ForgetPassword.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(ForgetPassword.this, "Email Failed To Send", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
