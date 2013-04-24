package com.example.bluetoothprototype;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void sendSomethingClicked(View button) {
        try {
            Log.d(TAG, "getting local device");
            // remote MAC here:
            BluetoothSocket socket = getBluetoothSocket();
            Log.d(TAG, "about to connect");
            socket.connect();
            Log.d(TAG, "Connected!");
            socket.getOutputStream().write("Hello, world!\n".getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to device", e);
        }

    }

    private static BluetoothSocket getBluetoothSocket() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a
                // ListView
                if (device.getName().equalsIgnoreCase(("firefly-0"))) {
                    try {
                        return device
                                .createInsecureRfcommSocketToServiceRecord(UUID
                                        .fromString("DEADBEEF-DEAD-BEEF-DEAD-BEEFDEADBEEF"));
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
