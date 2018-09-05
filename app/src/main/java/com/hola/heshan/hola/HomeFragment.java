package com.hola.heshan.hola;


import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements FingerPrintAuthCallback{

    private Button authenticateButton;
    private Button permissionButton;
    private Button attendanceButton;
    private Button blockChainButton;
    private TextView titleTextView;
    private TextView bodyTextView;
    private SpinKitView spinKitView;
    private FingerPrintAuthHelper fingerPrintAuthHelper;
    private MaterialDialog fingerPrintAuthPrompt;
    private volatile int fingerPrintAuthPromptTask;
    private FirebaseFunctions firebaseFunctions;

    private final static String URL = "http://192.168.43.5:3000/api";

    public BluetoothServices getBluetoothServices() {
        return bluetoothServices;
    }

    public void setBluetoothServices(BluetoothServices bluetoothServices) {
        this.bluetoothServices = bluetoothServices;
    }

    private BluetoothServices bluetoothServices;

    private volatile String companyId;

    private FirebaseServices firebaseServices;
    private BlockChainService blockChainService;

    public final static String USER_ID = "test_user_1";
    public final static String DOOR_ID = "1";

    private final static int VALIDATE_USER = 0;
    private final static int REQUEST_ACCESS_PERMISSION = 1;
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
        attendanceButton = view.findViewById(R.id.btn_attendance);
        blockChainButton = view.findViewById(R.id.btn_blockChain);
        titleTextView = view.findViewById(R.id.txt_title);
        bodyTextView = view.findViewById(R.id.txt_body);
        spinKitView = view.findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.INVISIBLE);
        firebaseServices = FirebaseServices.getInstance();
        blockChainService = BlockChainService.getInstance();
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
                fingerPrintAuthPromptTask = VALIDATE_USER;
                fingerPrintAuthHelper.startAuth();
            }
        });

        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                spinKitView.setVisibility(View.VISIBLE);
                Task<DocumentSnapshot> doorDataTask = firebaseServices.getDoorData(DOOR_ID);
                doorDataTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final String reqPermission = (String) task.getResult().get("permission_level");
                            companyId = (String) task.getResult().get("company_id");
                            Task<DocumentSnapshot> userDataTask = firebaseServices.getUserData(USER_ID);
                            userDataTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        String userPermission = (String) task.getResult().get("permission_level");
                                        String userCompany = (String) task.getResult().get("company_id");
                                        spinKitView.setVisibility(View.INVISIBLE);
                                        if(reqPermission.equals(userPermission)){
                                            // todo : open the door
                                        } else if (!userCompany.equals(companyId)){

                                            updateText("No permission", "Request permission confirm by fingerPrint");
                                            fingerPrintAuthPromptTask = REQUEST_ACCESS_PERMISSION;
                                            fingerPrintAuthHelper.startAuth();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseServices.recordAttendance(USER_ID,"COMPANY1");
            }
        });

        blockChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToBlockChain("d1");
            }
        });
        return view;
    }

    public void initiateHandshake(final String doorId, final String userId){
        spinKitView.setVisibility(View.VISIBLE);
        Task<DocumentSnapshot> doorDataTask = firebaseServices.getDoorData(DOOR_ID);
        doorDataTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final String reqPermission = (String) task.getResult().get("permission_level");
                    companyId = (String) task.getResult().get("company_id");
                    Task<DocumentSnapshot> userDataTask = firebaseServices.getUserData(USER_ID);
                    userDataTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                String userPermission = (String) task.getResult().get("permission_level");
                                String userCompany = (String) task.getResult().get("company_id");
                                spinKitView.setVisibility(View.INVISIBLE);
                                if(reqPermission.equals(userPermission)){
                                    connectToBlockChain(doorId);
                                    //String passCode = blockChainService.getPasscode(doorId,userId);
                                    //bluetoothServices.write(passCode.getBytes());
                                } else if (!userCompany.equals(companyId)){

                                    updateText("No permission", "Request permission confirm by fingerPrint");
                                    fingerPrintAuthPromptTask = REQUEST_ACCESS_PERMISSION;
                                    fingerPrintAuthHelper.startAuth();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateText(String title,String body){
        titleTextView.setText(title);
        bodyTextView.setText(body);
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

    private void connectToBlockChain(String doorId){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL + "Door/" + doorId, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String passcode = (String) response.get("password");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateText("Response: " , response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(getActivity(),"AuthSuccess", Toast.LENGTH_LONG).show();
        if(fingerPrintAuthPrompt != null){
            fingerPrintAuthPrompt.dismiss();
        }
        switch (fingerPrintAuthPromptTask){
            case REQUEST_ACCESS_PERMISSION:
                firebaseServices.requestPermission(USER_ID,companyId);
                updateText("","");
                break;
        }
    }


    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

        Toast.makeText(getActivity(),"AuthFailed", Toast.LENGTH_LONG).show();
    }


}
