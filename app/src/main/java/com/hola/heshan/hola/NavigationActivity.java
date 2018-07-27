package com.hola.heshan.hola;

import android.Manifest;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aitorvs.android.fingerlock.FingerLockManager;
import com.aitorvs.android.fingerlock.FingerprintDialog;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.RSAKeyGenParameterSpec;

import javax.crypto.KeyGenerator;

import static java.security.spec.RSAKeyGenParameterSpec.F4;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FingerprintDialog.Callback {

    private BluetoothDevice door;
    private Handler messageHandler;
    private BluetoothAdapter bluetoothAdapter;
    private volatile boolean bluetoothReady;

    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;

    private static final int ENABLE_BLUETOOTH_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 2;

    private FingerLockManager fingerLockManager;
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
        createKeys(KEY_ALIAS,false);
        // create and show the fingerprint dialog using the Builder
        new FingerprintDialog.Builder()
                .with(NavigationActivity.this)    // context, must call
                .setKeyName(KEY_ALIAS)       // String key name, must call
                .setRequestCode(69)         // request code identifier, must call
                .show();                    // show the dialog
    }


    static void createKeys(String alias, boolean requireAuth) {
        // Get an instance to the key generator using AES
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            int purpose = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            // to make it harder, byte padding
            String padding = KeyProperties.ENCRYPTION_PADDING_PKCS7;
            // Init the generator
            keyGenerator.init(new KeyGenParameterSpec.Builder(alias, purpose)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // key allowed only when user is authenticated
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(padding)
                    .build());
        // generate the key
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    private void onBlueToothReady(){

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
            fragmentTransaction.add(R.id.fragment_container,homeFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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

    @Override
    public void onFingerprintDialogAuthenticated() {
        Toast.makeText(this,"Fingerprint authenticated",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFingerprintDialogVerifyPassword(FingerprintDialog fingerprintDialog, String s) {

    }

    @Override
    public void onFingerprintDialogStageUpdated(FingerprintDialog fingerprintDialog, FingerprintDialog.Stage stage) {

    }

    @Override
    public void onFingerprintDialogCancelled() {

    }
}
