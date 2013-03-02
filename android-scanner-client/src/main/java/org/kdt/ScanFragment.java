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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ScanFragment extends Fragment {

    private ArrayList<String> scanLogList;
    private ArrayAdapter<String> scanLog;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v("ScanFragment", "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        initScanLog();
        // @Override
        // public void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        // outState.putStringArrayList("scanLog", scanLogList);
        // }
        //
        // @Override
        // public void onActivityCreated(Bundle savedInstanceState) {
        // super.onActivityCreated(savedInstanceState);
        // if (savedInstanceState == null)
        // return;
        //
        // scanLog.clear();
        // scanLog.addAll(savedInstanceState.getStringArrayList("scanLog"));
        // }
        return rootView;
    }

    private void initScanLog() {
        scanLogList = new ArrayList<String>();
        scanLog = new ArrayAdapter<String>(this.getActivity(),
                R.layout.scanned_item, scanLogList);

        ListView scanLogView = (ListView) rootView.findViewById(R.id.scanLog);

        scanLogView.setAdapter(scanLog);
    }

    public void appendToLog(String textToDisplay) {
        scanLog.add(textToDisplay);
    }

    public void clearLog() {
        scanLog.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("scanLog", scanLogList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            return;

        scanLog.clear();
        scanLog.addAll(savedInstanceState.getStringArrayList("scanLog"));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("ScanFragment", "onResume");
    }
}
