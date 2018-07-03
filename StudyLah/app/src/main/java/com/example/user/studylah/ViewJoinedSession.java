package com.example.user.studylah;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class ViewJoinedSession extends AppCompatActivity {
    private Button mButtonLeave;
    private String key;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_joined_session);

        // Initialise widgets
        mButtonLeave = (Button)findViewById(R.id.buttonLeave);

        // Get Key
        key = getIntent().getStringExtra("KEY");

        // Get database reference
        database = FirebaseDatabase.getInstance();
        final DatabaseReference sessionRef = database.getReference("/sessions/" + key);

        // Get Username
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUsername = currentUser.getDisplayName().toString();

        mButtonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeParticipant(sessionRef);
                backToJoinList();
            }

            public void removeParticipant(DatabaseReference sRef) {
                sRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Session s = mutableData.getValue(Session.class);
                        if(s.participants.containsKey(currentUsername)) s.participantCount -= 1;
                        s.participants.remove(currentUsername);

                        mutableData.setValue(s);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }

            public void backToJoinList() {
                Intent intent = new Intent(ViewJoinedSession.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
