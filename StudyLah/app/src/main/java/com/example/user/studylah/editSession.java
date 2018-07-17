package com.example.user.studylah;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class editSession extends AppCompatActivity {
    private AutoCompleteTextView mAutoModule;
    private EditText mEditTextTiming;
    private EditText mEditTextDate;
    private EditText mEditTextLocation;

    private int participantCount;
    private Map<String, Boolean> participants = new HashMap<>();

    private ArrayList<String> moduleCodes;
    private Hashtable moduleChecker;

    private Button mButtonEditInformation;
    private Button mButtonEdit;
    private Button mButtonDelete;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    // Get database
    String key;
    private FirebaseDatabase database;
    private DatabaseReference sessionRef;

    // Get user session database
    private FirebaseUser currentUser;
    private DatabaseReference userSessionRef;

    // Edit Information
    private String information = "No Information Available";
    private String imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);

        // Initialise database path
        key = getIntent().getStringExtra("KEY_EDIT");
        database = FirebaseDatabase.getInstance();
        sessionRef = database.getReference("/sessions/" + key);

        // Initialise current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialise widgets
        mAutoModule = (AutoCompleteTextView)findViewById(R.id.editAutoModule);
        mEditTextTiming = (EditText)findViewById(R.id.editEditTextTiming);
        mEditTextDate = (EditText) findViewById(R.id.editEditTextDate);
        mEditTextLocation = (EditText)findViewById(R.id.editEditTextLocation);

        mButtonEditInformation = (Button)findViewById(R.id.buttonEditInfo);
        mButtonEdit = (Button)findViewById(R.id.editButtonEdit);
        mButtonDelete = (Button)findViewById(R.id.editButtonDelete);

        moduleCodes = new ArrayList<String>();
        moduleChecker = new Hashtable();
        loadAutoData();

        // Copy previous data
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Session session = dataSnapshot.getValue(Session.class);
                mAutoModule.setText(session.getModule());
                mEditTextTiming.setText(session.getTiming());
                mEditTextDate.setText(session.getdate());
                mEditTextLocation.setText(session.getLocation());
                participantCount = session.getParticipantCount();
                participants = session.getParticipants();
                imageLink = session.getHostImage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        */

        // Create button
        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkModuleValidity(mAutoModule.getText().toString().trim())) {
                    Toast.makeText(editSession.this, "Please Enter A Valid Module", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextTiming.getText().toString().trim())) {
                    Toast.makeText(editSession.this, "Please Enter Timing", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mEditTextDate.getText().toString().trim())) {
                    Toast.makeText(editSession.this, "Please Enter Date", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextLocation.getText().toString().trim())) {
                    Toast.makeText(editSession.this, "Please Enter Location", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateSession();
                    backToMainActivity();
                }
            }
        });

        mEditTextTiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                boolean is24Hour = true;

                TimePickerDialog dialog = new TimePickerDialog(
                        editSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener,
                        hour, minute, is24Hour
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + ":" + minute;
                mEditTextTiming.setText(time);
            }
        };

        mEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        editSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                mEditTextDate.setText(date);
            }
        };

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDig = new AlertDialog.Builder(editSession.this);
                alertDig.setMessage("Are you sure you want to delete this session?");
                alertDig.setCancelable(false);

                alertDig.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete session from user
                        String current_uid = currentUser.getUid();
                        userSessionRef = database.getInstance().getReference().child("users").child(current_uid).child("hostedSessions").child(key);
                        userSessionRef.removeValue();

                        // Delete session
                        sessionRef.removeValue();
                        backToMainActivity();
                    }
                });

                alertDig.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDig.create().show();
            }
        });

        mButtonEditInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(editSession.this);
                    LayoutInflater inflater = editSession.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.edit_session_info, null);
                    dialogBuilder.setView(dialogView);

                    final EditText edt = (EditText) dialogView.findViewById(R.id.editInfo);

                    sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Session session = new Session();
                            session = dataSnapshot.getValue(Session.class);
                            edt.setText(session.getSessionInformation());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    dialogBuilder.setMessage("Edit Session Information below (Maximum 140 characters)");
                    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Edit the users bio in firebase
                            String newInfo = edt.getText().toString();
                            if(newInfo.length() > 140) {
                                Toast.makeText(editSession.this, "Maximum number of characters is 140", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(!newInfo.equals("")){
                                    information = newInfo;
                                }
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

    /*@Override
    public boolean onSupportNavigateUp() {
        backToHost();
        return true;
    }*/

    // Function to update session
    private void updateSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String username = user.getDisplayName().toString();
        String module = mAutoModule.getText().toString().trim();
        String timing = mEditTextTiming.getText().toString().trim();
        String date = mEditTextDate.getText().toString().trim();
        String location = mEditTextLocation.getText().toString().trim();
        // Write message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("sessions");
        // Create a new session
        Session session = new Session();
        session.setId(key);
        session.setHost(username);
        session.setModule(module);
        session.setTiming(timing);
        session.setDate(date);
        session.setLocation(location);
        session.setParticipantCount(participantCount);
        session.setParticipants(participants);
        session.setHostImage(imageLink);
        session.setSessionInformation(information);
        mDatabase.child(key).setValue(session);
    }

    private void backToMainActivity() {
        Intent intent = new Intent(editSession.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("moduleList.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadAutoData() {
        try {
            JSONArray moduleList = new JSONArray(loadJSONFromAsset());
            for(int i = 0; i < moduleList.length(); i++) {
                JSONObject modules = moduleList.getJSONObject(i);
                String module = modules.getString("ModuleCode");
                moduleCodes.add(module);
                moduleChecker.put(module, new Integer(1));
            }
            mAutoModule.setAdapter(new ArrayAdapter<String>(editSession.this, android.R.layout.simple_spinner_dropdown_item, moduleCodes));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkModuleValidity(String mod) {
        if (!moduleChecker.containsKey(mod)) {
            return true;
        }
        else if (TextUtils.isEmpty(mod)) {
            return true;
        }
        else return false;
    }
}
