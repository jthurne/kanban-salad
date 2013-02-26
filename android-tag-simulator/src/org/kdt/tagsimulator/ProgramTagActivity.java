package org.kdt.tagsimulator;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class ProgramTagActivity extends Activity {
    private static final String APP_MIME_TYPE = "application/vnd.kdt";
	private static final Charset ASCII = Charset.forName("US-ASCII");	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program_tag);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.program_tag, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Ndef ndefTag = Ndef.get(tagFromIntent);
			if (!ndefTag.isWritable()) {
				// TODO Do something if it isn't writeable
			}
			try {
				ndefTag.connect();
				ndefTag.writeNdefMessage(createMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					ndefTag.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }
	}
	
	private NdefMessage createMessage() {
		NdefRecord  mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				encode(APP_MIME_TYPE),
				new byte[0],
				encode("You've been BEAMED!"));

		return new NdefMessage(new NdefRecord[] {mimeRecord});
	}
	
    private byte[] encode(String toConvert) {
	return toConvert.getBytes(ASCII);
    }
	
	

}
