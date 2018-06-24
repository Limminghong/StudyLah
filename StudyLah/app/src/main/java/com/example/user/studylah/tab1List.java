package com.example.user.studylah;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class tab1List extends Fragment {

    ListView listView;
    //filters the sessions strings based on the module
    EditText filter;
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
        filter = (EditText) rootView.findViewById(R.id.search_filter);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("sessions");

        //initialise list
        list = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter = new ArrayAdapter<>(getActivity(),R.layout.session,R.id.module,list);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lists out all the Sessions that are available
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);
                    String module_info = session.getModule() + "\n" +
                            "Date: " + session.getdate() + "\n"
                            + "Location: " + session.getLocation() + "\n" +
                            "Time: " + session.getTiming() + "\n"
                            + "Host: " + session.getHost();
                    list.add(module_info);
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


        return rootView;
    }
}