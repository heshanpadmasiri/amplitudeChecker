package com.hola.heshan.hola;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    Button btn_start;

    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        btn_start = view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavigationActivity.getDeviceId() == null || NavigationActivity.getURL() == null){
                    Toast.makeText(getContext(),"Settings not set",Toast.LENGTH_LONG).show();
                } else {
                    NavigationActivity instance = NavigationActivity.getInstance();
                    if (instance.isRecording){
                        instance.stopRecording();
                        btn_start.setText("Start");
                    } else {
                        instance.startRecording();
                        btn_start.setText("Stop");
                    }

                }
            }
        });
        return view;


    }







}
