package com.example.user.studylah;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
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
                    String module_info = session.getModule().toString() + "\n" + session.getdate().toString()
                            + "  " + session.getLocation().toString() + "  " + session.getTiming().toString()
                            + "  " + session.getHost().toString();
                    //Spannable string used to make the first module line twice as big
                    SpannableString ss1 = new SpannableString(module_info);
                    ss1.setSpan(new RelativeSizeSpan(2f), 0,5,0);
                    //Mimicking the concatenation of the string
                    String final_module = "" + ss1.toString();

                    list.add(final_module);
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