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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ScanFragment extends Fragment {

    private ArrayList<String> scanLogList;
    private ArrayAdapter<String> scanLog;

    private View rootView;
    private ListView scanLogView;

    private ActionMode mActionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v("ScanFragment", "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        initScanLog();
        return rootView;
    }

    private void initScanLog() {
        scanLogList = new ArrayList<String>();
        scanLog = new ArrayAdapter<String>(this.getActivity(),
                R.layout.scanned_item, scanLogList);

        scanLogView = (ListView) rootView.findViewById(R.id.scanLog);
        scanLogView.setAdapter(scanLog);

        enableContextActionBarWhenSelectingScannedItems(scanLogView);
    }

    private void enableContextActionBarWhenSelectingScannedItems(
            ListView scanLogView) {
        scanLogView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        scanLogView.setOnItemClickListener(new ItemSelectedListener());
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

    private class ItemSelectedListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (mActionMode != null) {
                return;
            }
            mActionMode = getActivity().startActionMode(
                    new ActionModeCallback());
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.scanned_item, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after
        // onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            case R.id.action_delete_scan:
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.action_program_scan:
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            int selectedItem = scanLogView.getCheckedItemPosition();
            scanLogView.setItemChecked(selectedItem, false);
            mActionMode = null;
        }
    };
}
