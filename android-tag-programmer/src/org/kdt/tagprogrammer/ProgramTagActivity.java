package org.kdt.tagprogrammer;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class ProgramTagActivity extends Activity {
    private static final String TASK_MIME_TYPE = "application/vnd.kdt.task";
    private static final String CELL_MIME_TYPE = "application/vnd.kdt.cell";
    private static final String LEGACY_MIME_TYPE = "application/vnd.kdt";
    private static final Charset ASCII = Charset.forName("US-ASCII");
    
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_tag);
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcIsNotAvailable()) {
            tellTheUserNfcIsNotAvailable();
            finish();
            return;
        }
        
        createObjectsToInterceptNfcIntents();
    }

    private boolean nfcIsNotAvailable() {
        return nfcAdapter == null;
    }

    private void tellTheUserNfcIsNotAvailable() {
        Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
    }

    private void createObjectsToInterceptNfcIntents() {
        nfcPendingIntent = createPendingIntentForNFCIntents();
        intentFilters = createNfcIntentFilters();
        techLists = createNfcTechLists();
    }

    private PendingIntent createPendingIntentForNFCIntents() {
        return PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private IntentFilter[] createNfcIntentFilters() {
        IntentFilter alreadyProgrammed = createIntentFilterForAlreadyProgrammedTags();
        IntentFilter emptyTag = createIntentFilterForEmptyTags();
        return new IntentFilter[] {alreadyProgrammed, emptyTag};
    }

    private IntentFilter createIntentFilterForAlreadyProgrammedTags() {
        try {
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            ndef.addDataType(LEGACY_MIME_TYPE);
            ndef.addDataType(CELL_MIME_TYPE);
            ndef.addDataType(TASK_MIME_TYPE);
            return ndef;
        }
        catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
    }

    private IntentFilter createIntentFilterForEmptyTags() {
        return new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
    }

    private String[][] createNfcTechLists() {
        return new String[][] { new String[] { Ndef.class.getName() } };
    }

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, intentFilters, techLists);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.program_tag, menu);
        return true;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (aTagWeCanHandleWasScanned(intent)) {
            Ndef ndefTag = getNdefTagFrom(intent);
            
            if (!ndefTag.isWritable()) {
                tellTheUserTagIsNotWritable();
                return;
            }
            
            program(ndefTag);
        }
    }

    private void program(Ndef ndefTag) {
        try {
            ndefTag.connect();
            ndefTag.writeNdefMessage(createMessage());
            tellTheUserTheTagWasProgrammed();                
        } catch (IOException e) {
            tellTheUserProgrammingFailed(e);                
            e.printStackTrace();
        } catch (FormatException e) {
            tellTheUserProgrammingFailed(e);                
            e.printStackTrace();
        } finally {
            close(ndefTag);
        }
    }

    private void tellTheUserTheTagWasProgrammed() {
        Toast.makeText(this, "Tag successfully programmed", Toast.LENGTH_LONG).show();
    }

    private void tellTheUserProgrammingFailed(Exception e) {
        Toast.makeText(this, "Failed to program tag: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void tellTheUserTagIsNotWritable() {
        Toast.makeText(this, "Tag is not writable", Toast.LENGTH_LONG).show();
    }

    private void close(Ndef ndefTag) {
        try {
            ndefTag.close();
        } catch (IOException e) {
            tellTheUserClosingTheTagFailed(e);
            e.printStackTrace();
        }
    }

    private void tellTheUserClosingTheTagFailed(IOException e) {
        Toast.makeText(this, "Failed to close the tag: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private Ndef getNdefTagFrom(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        return Ndef.get(tagFromIntent);
    }

    private boolean aTagWeCanHandleWasScanned(Intent intent) {
        return NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || 
            NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

    private NdefMessage createMessage() {
        NdefRecord mimeRecord = createMimeRecord();
        return new NdefMessage(new NdefRecord[] { mimeRecord });
    }

    private NdefRecord createMimeRecord() {
        String mimeType = findMimeType();
        String tagData = getTagData();
        
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                encode(mimeType), new byte[0],
                encode(tagData));
    }

    private String findMimeType() {
        RadioButton taskButton = (RadioButton) findViewById(R.id.task_radio);
        
        String mimeType = CELL_MIME_TYPE;
        if (taskButton.isChecked()) {
            mimeType = TASK_MIME_TYPE;
        }
        return mimeType;
    }

    private String getTagData() {
        EditText tagDataText = (EditText) findViewById(R.id.tag_data);
        return tagDataText.getText().toString();
    }

    private byte[] encode(String toConvert) {
        return toConvert.getBytes(ASCII);
    }

}
