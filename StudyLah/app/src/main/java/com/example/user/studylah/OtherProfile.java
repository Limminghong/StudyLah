package com.example.user.studylah;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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
    private Button buttonRate;
    private RatingBar mOtherBar;

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
        buttonRate = (Button)findViewById(R.id.rateUser);
        mOtherBar = (RatingBar) findViewById(R.id.otherRatingBar);

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
                int avgStars = user.getAvgStars();

                mOtherName.setText(name);
                mOtherEmail.setText(email);
                mOtherBio.setText(bio);
                mOtherBar.setRating(avgStars);

                // Load Image
                if(!imageThumb.equals("default")) {
                    Picasso.get().load(imageThumb).into(mOtherImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Rate user
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = OtherProfile.this.getLayoutInflater();
                AlertDialog.Builder alertDig = new AlertDialog.Builder(OtherProfile.this);
                final View dialogView = inflater.inflate(R.layout.rate_user_dialog, null);
                alertDig.setView(dialogView);
                alertDig.setCancelable(false);

                alertDig.setMessage("Rate this user");
                alertDig.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDig.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDig.create().show();
            }
        });
    }
}
