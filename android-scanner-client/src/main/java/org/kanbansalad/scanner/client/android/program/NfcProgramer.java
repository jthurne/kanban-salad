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
import java.nio.charset.Charset;

import org.kanbansalad.scanner.R;
import org.kanbansalad.scanner.client.program.Programer;
import org.kanbansalad.scanner.client.program.ProgramingException;
import org.kanbansalad.scanner.client.tag.ProgramableTag;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

/**
 * 
 */
public class NfcProgramer implements Programer {
    /**
     * 
     */
    private static final int MESSAGE_OVERHEAD = 57;

    private static final String TAG = "NfcProgrammer";

    private static final Charset ASCII = Charset.forName("US-ASCII");

    private final Activity parentActivity;

    public NfcProgramer(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public ThereWas programTag(ProgramableTag tag) {
        Intent intent = parentActivity.getIntent();

        if (aTagWeCanHandleWasScanned(intent)) {
            Ndef ndefTag = getNdefTagFrom(intent);

            if (!ndefTag.isWritable()) {
                throw new TagIsNotWritable();
            }

            program(ndefTag, tag);
            return ThereWas.A_TAG_TO_PROGRAM;
        }

        return ThereWas.NO_TAG_TO_PROGRAM;
    }

    private boolean aTagWeCanHandleWasScanned(Intent intent) {
        return NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

    private Ndef getNdefTagFrom(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        return Ndef.get(tagFromIntent);
    }

    private void program(Ndef ndefTag, ProgramableTag tag) {
        try {
            ndefTag.connect();
            Log.d(TAG, "NDEF Max Size: " + ndefTag.getMaxSize());
            ndefTag.writeNdefMessage(createMessage(tag, ndefTag.getMaxSize()));
        } catch (IOException e) {
            Log.e(TAG, "Failed to write tag:" + e.getMessage(), e);
            throw new WriteFailed(e);
        } catch (FormatException e) {
            Log.e(TAG, "Failed to write tag:" + e.getMessage(), e);
            throw new WriteFailed(e);
        } finally {
            close(ndefTag);
        }
    }

    private NdefMessage createMessage(ProgramableTag tag, int maxMsgSize)
            throws IOException {

        int maxRecordSize = calcMaxRecordSize(maxMsgSize);

        NdefRecord mimeRecord = createRecordFor(tag, maxRecordSize);
        NdefMessage message = new NdefMessage(new NdefRecord[] { mimeRecord });

        Log.d(TAG, "mimeRecord size: " + mimeRecord.getPayload().length);
        Log.d(TAG, "NdefMessage size: " + message.toByteArray().length);

        return message;
    }

    private int calcMaxRecordSize(int maxMsgSize) {
        int maxRecordSize = maxMsgSize - MESSAGE_OVERHEAD;
        Log.d(TAG, "maxRecordSize: " + maxRecordSize);
        return maxRecordSize;
    }

    private NdefRecord createRecordFor(ProgramableTag tag, int maxRecordSize)
            throws IOException {

        byte[] encodedMimeType = encode(tag.getMimeType());
        int maxDataSize = calcMaxDataSize(maxRecordSize, encodedMimeType);

        // XXX Adding format flag temporarily to avoid needing to reprogram tags
        // Subtract one byte to reserve space for the format flag
        maxDataSize -= 1;

        byte[] tagData = encode(tag.toDataString(maxDataSize));
        byte[] data = new byte[tagData.length + 1];
        data[0] = 0;
        System.arraycopy(tagData, 0, data, 1, tagData.length);

        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                encodedMimeType, new byte[0],
                data);
    }

    private int calcMaxDataSize(int maxRecordSize, byte[] encodedMimeType) {
        int maxDataSize = maxRecordSize - encodedMimeType.length;
        Log.d(TAG, "maxDataSize: " + maxDataSize);
        return maxDataSize;
    }

    private NdefRecord createAppRecord() {
        return NdefRecord.createApplicationRecord("org.kanbansalad.scanner");
    }

    private byte[] encode(String toConvert) {
        return toConvert.getBytes(ASCII);
    }

    private void close(Ndef ndefTag) {
        try {
            ndefTag.close();
        } catch (IOException e) {
            throw new ClosingTheTagFailed(e);
        }
    }

    public class TagIsNotWritable extends ProgramingException {
        private static final long serialVersionUID = 1L;

        public TagIsNotWritable() {
            super(parentActivity.getString(R.string.tag_not_writable_error));
        }
    }

    public class ClosingTheTagFailed extends ProgramingException {
        private static final long serialVersionUID = 1L;

        public ClosingTheTagFailed(Throwable throwable) {
            super(parentActivity.getString(R.string.tag_not_writable_error,
                    throwable.getMessage()), throwable);
        }
    }

    public class WriteFailed extends ProgramingException {
        private static final long serialVersionUID = 1L;

        public WriteFailed(Throwable throwable) {
            super(parentActivity.getString(R.string.write_failed_error,
                    throwable.getMessage()), throwable);
        }
    }
}
