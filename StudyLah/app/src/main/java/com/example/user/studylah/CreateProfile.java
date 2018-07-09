package com.example.user.studylah;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateProfile extends AppCompatActivity {
    private EditText mEditTextUsername;
    private Button mButtonCreateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Initialise widgets
        mEditTextUsername = (EditText)findViewById(R.id.editTextUsername);
        mButtonCreateProfile = (Button)findViewById(R.id.buttonCreateProfile);

        //button animation
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        mButtonCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                String username = mEditTextUsername.getText().toString();

                if(TextUtils.isEmpty(username)) {
                    Toast.makeText(CreateProfile.this, "Required Field Empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Update profile
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    user.updateProfile(profileUpdates);
                    // Go to Main
                    goMain();
                }
            }
        });
    }

    private void goMain() {
        Intent intent = new Intent(CreateProfile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
