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
import org.kdt.scan.ListScanModel;
import org.kdt.scan.ScanPresenter;
import org.kdt.scan.ScanView;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;

public class ScanFragment extends Fragment implements ScanView, IntentListener {

    private ArrayList<String> scannedTagsList;
    private ArrayAdapter<String> scannedTags;

    private View rootView;
    private ListView scannedTagsView;

    private ActionMode mActionMode;

    private ScanPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v("ScanFragment", "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        initScannedTagsView();
        initSaveButton();

        return rootView;
    }

    private void initSaveButton() {
        Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveClicked();
            }
        });
    }

    private void initScannedTagsView() {
        scannedTagsView = findScannedTagsView();

        scannedTagsList = new ArrayList<String>();
        scannedTags = new ArrayAdapter<String>(this.getActivity(),
                R.layout.scanned_tag, scannedTagsList);

        scannedTagsView.setAdapter(scannedTags);

        enableContextActionBarWhenSelectingScannedItems(scannedTagsView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        presenter = new ScanPresenter(this, new ListScanModel(),
                new NfcScanner(activity));
    }

    @Override
    public void onNewIntent(Intent intent) {
        presenter.tagScanned();
    }

    private ListView findScannedTagsView() {
        return (ListView) rootView.findViewById(R.id.scanned_tags_list);
    }

    private void enableContextActionBarWhenSelectingScannedItems(
            ListView scannedTagsView) {
        scannedTagsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        scannedTagsView.setOnItemClickListener(new ItemSelectedListener());
    }

    @Override
    public void appendToScannedTags(String textToDisplay) {
        scannedTags.add(textToDisplay);
    }

    @Override
    public void selectScannedTag(int position) {
        scannedTagsView.performItemClick(null, position, -1);
    }

    @Override
    public void deleteScannedTag(int logEntryIndex) {
        scannedTagsList.remove(logEntryIndex);
    }

    @Override
    public void clearScannedTags() {
        scannedTags.clear();
    }

    @Override
    public void showScannedTagContextMenu() {
        if (mActionMode != null) {
            return;
        }
        mActionMode = getActivity().startActionMode(new ActionModeCallback());
    }

    @Override
    public void closeScannedTagContextMenu() {
        mActionMode.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("scannedTags", scannedTagsList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            return;

        scannedTags.clear();
        scannedTags
                .addAll(savedInstanceState.getStringArrayList("scannedTags"));
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
            presenter.tagSelected(position);
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.scanned_tag, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int selectedTag = scannedTagsView.getCheckedItemPosition();

            switch (item.getItemId()) {
            case R.id.action_delete_scan:
                presenter.deleteTagClicked(selectedTag);
                return true;
            default:
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            int selectedItem = scannedTagsView.getCheckedItemPosition();
            scannedTagsView.setItemChecked(selectedItem, false);
            mActionMode = null;
        }
    };
}
