package com.example.user.studylah;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class createSession extends AppCompatActivity {
    private static final String TAG = "createSession";

    private AutoCompleteTextView mAutoModule;
    private EditText mEditTextTiming;
    private EditText mEditTextDate;
    private EditText mEditTextLocation;

    private ArrayList<String> moduleCodes;
    private Hashtable moduleChecker;

    private Button mButtonCreate;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        // Initialise widgets
        mAutoModule = (AutoCompleteTextView) findViewById(R.id.autoModule);
        mEditTextTiming = (EditText)findViewById(R.id.editTextTiming);
        mEditTextDate = (EditText) findViewById(R.id.editTextDate);
        mEditTextLocation = (EditText)findViewById(R.id.editTextLocation);

        mButtonCreate = (Button)findViewById(R.id.buttonCreate);

        moduleCodes = new ArrayList<String>();
        moduleChecker = new Hashtable();
        loadAutoData();

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        */

        // Create button
        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkModuleValidity(mAutoModule.getText().toString().trim())) {
                    Toast.makeText(createSession.this, "Please Enter A Valid Module", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mEditTextTiming.getText().toString().trim())) {
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
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        backToHost();
        return true;
    }*/

    private void addNewSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String username = user.getDisplayName().toString();
        String module = mAutoModule.getText().toString().trim();
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
        session.setId(sessionId);
        mDatabase.child(sessionId).setValue(session);
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
