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
package org.kanbansalad.scanner.client.android.scan;

import org.kanbansalad.scanner.R;
import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.scanner.client.android.IntentListener;
import org.kanbansalad.scanner.client.android.ModelProvider;
import org.kanbansalad.scanner.client.scan.ScanPresenter;
import org.kanbansalad.scanner.client.scan.ScanView;
import org.kanbansalad.scanner.client.tag.ScanableTag;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ScanFragment extends Fragment implements ScanView, IntentListener {

    private ScanModelAdaptor scannedTagsAdaptor;

    private View rootView;
    private ListView scannedTagsView;

    private ActionMode actionMode;

    private ScanPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v("ScanFragment", "onCreateView");
        this.setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        initScannedTagsView();
        initSaveButton();

        return rootView;
    }

    private void initSaveButton() {
        ImageButton saveButton = (ImageButton) rootView
                .findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendClicked();
            }
        });
    }

    private void initScannedTagsView() {
        scannedTagsView = findScannedTagsView();

        scannedTagsAdaptor = new ScanModelAdaptor(this.getActivity(),
                ModelProvider.getInstance());

        scannedTagsView.setAdapter(scannedTagsAdaptor);

        enableContextActionBarWhenSelectingScannedItems(scannedTagsView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scan_tag, menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        presenter = new ScanPresenter(this, ModelProvider.getInstance(),
                new NfcScanner(activity), new EmailSender(this.getActivity()));
    }

    @Override
    public void onNewIntent(Intent intent) {
        presenter.tagTapped();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_clear:
            presenter.clearClicked();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
    public void selectTag(int position) {
        scannedTagsView.performItemClick(null, position, -1);
        scannedTagsView.smoothScrollToPosition(position);
    }

    @Override
    public void appendToTags(ScanableTag tag) {
        // We don't actually need to add the tag because the adaptor is backed
        // by the same model as the presenter (the adaptor displays whatever is
        // in the model).
        // But we do need to notify the adaptor that its data has changed
        refreshTags();
    }

    @Override
    public void deleteTag(int logEntryIndex) {
        // We don't actually need to delete the tag because the adaptor is
        // backed
        // by the same model as the presenter (the adaptor displays whatever is
        // in the model).
        // But we do need to notify the adaptor that its data has changed
        refreshTags();
    }

    @Override
    public void clearTags() {
        // We don't actually need to clear because the adaptor is backed
        // by the same model as the presenter (the adaptor displays whatever is
        // in the model).
        // But we do need to notify the adaptor that its data has changed
        refreshTags();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.scan.ScanView#refreshTagListView()
     */
    @Override
    public void refreshTags() {
        if (scannedTagsAdaptor != null)
            scannedTagsAdaptor.notifyDataSetChanged();
    }

    @Override
    public void hideSoftKeyboard() {
        if (getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public Visible getVisisble() {
        return Visible.valueOf(this.getUserVisibleHint());
    }

    @Override
    public Visible getContextMenuVisible() {
        return Visible.valueOf(actionMode != null);
    }

    @Override
    public void showTagContextMenu() {
        if (actionMode != null) {
            return;
        }
        actionMode = getActivity().startActionMode(
                new ScannedTagActionModeCallback());
    }

    @Override
    public void closeTagContextMenu() {
        if (actionMode != null)
            actionMode.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putStringArrayList("scannedTags", scannedTagsList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            return;
    }

    @Override
    public void showException(Exception e) {
        // TODO we might want to show a dialog box instead
        Toast.makeText(this.getActivity(),
                e.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (presenter != null)
            presenter.visibilityChanged(Visible.valueOf(isVisibleToUser));
    }

    private class ItemSelectedListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            presenter.tagSelected(position);
        }
    }

    private class ScannedTagActionModeCallback implements ActionMode.Callback {

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
            presenter.tagUnselected();
            int selectedItem = scannedTagsView.getCheckedItemPosition();
            scannedTagsView.setItemChecked(selectedItem, false);
            actionMode = null;
        }
    };
}
