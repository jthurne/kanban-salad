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
package org.kdt;

import java.util.Set;

import org.kdt.kanbandatatracker.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

// FIXME: Find a way to push some of the logic in the class into testable objects
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            initEmailAddressPref();
            initBluetoothDeviceList();
        }

        private void initBluetoothDeviceList() {
            final ListPreference bluetoothDeviceList = (ListPreference) this
                    .findPreference(SettingKeys.BLUETOOTH_DEVICE);

            setBluetoothDevicesOn(bluetoothDeviceList);
            bluetoothDeviceList
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            setBluetoothDevicesOn(bluetoothDeviceList);
                            return true;
                        }
                    });

            bindValueToSummary(bluetoothDeviceList);
        }

        private void initEmailAddressPref() {
            Preference emailAddressPref = this
                    .findPreference(SettingKeys.EMAIL_ADDRESS);
            bindValueToSummary(emailAddressPref);
        }

        private void bindValueToSummary(Preference preference) {
            BindValueToSummaryPreferenceChangeListener listener = new BindValueToSummaryPreferenceChangeListener();
            preference
                    .setOnPreferenceChangeListener(listener);

            String currentValue = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), "");

            listener.onPreferenceChange(preference, currentValue);
        }

        private void setBluetoothDevicesOn(ListPreference bluetoothDeviceList) {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

            CharSequence[] entries = new CharSequence[1];
            CharSequence[] entryValues = new CharSequence[1];
            entries[0] = "No Devices";
            entryValues[0] = "";

            if (pairedDevices.size() > 0) {
                entries = new CharSequence[pairedDevices.size()];
                entryValues = new CharSequence[pairedDevices.size()];
                int i = 0;
                for (BluetoothDevice device : pairedDevices) {
                    entries[i] = device.getName();
                    entryValues[i] = device.getAddress();
                    i++;
                }
            }

            bluetoothDeviceList.setEntries(entries);
            bluetoothDeviceList.setEntryValues(entryValues);
        }
    }

    private static class BindValueToSummaryPreferenceChangeListener implements
            OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(
                Preference preference, Object newValue) {

            if (preference instanceof ListPreference) {
                ((ListPreference) preference).setValue((String) newValue);
                preference.setSummary(((ListPreference) preference).getEntry());
            } else {
                preference.setSummary(newValue.toString());
            }
            return true;
        }
    }

}
