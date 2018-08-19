package com.hola.heshan.hola;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Set;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private BluetoothDevice door;
    private Handler messageHandler;
    private BluetoothAdapter bluetoothAdapter;
    private volatile boolean bluetoothReady;
    private BluetoothServices bluetoothService;

    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;

    private static final int ENABLE_BLUETOOTH_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 2;

    private static final String KEY_ALIAS = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create Fragments
        homeFragment = new HomeFragment();
        // set fragment to home
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,homeFragment);
        fragmentTransaction.commit();

        // set up bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothReady = false;
        if(bluetoothAdapter == null){
            Toast.makeText(this, "Device don't support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            // enable bluetooth
            bluetoothReady = bluetoothAdapter.isEnabled();
            if (!bluetoothReady){
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent,ENABLE_BLUETOOTH_REQUEST);
            } else {
                onBlueToothReady();
            }
        }
    }

    private void onBlueToothReady(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice testDevice = null;
        for (BluetoothDevice device: pairedDevices){
            if (device.getName().equals("HC-05")){
                testDevice = device;
                break;
            }
        }

        messageHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case BluetoothMessages.TEST_MESSAGE:
                        byte[] data = (byte[]) msg.obj;
                        onMessageRecieve(data);
                }
            }
        };
        bluetoothService = new BluetoothServices(messageHandler,testDevice);
        bluetoothService.start();
    }


    private void onMessageRecieve(byte[] msg){
        Toast.makeText(this,Arrays.toString(msg), Toast.LENGTH_LONG).show();
        bluetoothService.write("y".getBytes());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH_REQUEST){
            if (resultCode == RESULT_OK){
                bluetoothReady = bluetoothAdapter.isEnabled();
                Toast.makeText(this, "Bluetooth Enabled",Toast.LENGTH_LONG).show();
                onBlueToothReady();
            } else {
                Toast.makeText(this, "Bluetooth enable refused", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container,homeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
