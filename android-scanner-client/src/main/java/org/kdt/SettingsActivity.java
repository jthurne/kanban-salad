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

import org.kdt.kanbandatatracker.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            Preference emailAddressPref = this
                    .findPreference(SettingKeys.EMAIL_ADDRESS);
            emailAddressPref
                    .setOnPreferenceChangeListener(new EditTextPreferenceChangeListener());

            String currentValue = PreferenceManager
                    .getDefaultSharedPreferences(emailAddressPref.getContext())
                    .getString(emailAddressPref.getKey(), "");
            emailAddressPref.setSummary(currentValue);
        }
    }

    private static class EditTextPreferenceChangeListener implements
            OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(
                Preference preference, Object newValue) {
            preference.setSummary(newValue.toString());
            return true;
        }
    }

}
