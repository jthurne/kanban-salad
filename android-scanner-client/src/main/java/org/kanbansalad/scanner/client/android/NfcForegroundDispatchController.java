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
package org.kanbansalad.scanner.client.android;

import static org.kanbansalad.scanner.client.tag.TagType.CELL;
import static org.kanbansalad.scanner.client.tag.TagType.TASK;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;

/**
 * Manages the NFC Forground Dispatch System.
 */
public class NfcForegroundDispatchController {
    private final Activity parentActivity;
    private final NfcAdapter nfcAdapter;
    private final PendingIntent nfcPendingIntent;
    private final IntentFilter[] intentFilters;
    private final String[][] techLists;

    public NfcForegroundDispatchController(Activity parentActivity) {
        this.parentActivity = parentActivity;

        nfcAdapter = NfcAdapter.getDefaultAdapter(parentActivity);
        nfcPendingIntent = createPendingIntentForNFCIntents();
        intentFilters = createNfcIntentFilters();
        techLists = createNfcTechLists();
    }

    private PendingIntent createPendingIntentForNFCIntents() {
        return PendingIntent.getActivity(parentActivity, 0, new Intent(
                parentActivity, parentActivity.getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private IntentFilter[] createNfcIntentFilters() {
        IntentFilter alreadyProgrammed = createIntentFilterForAlreadyProgrammedTags();
        IntentFilter emptyTag = createIntentFilterForEmptyTags();
        return new IntentFilter[] { alreadyProgrammed, emptyTag };
    }

    private IntentFilter createIntentFilterForAlreadyProgrammedTags() {
        try {
            IntentFilter ndef = new IntentFilter(
                    NfcAdapter.ACTION_NDEF_DISCOVERED);
            ndef.addDataType(CELL.mimeType());
            ndef.addDataType(TASK.mimeType());
            return ndef;
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
    }

    private IntentFilter createIntentFilterForEmptyTags() {
        return new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
    }

    private String[][] createNfcTechLists() {
        return new String[][] { new String[] { Ndef.class.getName() } };
    }

    public void enableForegroundDispatch() {
        nfcAdapter.enableForegroundDispatch(parentActivity, nfcPendingIntent,
                intentFilters, techLists);
    }

    public void disableForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(parentActivity);
    }
}
