package com.example.user.studylah;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class SearchUser extends AppCompatActivity {

    // Initialise Search Bar
    Toolbar searchBar;
    MaterialSearchView searchView;

    // User database
    private DatabaseReference mUserDatabase;

    // User list
    private ListView mListView;
    private ArrayList<String> listName;
    private ArrayList<String> listImage;
    private TextView emptyText;
    private UserListAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        // Create search bar
        searchBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(searchBar);
        getSupportActionBar().setTitle("Search User");
        searchBar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView = (MaterialSearchView)findViewById(R.id.searchview);

        // Initialise user database;
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialise Lists
        mListView = (ListView)findViewById(R.id.listView);
        listName = new ArrayList<>();
        listImage = new ArrayList<>();
        emptyText = (TextView)findViewById(R.id.emptyView);
        userAdapter = new UserListAdapter(SearchUser.this, listName, listImage);

        // Populate user list
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listName.clear();
                listImage.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = new User();
                    user = ds.getValue(User.class);

                    String username = user.getUsername();
                    String userImage = user.getImageThumb();

                    listName.add(username);
                    listImage.add(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                mListView.setEmptyView(emptyText);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if(newText != null && !newText.isEmpty()) {

                    ArrayList<String> listNameFound = new ArrayList<>();
                    ArrayList<String> listImageFound = new ArrayList<>();

                    for(int pos = 0; pos < listName.size(); pos++){
                        if(listName.get(pos).contains(newText)){
                            listNameFound.add(listName.get(pos));
                            listImageFound.add(listImage.get(pos));
                        }
                    }

                    UserListAdapter userAdapterFound = new UserListAdapter(SearchUser.this, listNameFound, listImageFound);
                    mListView.setAdapter(userAdapterFound);
                }
                else {
                    mListView.setEmptyView(emptyText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }
}
