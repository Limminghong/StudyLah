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
    ArrayList<String> list3;
    ArrayAdapter<String> adapter3;
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
        list3 = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter3 = new ArrayAdapter<>(getActivity(),R.layout.session,R.id.module,list3);

        //button animation
        final Animation animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);

        ValueEventListener valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                //Clear the list and sessionId
                list3.clear();
                sessionId.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    String module_info = session.getModule() + "\n" +
                            "Host: " + session.getHost() + "\n" +
                            "Timing: " + session.getTiming() + "\n" +
                            "Date: " + session.getdate() + "\n" +
                            "Location: " + session.getLocation();

                    if (session.getHost().equals(name)){
                        list3.add(module_info);
                        sessionId.put(list3.indexOf(module_info), session.getId());
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

                alertDig.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Goes to the edit activity
                        Intent intent = new Intent(getActivity(), editSession.class);
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
