package com.hola.heshan.hola;


import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements FingerPrintAuthCallback{

    private Button authenticateButton;
    private Button permissionButton;
    private FingerPrintAuthHelper fingerPrintAuthHelper;
    private MaterialDialog fingerPrintAuthPrompt;
    private FirebaseFunctions firebaseFunctions;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        firebaseFunctions = FirebaseFunctions.getInstance();
        fingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(view.getContext(),this);

        authenticateButton = view.findViewById(R.id.btn_auth);
        permissionButton = view.findViewById(R.id.btn_permission);

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fingerPrintAuthPrompt = new MaterialDialog.Builder(v.getContext())
                        .title("Title")
                        .content("Content")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(getContext(),"positive",Toast.LENGTH_LONG).show();

                            }
                        })
                        .build();
                fingerPrintAuthPrompt.show();
                fingerPrintAuthHelper.startAuth();
            }
        });

        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
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
        if(fingerPrintAuthPrompt != null){
            fingerPrintAuthPrompt.dismiss();
        }
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

        Toast.makeText(getActivity(),"AuthFailed", Toast.LENGTH_LONG).show();
    }

    private Task<Boolean> checkPermission(){
        return firebaseFunctions
                .getHttpsCallable("checkPermission")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Object result = task.getResult().getData();
                        return (boolean) result;
                    }
                });
    }
}
