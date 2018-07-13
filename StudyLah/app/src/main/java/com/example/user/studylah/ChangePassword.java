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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ChangePassword extends AppCompatActivity {

    private FirebaseUser user;
    private AuthCredential credentials;
    private String userEmail;

    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mRetypePassword;

    private Button mButtonChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialise user and required fields
        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        mOldPassword = findViewById(R.id.editTextOldPassword);
        mNewPassword = findViewById(R.id.editTextNewPassword);
        mRetypePassword = findViewById(R.id.editTextRetypePassword);
        mButtonChangePassword = findViewById(R.id.buttonChangePassword);

        mButtonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldPassword = mOldPassword.getText().toString().trim();
                final String newPassword = mNewPassword.getText().toString().trim();
                final String retypePassword = mRetypePassword.getText().toString().trim();

                if(TextUtils.isEmpty(oldPassword)) {
                    Toast.makeText(ChangePassword.this, "Enter Old Password", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(ChangePassword.this, "Enter New Password", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(retypePassword)) {
                    Toast.makeText(ChangePassword.this, "Re-type New Password", Toast.LENGTH_SHORT).show();
                }
                else if(!newPassword.equals(retypePassword)) {
                    Toast.makeText(ChangePassword.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Get credentials
                    credentials = EmailAuthProvider.getCredential(userEmail, oldPassword);

                    // Check credentials
                    user.reauthenticate(credentials).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                // Update password
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> reauth_task) {
                                        if(reauth_task.isSuccessful()) {
                                            Toast.makeText(ChangePassword.this, "Password Updated", Toast.LENGTH_SHORT).show();

                                            // Go back to main activity
                                            Intent intent = new Intent(ChangePassword.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(ChangePassword.this, "Error Password Not Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(ChangePassword.this, "Error Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
