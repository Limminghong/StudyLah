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

public class tab2Join extends Fragment {
    Button mButtonHost;

    ListView joinList;
    // Authenticating Firebase for session list
    FirebaseDatabase database;

    //get the name of the currently signed in user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //name of the user
    String name = user.getDisplayName();

    //make reference to the database used
    DatabaseReference ref;

    //Creating list to store all the session information
    ArrayList<String> list2;
    ArrayAdapter<String> adapter2;
    Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_join, container, false);

        session = new Session();
        joinList = (ListView) rootView.findViewById(R.id.joinList);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("sessions");

        //initialise list
        list2 = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter2 = new ArrayAdapter<>(getActivity(),R.layout.session,R.id.module,list2);

        ValueEventListener valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    String module_info = "Module: " + session.getModule();

                    if (session.getParticipants().containsKey(name)) list2.add(module_info);
                }
                joinList.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }
}
