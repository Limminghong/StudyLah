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

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> sessionDesc;
    private final ArrayList<String> imageLink;

    public CustomListAdapter(Activity context, ArrayList<String> sessionDesc, ArrayList<String> imageLink) {
        super(context, R.layout.session, sessionDesc);

        this.context = context;
        this.sessionDesc = sessionDesc;
        this.imageLink = imageLink;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.session, null,true);

        TextView mModule = (TextView)rowView.findViewById(R.id.module);
        CircleImageView mDisplayImage = (CircleImageView)rowView.findViewById(R.id.userImage2);

        mModule.setText(sessionDesc.get(position));
        if(!imageLink.get(position).equals("default")) {
            Picasso.get().load(imageLink.get(position)).into(mDisplayImage);
        }

        return rowView;
    };
}
