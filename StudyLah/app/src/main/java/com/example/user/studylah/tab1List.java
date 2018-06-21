package com.example.user.studylah;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class tab1List extends Fragment {

    ListView listView;
    // Authenticating Firebase for session list
    FirebaseDatabase database;
    DatabaseReference ref;

    //Creating list to store all the session information
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_list, container, false);

        session = new Session();
        listView = (ListView) rootView.findViewById(R.id.listView);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("sessions");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(),R.layout.session,R.id.session_view,list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    list.add(session.getHost().toString() + "  " + session.getTiming().toString()
                    + "  " + session.getModule().toString() + "  " + session.getLocation().toString()
                    + "  " + session.getdate().toString());

                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }
}