package com.hola.heshan.hola;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothServices extends Thread{
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothDevice device;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public BluetoothServices(Handler mHandler, BluetoothDevice device) {
        this.mHandler = mHandler;
        this.device = device;

        try {
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            if (bluetoothSocket != null){
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if(inputStream != null){
            byte[] message = new byte[1];
            while (true){
                try {
                    inputStream.read(message);
                    Message.obtain(mHandler,BluetoothMessages.TEST_MESSAGE,message).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void write(byte[] message){
        try {
            outputStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}