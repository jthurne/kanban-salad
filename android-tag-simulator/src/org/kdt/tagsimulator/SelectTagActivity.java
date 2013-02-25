package org.kdt.tagsimulator;

import java.nio.charset.Charset;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SelectTagActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tag);
        
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		NdefRecord  uriRecord = NdefRecord.createUri("http://github.com/jthurne/kanban-data-tracker");
		NdefRecord  mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, 
				"application/vnd.kdt".getBytes(Charset.forName("US-ASCII")),
				new byte[0], 
				"You've been BEAMED!".getBytes(Charset.forName("US-ASCII")));
		
		NdefMessage message = new NdefMessage(new NdefRecord[] {mimeRecord});
		
		if (nfcAdapter == null) return;
		nfcAdapter.setNdefPushMessage(message, this);        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_tag, menu);
        return true;
    }
    
}
