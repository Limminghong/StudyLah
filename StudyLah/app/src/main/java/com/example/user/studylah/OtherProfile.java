package com.example.user.studylah;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfile extends AppCompatActivity {

    // Widgets
    private CircleImageView mOtherImage;
    private TextView mOtherName;
    private TextView mOtherEmail;
    private TextView mOtherBio;
    private Button buttonFeedback;
    private Button buttonReport;

    // User database reference
    private String userId;
    DatabaseReference otherUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        // Initialise Widgets
        mOtherImage = (CircleImageView)findViewById(R.id.otherImage);
        mOtherName = (TextView)findViewById(R.id.otherName);
        mOtherEmail = (TextView)findViewById(R.id.otherEmail);
        mOtherBio = (TextView)findViewById(R.id.otherBio);
        buttonFeedback = (Button)findViewById(R.id.giveFeedback);
        buttonReport = (Button)findViewById(R.id.reportUser);

        // Get user reference
        userId = getIntent().getStringExtra("HOST_ID");
        otherUserReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Retrieve user information
        otherUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                user = dataSnapshot.getValue(User.class);

                // Get information from snapshot
                String name = user.getUsername();
                String email = user.getEmail();
                String bio = user.getBio();
                String imageThumb = user.getImageThumb();

                mOtherName.setText(name);
                mOtherEmail.setText(email);
                mOtherBio.setText(bio);

                // Load Image
                if(!imageThumb.equals("default")) {
                    Picasso.get().load(imageThumb).into(mOtherImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
