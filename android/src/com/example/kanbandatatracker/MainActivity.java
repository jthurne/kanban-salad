package com.example.kanbandatatracker;

import java.nio.charset.Charset;

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
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                for (Parcelable rawMessage : rawMsgs) {
                    NdefMessage message = (NdefMessage) rawMessage;

                    NdefRecord[] records = message.getRecords();
                    for (NdefRecord record : records) {
                        byte[] payload = record.getPayload();
                        String text = new String(payload,
                                Charset.forName("US-ASCII"));

                        final TextView helloWorldText = (TextView) findViewById(R.id.hello_world);
                        helloWorldText.setText(text);
                    }
                }
            }
        }
    }

}
