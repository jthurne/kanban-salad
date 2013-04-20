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
package org.kdt.scan;

import java.io.File;

import org.kdt.SettingKeys;
import org.kdt.kanbandatatracker.R;
import org.kdt.scan.Sender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * 
 */
public class EmailSender implements Sender {

    private final Activity parentActivity;

    public EmailSender(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.scan.Sender#send(java.io.File)
     */
    @Override
    public void send(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, getEmailAddress());
        intent.putExtra(Intent.EXTRA_SUBJECT, getSubject(file));
        intent.putExtra(Intent.EXTRA_TEXT, getBody());
        intent.setType("message/rfc822");

        Uri csvFileUri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, csvFileUri);

        parentActivity.startActivity(intent);
    }

    private String[] getEmailAddress() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(parentActivity);

        return new String[] { preferences.getString(SettingKeys.EMAIL_ADDRESS,
                "") };
    }

    private String getSubject(File file) {
        return parentActivity.getResources().getString(
                R.string.snapshot_email_subject, file.getName());
    }

    private String getBody() {
        return parentActivity.getResources().getString(
                R.string.snapshot_email_body);
    }
}
