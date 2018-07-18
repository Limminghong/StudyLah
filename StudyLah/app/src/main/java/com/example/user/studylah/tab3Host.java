package com.example.user.studylah;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class tab3Host extends Fragment {
    Button mButtonHost;

    ListView hostView;
    // Authenticating Firebase for session list
    FirebaseDatabase database;

    //get the name of the currently signed in user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //name of the user
    String name = user.getDisplayName();

    //make reference to the database used
    DatabaseReference ref;

    //Creating list to store all the session information
    ArrayList<String> listModule3;
    ArrayList<String> listHost3;
    ArrayList<String> listTiming3;
    ArrayList<String> listDate3;
    ArrayList<String> listLocation3;
    ArrayList<String> listImage3;
    CustomListAdapter adapter3;
    Session session;

    //Creating array to store all the id
    Map<Integer, String> sessionId = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_host, container, false);

        session = new Session();
        hostView = (ListView) rootView.findViewById(R.id.hostView);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("sessions");

        //initialise list
        listModule3 = new ArrayList<>();
        listHost3 = new ArrayList<>();
        listTiming3 = new ArrayList<>();
        listDate3 = new ArrayList<>();
        listLocation3 = new ArrayList<>();
        listImage3 = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter3 = new CustomListAdapter(getActivity(), listModule3, listHost3, listTiming3, listDate3, listLocation3, listImage3);

        //button animation
        final Animation animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);

        ValueEventListener valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                //Clear the list and sessionId
                listModule3.clear();
                listImage3.clear();
                listHost3.clear();
                listTiming3.clear();
                listDate3.clear();
                listLocation3.clear();
                sessionId.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);

                    String module_name = session.getModule();
                    String module_host = "Host: " + session.getHost();
                    String module_timing = "Timing: " + session.getTimingFrom() + " - " + session.getTimingTo();
                    String module_date = "Date: " + session.getdate();
                    String module_location = "Location: " + session.getLocation();
                    String imageLink = session.getHostImage();

                    if (session.getHost().equals(name)){
                        listModule3.add(module_name);
                        listHost3.add(module_host);
                        listTiming3.add(module_timing);
                        listDate3.add(module_date);
                        listLocation3.add(module_location);
                        listImage3.add(imageLink);
                        sessionId.put(listModule3.indexOf(module_name), session.getId());
                    }
                }
                hostView.setAdapter(adapter3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Host button
        mButtonHost = (Button) rootView.findViewById(R.id.buttonHost);
        mButtonHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(getActivity(), createSession.class);
                startActivity(intent);
            }
        });

        hostView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
                alertDig.setMessage("Do you want to edit this session?");
                alertDig.setCancelable(false);

                final int pos = i;
                alertDig.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Goes to the edit activity
                        String key = sessionId.get(pos);
                        Intent intent = new Intent(getActivity(), editSession.class);
                        intent.putExtra("KEY_EDIT", key);
                        startActivity(intent);
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

        return rootView;
    }
}
