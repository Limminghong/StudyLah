package com.example.user.studylah;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class tab1List extends Fragment {

    ListView listView;
    //filters the sessions strings based on the module
    EditText filter;
    // Authenticating Firebase for session list
    FirebaseDatabase database;
    DatabaseReference ref;

    //Creating list to store all the session information
    public ArrayList<String> list;
    public ArrayAdapter<String> adapter;
    Session session;

    //Creating array to store all the id
    Map<Integer, String> sessionId = new HashMap<>();

    // Get current username
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUsername = currentUser.getDisplayName().toString();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_list, container, false);

        session = new Session();
        listView = (ListView) rootView.findViewById(R.id.listView);
        filter = (EditText) rootView.findViewById(R.id.search_filter);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("sessions");

        //initialise list
        list = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter = new ArrayAdapter<>(getActivity(), R.layout.session, R.id.module, list);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    String module_info = session.getModule() + "\n" +
                                        "Host: " + session.getHost() + "\n" +
                                        "Timing: " + session.getTiming() + "\n" +
                                        "Date: " + session.getdate() + "\n" +
                                        "Location: " + session.getLocation();
                    list.add(module_info);
                    sessionId.put(list.indexOf(module_info), session.getId());
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        //using the filter to search through the modules
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (tab1List.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //what do do when an item is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
                alertDig.setMessage("Do you want to join this session?");
                alertDig.setCancelable(false);

                String key = sessionId.get(i);
                final DatabaseReference sessionRef = database.getReference("/sessions/" + key);

                alertDig.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getActivity(), "Cool it works", Toast.LENGTH_SHORT).show();
                        // Add student
                        addParticipant(sessionRef);
                    }

                    public void addParticipant(DatabaseReference sRef) {
                        sRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Session s = mutableData.getValue(Session.class);
                                if(!s.participants.containsKey(currentUsername)) s.participantCount += 1;
                                s.participants.put(currentUsername, true);

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