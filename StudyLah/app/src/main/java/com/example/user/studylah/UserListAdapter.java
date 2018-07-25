package com.example.user.studylah;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> host;
    private final ArrayList<String> imageLink;

    public UserListAdapter(Activity context, ArrayList<String> host, ArrayList<String> imageLink) {
        super(context, R.layout.user, host);

        this.context = context;
        this.host = host;
        this.imageLink = imageLink;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.user, null,true);

        TextView textHost = (TextView)rowView.findViewById(R.id.userName);
        CircleImageView mDisplayImage = (CircleImageView)rowView.findViewById(R.id.userFace);

        textHost.setText(host.get(position));
        if(!imageLink.get(position).equals("default")) {
            Picasso.get().load(imageLink.get(position)).into(mDisplayImage);
        }

        return rowView;
    };
}
