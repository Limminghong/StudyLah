package com.example.user.studylah;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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
    ArrayList<String> listModule2;
    ArrayList<String> listHost2;
    ArrayList<String> listTiming2;
    ArrayList<String> listDate2;
    ArrayList<String> listLocation2;
    ArrayList<String> listImage2;
    CustomListAdapter adapter2;
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
        listModule2 = new ArrayList<>();
        listHost2 = new ArrayList<>();
        listTiming2 = new ArrayList<>();
        listDate2 = new ArrayList<>();
        listLocation2 = new ArrayList<>();
        listImage2 = new ArrayList<>();

        //initialise adapter to connect firebase data to the list
        adapter2 = new CustomListAdapter(getActivity(), listModule2, listHost2, listTiming2, listDate2, listLocation2, listImage2);

        ValueEventListener valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clear the list and sessionId
                listModule2.clear();
                listImage2.clear();
                listHost2.clear();
                listTiming2.clear();
                listDate2.clear();
                listLocation2.clear();
                sessionId.clear();
                //lists out all the Sessions that are available
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //receives all the information for each session
                    session = ds.getValue(Session.class);

                    String module_name = session.getModule();
                    String module_host = "Host: " + session.getHost();
                    String module_timing = "Timing: " + session.getTiming();
                    String module_date = "Date: " + session.getdate();
                    String module_location = "Location: " + session.getLocation();
                    String imageLink = session.getHostImage();

                    if (session.getParticipants().containsKey(name)){
                        listModule2.add(module_name);
                        listHost2.add(module_host);
                        listTiming2.add(module_timing);
                        listDate2.add(module_date);
                        listLocation2.add(module_location);
                        listImage2.add(imageLink);
                        sessionId.put(listModule2.indexOf(module_name), session.getId());
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
                LayoutInflater inflater = tab2Join.this.getLayoutInflater();
                AlertDialog.Builder alertDig = new AlertDialog.Builder(getActivity());
                final View dialogView = inflater.inflate(R.layout.session_info_dialog, null);
                alertDig.setView(dialogView);
                alertDig.setCancelable(false);

                // Initialise widgets in dialog
                final CircleImageView mUserFace = (CircleImageView)dialogView.findViewById(R.id.userFace);
                final TextView mDialogHost = (TextView)dialogView.findViewById(R.id.dialogHost);
                final TextView mDialogModule = (TextView)dialogView.findViewById(R.id.dialogModule);
                final TextView mDialogTiming = (TextView)dialogView.findViewById(R.id.dialogTiming);
                final TextView mDialogDate = (TextView)dialogView.findViewById(R.id.dialogDate);
                final TextView mDialogLocation = (TextView)dialogView.findViewById(R.id.dialogLocation);
                final TextView mDialogInfo = (TextView)dialogView.findViewById(R.id.dialogInfo);

                String key = sessionId.get(i);
                final DatabaseReference sessionRef = database.getReference("/sessions/" + key);

                sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Session thisSession = new Session();
                        thisSession = dataSnapshot.getValue(Session.class);

                        final String imageLink = thisSession.getHostImage();

                        if(!imageLink.equals("default")) {
                            Picasso.get().load(imageLink).into(mUserFace);
                        }

                        mDialogModule.setText(thisSession.getModule());
                        mDialogHost.setText("Host: " + thisSession.getHost());
                        mDialogTiming.setText("Timing: " + thisSession.getTiming());
                        mDialogDate.setText("Date: " + thisSession.getdate());
                        mDialogLocation.setText("Location: " + thisSession.getLocation());
                        mDialogInfo.setText(thisSession.getSessionInformation());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                alertDig.setMessage("Do you want to leave this session?");
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
