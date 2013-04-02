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

import java.io.IOException;
import java.nio.charset.Charset;

import org.kdt.model.Programable;
import org.kdt.program.Programer;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

/**
 * 
 */
public class NfcProgramer implements Programer {
    private static final Charset ASCII = Charset.forName("US-ASCII");

    private final Activity parentActivity;

    public NfcProgramer(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void programTag(Programable tag) {
        Intent intent = parentActivity.getIntent();

        if (aTagWeCanHandleWasScanned(intent)) {
            Ndef ndefTag = getNdefTagFrom(intent);

            if (!ndefTag.isWritable()) {
                throw new TagIsNotWritable();
            }

            program(ndefTag, tag);
        }
    }

    private boolean aTagWeCanHandleWasScanned(Intent intent) {
        return NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

    private Ndef getNdefTagFrom(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        return Ndef.get(tagFromIntent);
    }

    private void program(Ndef ndefTag, Programable tag) {
        try {
            ndefTag.connect();
            ndefTag.writeNdefMessage(createMessage(tag));
        } catch (IOException e) {
            throw new ProgrammingFailed(e);
        } catch (FormatException e) {
            throw new ProgrammingFailed(e);
        } finally {
            close(ndefTag);
        }
    }

    private NdefMessage createMessage(Programable tag) {
        NdefRecord mimeRecord = createMimeRecord(tag);
        NdefRecord appRecord = createAppRecord();
        return new NdefMessage(new NdefRecord[] { mimeRecord, appRecord });
    }

    private NdefRecord createMimeRecord(Programable tag) {
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                encode(tag.getMimeType()), new byte[0],
                encode(tag.getDataString()));
    }

    private NdefRecord createAppRecord() {
        return NdefRecord.createApplicationRecord("org.kdt.kanbandatatracker");
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

    public static class TagIsNotWritable extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static class ProgrammingFailed extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ProgrammingFailed(Throwable throwable) {
            super(throwable);
        }
    }

    public static class ClosingTheTagFailed extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ClosingTheTagFailed(Throwable throwable) {
            super(throwable);
        }
    }

}
