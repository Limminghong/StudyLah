package com.example.user.studylah;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

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
    private String currentId;
    DatabaseReference personalUserReference;
    private Map<String, Float> listRated;

    // Stars
    private float currentStars;
    private int numberOfRaters;

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

        // Get personal reference
        currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        personalUserReference = FirebaseDatabase.getInstance().getReference("users").child(currentId);
        listRated = new HashMap<>();

        // Get a map of rated users;
        personalUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRated.clear();
                User user = new User();
                user = dataSnapshot.getValue(User.class);

                listRated = user.getRated();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                float avgStars = user.getAvgStars();
                currentStars = user.getStars();
                numberOfRaters = user.getNumRaters();

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

                final RatingBar mCurrentRating = (RatingBar)dialogView.findViewById(R.id.newRatingBar);

                alertDig.setMessage("Rate this user");
                alertDig.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listRated.containsKey(userId)) {
                            // Replace the rating
                            float prevUserRating = listRated.get(userId);
                            float currentUserRating = mCurrentRating.getRating();
                            float newAvgRating;

                            currentStars -= prevUserRating;
                            currentStars += currentUserRating;

                            newAvgRating = currentStars / (float) numberOfRaters;

                            updateListRater(userId, currentUserRating, currentStars, newAvgRating, numberOfRaters);

                        }
                        else {
                            // Add new rating
                            float newUserRating = mCurrentRating.getRating();
                            float newAvgRating;
                            int newNumberOfRaters = numberOfRaters + 1;

                            currentStars += newUserRating;
                            newAvgRating = currentStars / newNumberOfRaters;

                            updateListRater(userId, newUserRating, currentStars, newAvgRating, newNumberOfRaters);
                        }
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

    private void updateListRater(String userId, float givenStars, float newStars, float newAvgRating, int newNumberOfRaters) {
        otherUserReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        personalUserReference = FirebaseDatabase.getInstance().getReference("users").child(currentId);
        Map updates_self = new HashMap();
        Map updates_others = new HashMap();

        updates_self.put(userId, givenStars);

        updates_others.put("stars", newStars);
        updates_others.put("avgStars", newAvgRating);
        updates_others.put("numRaters", newNumberOfRaters);

        personalUserReference.child("rated").updateChildren(updates_self);
        otherUserReference.updateChildren(updates_others);
    }
}
