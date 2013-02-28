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
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;


public class NfcScanner implements Scanner {
	
	public List<String> scan(Intent intent) {
		List<String> textPayloads = new LinkedList<String>();
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			textPayloads.addAll(rawToStrings(rawMsgs));
	    }
		
		return textPayloads;
	}
	
	private List<String> rawToStrings(Parcelable[] rawMsgs) {
		List<String> textPayloads = new LinkedList<String>();
		
		if (rawMsgs != null) {
		    for (Parcelable rawMessage : rawMsgs) {
		        NdefMessage message = (NdefMessage) rawMessage;

		        textPayloads.addAll(messageToStrings(message));
		    }
		}
		
		return textPayloads;
	}
	
	private List<String> messageToStrings(NdefMessage message) {
		List<String> textPayloads = new LinkedList<String>();
		
		NdefRecord[] records = message.getRecords();
		for (NdefRecord record : records) {
			String mimeType = decodePayload(record.getType());

			if(!mimeType.startsWith("application/vnd.kdt")) {
			    continue;
			}
			
			String text = decodePayload(record.getPayload());
			
			if (mimeType.endsWith("task")) {
				text = "\t\t" + text;
			}
			
			textPayloads.add(text);
		}
		
		return textPayloads;
	}
	
	private String decodePayload(byte[] payload) {
		return new String(payload, Charset.forName("US-ASCII"));
	}
}
