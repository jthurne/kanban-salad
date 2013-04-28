/**
 * Copyright 2013 Jim Hurne and Joseph Kramer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kanbansalad.scanner.client.android.program;

import java.io.IOException;

import org.kanbansalad.bluetooth.BluetoothConstants;
import org.kanbansalad.scanner.client.android.SettingKeys;
import org.kanbansalad.scanner.client.program.BluetoothConnection;
import org.kanbansalad.scanner.client.program.BluetoothConnectionProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 
 */
public class AndroidBluetoothConnectionProvider implements
        BluetoothConnectionProvider {

    private final SharedPreferences prefs;

    public AndroidBluetoothConnectionProvider(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public BluetoothConnection get() {
        try {
            String address = getDeviceAddress();
            return new AndroidBluetoothConnection(getBluetoothSocket(address));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    private String getDeviceAddress() {
        return prefs.getString(SettingKeys.BLUETOOTH_DEVICE, "");
    }

    private BluetoothSocket getBluetoothSocket(String address)
            throws IOException {
        BluetoothSocket socket = findDevice(address)
                .createRfcommSocketToServiceRecord(
                        BluetoothConstants.APP_UUID);

        socket.connect();

        return socket;
    }

    private BluetoothDevice findDevice(String address) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.cancelDiscovery();

        return adapter.getRemoteDevice(address);
    }
}
