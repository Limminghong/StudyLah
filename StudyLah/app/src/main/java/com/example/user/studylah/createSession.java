package com.example.user.studylah;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.VoiceInteractor;
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
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class createSession extends AppCompatActivity {
    private static final String TAG = "createSession";

    // Database
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private AutoCompleteTextView mAutoModule;
    private EditText mEditTextTiming;
    private EditText mEditTextTiming2;
    private EditText mEditTextDate;
    private EditText mEditTextLocation;

    private ArrayList<String> moduleCodes;
    private Hashtable moduleChecker;

    private Button mButtonCreate;
    private Button mButtonAddInfo;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener2;

    // Get Information
    private String information = "No Information Available";
    private String imageThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        // Initialise widgets
        mAutoModule = (AutoCompleteTextView) findViewById(R.id.autoModule);
        mEditTextTiming = (EditText)findViewById(R.id.editTextTiming);
        mEditTextTiming2 = (EditText)findViewById(R.id.editTextTiming2);
        mEditTextDate = (EditText) findViewById(R.id.editTextDate);
        mEditTextLocation = (EditText)findViewById(R.id.editTextLocation);

        mButtonCreate = (Button)findViewById(R.id.buttonCreate);
        mButtonAddInfo = (Button)findViewById(R.id.buttonInfo);

        moduleCodes = new ArrayList<String>();
        moduleChecker = new Hashtable();
        loadAutoData();

        // Load current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Reference to user database
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        // Retrieve user image thumbnail
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                user = dataSnapshot.getValue(User.class);
                imageThumb = user.getImageThumb();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Create button
        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkModuleValidity(mAutoModule.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Please Enter A Valid Module", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextTiming.getText().toString().trim()) || TextUtils.isEmpty(mEditTextTiming2.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Please Enter Timing", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mEditTextDate.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Please Enter Date", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextLocation.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Please Enter Location", Toast.LENGTH_SHORT).show();
                }
                else {
                    addNewSession();
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
                        createSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener,
                        hour, minute, is24Hour
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mEditTextTiming2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                boolean is24Hour = true;

                TimePickerDialog dialog = new TimePickerDialog(
                        createSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener2,
                        hour, minute, is24Hour
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if(minute < 10) {
                    time = hourOfDay + ":" +  "0" + minute;
                }
                else {
                    time = hourOfDay + ":" + minute;
                }
                mEditTextTiming.setText(time);
            }
        };

        mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if(minute < 10) {
                    time = hourOfDay + ":" +  "0" + minute;
                }
                else {
                    time = hourOfDay + ":" + minute;
                }
                mEditTextTiming2.setText(time);
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
                        createSession.this,
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

        mButtonAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(createSession.this);
                    LayoutInflater inflater = createSession.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.add_session_info, null);
                    dialogBuilder.setView(dialogView);

                    final EditText edt = (EditText) dialogView.findViewById(R.id.addInfo);

                    dialogBuilder.setMessage("Enter Session Information below (Maximum 140 characters)");
                    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Edit the users bio in firebase
                            String newInfo = edt.getText().toString();
                            if(newInfo.length() > 140) {
                                Toast.makeText(createSession.this, "Maximum number of characters is 140", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                information = newInfo;
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

    private void addNewSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String username = user.getDisplayName().toString();
        String hostId = user.getUid();
        String module = mAutoModule.getText().toString().trim();
        String timingFrom = mEditTextTiming.getText().toString().trim();
        String timingTo = mEditTextTiming2.getText().toString().trim();
        String date = mEditTextDate.getText().toString().trim();
        String location = mEditTextLocation.getText().toString().trim();
        long timestamp = convertToTimestamp(date, timingTo);
        // Write message to database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("sessions");
        // Session object to store info
        Session session = new Session();
        session.setHost(username);
        session.setModule(module);
        session.setTimingFrom(timingFrom);
        session.setTimingTo(timingTo);
        session.setDate(date);
        session.setLocation(location);
        session.setSessionInformation(information);
        session.setHostImage(imageThumb);
        session.setTimestamp(timestamp);
        session.setHostId(hostId);
        String sessionId = mDatabase.push().getKey();
        session.setId(sessionId);
        addSessionIntoUser(sessionId);
        mDatabase.child(sessionId).setValue(session);
    }

    private long convertToTimestamp(String date, String timing) {
        try {
            String myDate = date + " " + timing + ":00";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date timestamp = sdf.parse(myDate);
            long millis = timestamp.getTime();
            return  millis;
        }
        catch (Exception e){
            // default error
            return 0;
        }
    }

    private void addSessionIntoUser(String sessionId) {
        String current_uid = currentUser.getUid();
        DatabaseReference userSessionDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid).child("hostedSessions");

        Map hostingSession = new HashMap();
        hostingSession.put(sessionId, true);

        userSessionDatabaseRef.updateChildren(hostingSession);
    }

    private void backToMainActivity() {
        Intent intent = new Intent(createSession.this, MainActivity.class);
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
            mAutoModule.setAdapter(new ArrayAdapter<String>(createSession.this, android.R.layout.simple_spinner_dropdown_item, moduleCodes));
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
