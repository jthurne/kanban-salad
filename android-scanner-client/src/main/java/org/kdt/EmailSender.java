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

import java.io.File;

import org.kdt.scan.Sender;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

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
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { "hurne_jim@yahoo.com" });
        intent.putExtra(Intent.EXTRA_SUBJECT, "NFC Tag Scan");
        intent.putExtra(Intent.EXTRA_TEXT, "Scanned NFC Tag Data");
        intent.setType("text/plain");

        Uri csvFileUri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, csvFileUri);

        parentActivity.startActivity(intent);
    }

}
