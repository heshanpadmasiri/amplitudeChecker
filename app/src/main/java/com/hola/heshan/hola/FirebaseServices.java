package com.hola.heshan.hola;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseServices {
    private static FirebaseServices instance;
    private FirebaseFirestore db;

    private FirebaseServices(){
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseServices getInstance() {
        return instance;
    }

    protected Task<DocumentSnapshot> getUserData(String userId){
        return db.collection("users").document(userId).get();
    }

    protected Task<DocumentSnapshot> getDoorData(String doorId){
        return db.collection("doors").document(doorId).get();
    }

    protected void requestPermission(final String userId, String companyId){
        final DocumentReference docRef = db.collection("pending_request").document(companyId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    List<String> currentRequest = (List<String>) task.getResult().get("pending_users");
                    if(currentRequest != null){
                        currentRequest.add(userId);
                        docRef.update("pending_users",currentRequest);
                    } else {
                        Map<String,Object> valueMap = new HashMap<>();
                        List<String> pendingUsers = new ArrayList<>();
                        pendingUsers.add(userId);
                        valueMap.put("pending_users", pendingUsers);
                        docRef.set(valueMap);
                    }
                }
            }
        });
    }
}
