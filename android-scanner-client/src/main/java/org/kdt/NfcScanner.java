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

import java.nio.charset.Charset;

import org.kdt.model.Cell;
import org.kdt.model.Scanable;
import org.kdt.model.Task;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;


public class NfcScanner implements Scanner {
	
	private final Intent intent;
	
	public NfcScanner(Intent intent) {
		this.intent = intent;
	}
	
	public Scanable scan() {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			return rawToScanable(rawMsgs);
	    }
		
		return null;
	}
	
	private Scanable rawToScanable(Parcelable[] rawMsgs) {
	    NdefMessage message = (NdefMessage) rawMsgs[0];
    	return messageToScanable(message);
	}
	
	private Scanable messageToScanable(NdefMessage message) {
		
		NdefRecord record = message.getRecords()[0];
		String text = decodePayload(record.getPayload());
		
		String mimeType = decodePayload(record.getType());

		Scanable kanbanArtifact;
		if (mimeType.endsWith("task")) {
			kanbanArtifact = new Task(text);
		} else {
			kanbanArtifact = new Cell(text);
		}
		
		return kanbanArtifact;
	}
	
	private String decodePayload(byte[] payload) {
		return new String(payload, Charset.forName("US-ASCII"));
	}
}
