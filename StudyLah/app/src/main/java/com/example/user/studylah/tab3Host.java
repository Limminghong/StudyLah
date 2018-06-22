package com.example.user.studylah;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class tab3Host extends Fragment {
    Button mButtonHost;

    ListView hostView;
    // Authenticating Firebase for session list
    FirebaseDatabase database;

    //get the name of the currently signed in user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //name of the user
    String name = user.getDisplayName().toString();

    //make reference to the database used
    DatabaseReference ref;

    //Creating list to store all the session information
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_host, container, false);

        session = new Session();
        hostView = (ListView) rootView.findViewById(R.id.hostView);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("sessions");

        //initialise list
        list = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter = new ArrayAdapter<>(getActivity(),R.layout.hosting,R.id.hosting,list);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    String module_info = "Module: " + session.getModule().toString() + "\n" +
                            "Date: " + session.getdate().toString() + "\n" +
                            "Location: " + session.getLocation().toString() + "\n" +
                            "Timing: " + session.getTiming().toString();

                    list.add(module_info);
                }
                hostView.setAdapter(adapter);
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
                Intent intent = new Intent(getActivity(), createSession.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
