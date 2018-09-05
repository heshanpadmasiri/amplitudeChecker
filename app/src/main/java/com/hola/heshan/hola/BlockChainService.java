package com.hola.heshan.hola;

import android.content.Context;

public class BlockChainService {
    private static BlockChainService instance = new BlockChainService();
    private FirebaseServices firebaseServices;
    private final static String URL = "test_url";

    private BlockChainService(){
        firebaseServices = FirebaseServices.getInstance();
    }

    public static BlockChainService getInstance() {
        return instance;
    }

    public String getPasscode(String doorId, String userId, Context context){
        return null;
    }
}
