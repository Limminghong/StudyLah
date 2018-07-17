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
    private final ArrayList<String> modules;
    private final ArrayList<String> host;
    private final ArrayList<String> timing;
    private final ArrayList<String> date;
    private final ArrayList<String> location;
    private final ArrayList<String> imageLink;

    public CustomListAdapter(Activity context, ArrayList<String> modules, ArrayList<String> host, ArrayList<String> timing, ArrayList<String> date, ArrayList<String> location, ArrayList<String> imageLink) {
        super(context, R.layout.session, modules);

        this.context = context;
        this.modules = modules;
        this.host = host;
        this.timing = timing;
        this.date = date;
        this.location = location;
        this.imageLink = imageLink;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.session, null,true);

        TextView textModule = (TextView)rowView.findViewById(R.id.textModule);
        TextView textHost = (TextView)rowView.findViewById(R.id.textHost);
        TextView textTiming = (TextView)rowView.findViewById(R.id.textTiming);
        TextView textDate = (TextView)rowView.findViewById(R.id.textDate);
        TextView textLocation = (TextView)rowView.findViewById(R.id.textLocation);
        CircleImageView mDisplayImage = (CircleImageView)rowView.findViewById(R.id.userImage2);

        textModule.setText(modules.get(position));
        textHost.setText(host.get(position));
        textTiming.setText(timing.get(position));
        textDate.setText(date.get(position));
        textLocation.setText(location.get(position));
        if(!imageLink.get(position).equals("default")) {
            Picasso.get().load(imageLink.get(position)).into(mDisplayImage);
        }

        return rowView;
    };
}
