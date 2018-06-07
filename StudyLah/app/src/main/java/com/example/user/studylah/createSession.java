package com.example.user.studylah;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createSession extends AppCompatActivity {
    private EditText mEditTextModule;
    private EditText mEditTextTiming;
    private EditText mEditTextDate;
    private EditText mEditTextLocation;

    private Button mButtonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        // Initialise widgets
        mEditTextModule = (EditText)findViewById(R.id.editTextModule);
        mEditTextTiming = (EditText)findViewById(R.id.editTextTiming);
        mEditTextDate = (EditText) findViewById(R.id.editTextDate);
        mEditTextLocation = (EditText)findViewById(R.id.editTextLocation);

        mButtonCreate = (Button)findViewById(R.id.buttonCreate);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        */

        // Create button
        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mEditTextModule.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Enter Module(s)", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextTiming.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Enter Timing", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mEditTextDate.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Enter Date", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextLocation.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Enter Location", Toast.LENGTH_SHORT).show();
                }
                else {
                    addNewSession();
                    backToMainActivity();
                }
            }
        });
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        backToHost();
        return true;
    }*/

    private void addNewSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String username = user.getDisplayName().toString();
        String module = mEditTextModule.getText().toString().trim();
        String timing = mEditTextTiming.getText().toString().trim();
        String date = mEditTextDate.getText().toString().trim();
        String location = mEditTextLocation.getText().toString().trim();
        // Write message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("sessions");
        // Session object to store info
        Session session = new Session();
        session.setHost(username);
        session.setModule(module);
        session.setTiming(timing);
        session.setDate(date);
        session.setLocation(location);
        String sessionId = mDatabase.push().getKey();
        mDatabase.child(sessionId).setValue(session);
    }

    private void backToMainActivity() {
        Intent intent = new Intent(createSession.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
