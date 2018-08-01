package com.hola.heshan.hola;


import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements FingerPrintAuthCallback{

    private Button authenticateButton;
    private FingerPrintAuthHelper fingerPrintAuthHelper;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        authenticateButton = view.findViewById(R.id.btn_auth);
        fingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(view.getContext(),this);
        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPrintAuthHelper.startAuth();
            }
        });
        return view;
    }

    @Override
    public void onNoFingerPrintHardwareFound() {

    }

    @Override
    public void onNoFingerPrintRegistered() {

    }

    @Override
    public void onBelowMarshmallow() {

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(getActivity(),"AuthSuccess", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

        Toast.makeText(getActivity(),"AuthFailed", Toast.LENGTH_LONG).show();
    }
}
