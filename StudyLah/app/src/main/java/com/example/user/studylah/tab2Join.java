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

    //Creating array to store all the id
    Map<Integer, String> sessionId = new HashMap<>();

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
                //clear list to prevent bug
                list2.clear();
                sessionId.clear();
                //lists out all the Sessions that are available
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    String module_info = session.getModule() + "   " + "Host: " + session.getHost() + "\n" +
                            "Timing: " + session.getTiming() + "\n" +
                            "Date: " + session.getdate() + "\n" +
                            "Location: " + session.getLocation();
                    if (session.getParticipants().containsKey(name)) {
                        list2.add(module_info);
                        sessionId.put(list2.indexOf(module_info), session.getId());
                    }
                }
                joinList.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        joinList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

                AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
                alertDig.setMessage("Do you want to leave this session?");
                alertDig.setCancelable(false);
                String key = sessionId.get(i);
                final DatabaseReference sessionRef = database.getReference("/sessions/" + key);

                alertDig.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getActivity(), "Cool it works", Toast.LENGTH_SHORT).show();
                        // Add student
                        removeParticipant(sessionRef);
                    }

                    public void removeParticipant(DatabaseReference sRef) {
                        sRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Session s = mutableData.getValue(Session.class);
                                if(s.participants.containsKey(name)) s.participantCount -= 1;
                                s.participants.remove(name);

                                mutableData.setValue(s);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
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
