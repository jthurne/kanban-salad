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

import java.util.ArrayList;

import org.kdt.kanbandatatracker.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ScanActivity extends Activity implements ScanView {

    private final ScanPresenter presenter;

    private ArrayList<String> scanLogList;
    private ArrayAdapter<String> scanLog;
    private NfcForegroundDispatchController nfcDispatchController;

    public ScanActivity() {
        presenter = new ScanPresenter(this, new NfcScanner(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initScanLog();
        nfcDispatchController = new NfcForegroundDispatchController(this);

        presenter.tryToScanTag();
    }

    private void initScanLog() {
        scanLogList = new ArrayList<String>();
        scanLog = new ArrayAdapter<String>(this, R.layout.scanned_item,
                scanLogList);

        ListView scanLogView = (ListView) findViewById(R.id.scanLog);
        scanLogView.setAdapter(scanLog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        presenter.tryToScanTag();
    }

    public void appendToLog(String textToDisplay) {
        scanLog.add(textToDisplay);
    }

    public void clearLog() {
        scanLog.clear();
    }

    public void helpMenuItemClicked(MenuItem item) {
        presenter.helpMenuItemClicked();
    }

    public void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("scanLog", scanLogList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scanLog.clear();
        scanLog.addAll(savedInstanceState.getStringArrayList("scanLog"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcDispatchController.disableForegroundDispatch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcDispatchController.enableForegroundDispatch();
    }

    public void saveSnapshot(View view) {
        presenter.saveSnapshot();
    }
}
