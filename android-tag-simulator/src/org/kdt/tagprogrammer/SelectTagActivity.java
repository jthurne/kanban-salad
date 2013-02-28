package org.kdt.tagprogrammer;

import java.nio.charset.Charset;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SelectTagActivity extends Activity {
    private static final String APP_MIME_TYPE = "application/vnd.kdt";
    private static final Charset ASCII = Charset.forName("US-ASCII");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tag);

        configureNFCTag(createMessage());
    }

    private void configureNFCTag(NdefMessage message) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
            return;
        nfcAdapter.setNdefPushMessage(message, this);
    }

    private NdefMessage createMessage() {
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                encode(APP_MIME_TYPE), new byte[0],
                encode("You've been BEAMED!"));

        return new NdefMessage(new NdefRecord[] { mimeRecord });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_tag, menu);
        return true;
    }

    private byte[] encode(String toConvert) {
        return toConvert.getBytes(ASCII);
    }

}
