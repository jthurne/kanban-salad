package com.example.kanbandatatracker;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		List<String> payloads = tagToStrings();
		
		for (String text : payloads) {
			final TextView helloWorldText = (TextView) findViewById(R.id.hello_world);
		    helloWorldText.setText(text);
		}
	}

	private List<String> tagToStrings() {
		List<String> textPayloads = new LinkedList<String>();
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
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
			String text = decodePayload(record);
			
			textPayloads.add(text);
		}
		
		return textPayloads;
	}

	private String decodePayload(NdefRecord record) {
		byte[] payload = record.getPayload();
		String text = new String(payload, Charset.forName("US-ASCII"));
		return text;
	}

}
