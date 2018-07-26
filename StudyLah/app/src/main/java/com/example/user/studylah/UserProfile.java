package com.example.user.studylah;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserProfile extends AppCompatActivity {
    // Database
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout
    // Image
    private CircleImageView mDisplayImage;
    // Buttons
    private Button mChangeBio;
    private Button mChangeImage;
    // TextViews
    private TextView mDisplayName;
    private TextView mEmail;
    private TextView mBio;
    // Ratings
    private RatingBar mRatingBar;
    // Constants
    private static final int GALLERY_PICK = 1;
    // Storage Reference
    private StorageReference mImageStorage;
    private ProgressDialog mProgessDialog;

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
        mChangeImage = (Button)findViewById(R.id.changeImage);
        mRatingBar = (RatingBar)findViewById(R.id.userRatingBar);

        // Initialise variables
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        // Add Value Event Listener
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Change snapshot into a User class
                User user = new User();
                user = dataSnapshot.getValue(User.class);

                // Get information from snapshot
                String name = user.getUsername();
                String email = user.getEmail();
                String bio = user.getBio();
                String imageThumb = user.getImageThumb();
                int avgStars = user.getAvgStars();

                mDisplayName.setText(name);
                mEmail.setText(email);
                mBio.setText(bio);
                mRatingBar.setRating(avgStars);

                // Load Image
                if(!imageThumb.equals("default")) {
                    Picasso.get().load(imageThumb).into(mDisplayImage);
                }
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

                    dialogBuilder.setMessage("Enter Biography below (Maximum 140 characters)");
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

        // Change Display Picture
        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgessDialog = new ProgressDialog(UserProfile.this);
                mProgessDialog.setTitle("Uploading Image...");
                mProgessDialog.setMessage("Please wait while we upload and process the image");
                mProgessDialog.setCanceledOnTouchOutside(false);
                mProgessDialog.show();

                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());

                final String current_user_id = mCurrentUser.getUid();
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_file);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                // Create Directory
                final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            mImageStorage.child("profile_images").child(current_user_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                            if(thumb_task.isSuccessful()) {
                                                mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        final String thumb_downloadUrl = uri.toString();

                                                        Map update_hashMap = new HashMap<>();
                                                        update_hashMap.put("imageLink", downloadUrl);
                                                        update_hashMap.put("imageThumb", thumb_downloadUrl);

                                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> image_task) {
                                                                if(image_task.isSuccessful()) {
                                                                    // Change all session images
                                                                    changeImages(thumb_downloadUrl);
                                                                    mProgessDialog.dismiss();
                                                                }
                                                                else {
                                                                    Toast.makeText(UserProfile.this, "Upload Failed During Process Dialog.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(UserProfile.this, "Upload Thumbnail Failed During Process Dialog.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            Toast.makeText(UserProfile.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
                            mProgessDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void changeImages(final String link) {
        final DatabaseReference userHostedSessions = mUserDatabase.child("hostedSessions");
        userHostedSessions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String session_key = ds.getKey();
                    final DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference("sessions").child(session_key);

                    sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Map update_map = new HashMap();
                                update_map.put("hostImage", link);
                                sessionRef.updateChildren(update_map);
                            }
                            else {
                                userHostedSessions.child(session_key).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}