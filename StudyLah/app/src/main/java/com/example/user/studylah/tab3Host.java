package com.example.user.studylah;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class tab3Host extends Fragment {
    private Button mButtonHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_host, container, false);

        // Host button
        mButtonHost = (Button) rootView.findViewById(R.id.buttonHost);
        mButtonHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), createSession.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
