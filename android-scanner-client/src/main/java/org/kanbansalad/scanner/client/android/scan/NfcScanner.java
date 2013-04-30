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
package org.kanbansalad.scanner.client.android.scan;

import java.nio.charset.Charset;

import org.kanbansalad.scanner.client.scan.Scanner;
import org.kanbansalad.scanner.client.scan.TagParser;
import org.kanbansalad.scanner.client.tag.EmptyTag;
import org.kanbansalad.scanner.client.tag.ScanableTag;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

public class NfcScanner implements Scanner {

    private final Activity parentActivity;

    public NfcScanner(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public ScanableTag scan() {
        Intent intent = parentActivity.getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            return rawToScanable(rawMsgs);
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            return new EmptyTag();
        }

        return null;
    }

    private ScanableTag rawToScanable(Parcelable[] rawMsgs) {
        NdefMessage message = (NdefMessage) rawMsgs[0];
        return messageToScanable(message);
    }

    private ScanableTag messageToScanable(NdefMessage message) {
        NdefRecord record = message.getRecords()[0];

        // XXX Ignoring the first byte because it contains a flag we are not
        // paying attention to yet
        byte[] rawData = new byte[record.getPayload().length - 1];
        System.arraycopy(record.getPayload(), 1, rawData, 0, rawData.length);

        String text = decodePayload(rawData);

        String mimeType = decodePayload(record.getType());

        return new TagParser().parse(mimeType, text);
    }

    private String decodePayload(byte[] payload) {
        return new String(payload, Charset.forName("US-ASCII"));
    }
}
