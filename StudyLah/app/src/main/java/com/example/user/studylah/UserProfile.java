package com.example.user.studylah;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    // Database
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout
    CircleImageView mDisplayImage;
    Button mChangeBio;
    TextView mDisplayName;
    TextView mEmail;
    TextView mBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        // Initialise Layout
        mDisplayImage = (CircleImageView)findViewById(R.id.userImage);
        mDisplayName = (TextView)findViewById(R.id.displayName);
        mEmail = (TextView)findViewById(R.id.userEmail);
        mBio = (TextView)findViewById(R.id.bioText);
        mChangeBio = (Button)findViewById(R.id.changeBio);

        // Initialise variables
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        // Add Value Event Listener
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Change snapshot into a User class
                User user = new User();
                user = dataSnapshot.getValue(User.class);

                // Get information from snapshot
                String name = user.getUsername();
                String image = user.getImageLink();
                String email = user.getEmail();
                String bio = user.getBio();

                mDisplayName.setText(name);
                mEmail.setText(email);
                mBio.setText(bio);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Change Bio
        mChangeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserProfile.this);
                    LayoutInflater inflater = UserProfile.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.change_bio, null);
                    dialogBuilder.setView(dialogView);

                    final EditText edt = (EditText) dialogView.findViewById(R.id.editBio);

                    dialogBuilder.setMessage("Enter Biography below (Maximum 140 characters");
                    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Edit the users bio in firebase
                            String newBio = edt.getText().toString();
                            if(newBio.length() > 140) {
                                Toast.makeText(UserProfile.this, "Maximum number of characters is 140", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mUserDatabase.child("bio").setValue(newBio);
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //pass
                        }
                    });
                    AlertDialog b = dialogBuilder.create();
                    b.show();
                }
            }
        });
    }
}