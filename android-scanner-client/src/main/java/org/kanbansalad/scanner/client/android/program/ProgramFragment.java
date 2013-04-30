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
package org.kanbansalad.scanner.client.android.program;

import static org.kanbansalad.scanner.client.CommonConstants.NONE;
import static org.kanbansalad.scanner.client.Visible.VISIBLE;

import org.kanbansalad.scanner.R;
import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.scanner.client.android.AsyncTaskExecutor;
import org.kanbansalad.scanner.client.android.IntentListener;
import org.kanbansalad.scanner.client.android.ModelProvider;
import org.kanbansalad.scanner.client.android.SharedPrefsSettings;
import org.kanbansalad.scanner.client.program.BluetoothTaskFinder;
import org.kanbansalad.scanner.client.program.ProgramPresenter;
import org.kanbansalad.scanner.client.program.ProgramView;
import org.kanbansalad.trackable.TagType;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ProgramFragment extends Fragment implements IntentListener,
        ProgramView {

    private ProgramPresenter presenter;

    private View taskDetails;
    private View cellDetails;
    private View lookupButton;
    private Spinner tagTypeSpinner;

    private OnSharedPreferenceChangeListener sharedPrefsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_program,
                container,
                false);

        taskDetails = rootView.findViewById(R.id.task_details);
        cellDetails = rootView.findViewById(R.id.cell_details);
        initLookupButton();
        initTagTypeSpinner(rootView);

        presenter.viewInitalized();
        initSharedPrefsListener();

        return rootView;
    }

    /**
     * 
     */
    private void initLookupButton() {
        lookupButton = taskDetails.findViewById(R.id.lookup_button);
        lookupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.lookupClicked();
            }
        });
    }

    private void initSharedPrefsListener() {
        sharedPrefsListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(
                    SharedPreferences sharedPreferences,
                    String key) {
                presenter.settingsUpdated();
            }
        };

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(sharedPrefsListener);
    }

    private void initTagTypeSpinner(final View rootView) {
        tagTypeSpinner = (Spinner) rootView
                .findViewById(R.id.tag_type_spinner);
        tagTypeSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view,
                            int pos, long id) {
                        presenter.typeChangedTo(getSelectedTagType());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        presenter = new ProgramPresenter(
                this,
                ModelProvider.getInstance(),
                new SharedPrefsSettings(activity),
                new NfcProgramer(activity),
                new BluetoothTaskFinder(new AndroidBluetoothConnectionProvider(
                        activity)),
                new AsyncTaskExecutor(activity));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(sharedPrefsListener);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (presenter != null)
            presenter.visibilityChanged(Visible.valueOf(isVisibleToUser));
    }

    @Override
    public void setTaskDetailsVisible(Visible is) {
        if (is == VISIBLE) {
            taskDetails.setVisibility(View.VISIBLE);
        } else {
            taskDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCellDetailsVisible(Visible is) {
        if (is == VISIBLE) {
            cellDetails.setVisibility(View.VISIBLE);
        } else {
            cellDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLookupButtonVisible(Visible is) {
        if (is == VISIBLE) {
            lookupButton.setVisibility(View.VISIBLE);
        } else {
            lookupButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        presenter.tagTapped();
    }

    @Override
    public void showMessage(Message message) {
        int messageId = stringIdFor(message);
        Toast.makeText(this.getActivity(),
                getString(messageId),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showException(Exception e) {
        // TODO use a dialog box instead?
        Toast.makeText(this.getActivity(),
                e.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showException(Message message, Exception e) {
        int messageId = stringIdFor(message);

        // TODO use a dialog box instead?
        Toast.makeText(this.getActivity(),
                getString(messageId, e.getMessage()),
                Toast.LENGTH_LONG).show();

    }

    private int stringIdFor(Message message) {
        switch (message) {
        case TAG_PROGRAMMED:
            return R.string.tag_programmed;
        case TASK_NOT_FOUND:
            return R.string.task_not_found;
        case TASK_LOOKUP_FAILED:
            return R.string.task_lookup_failed;
        }
        return NONE;
    }

    @Override
    public String getSwimlane() {
        return getTextFrom(cellDetails, R.id.swimlane_edit);
    }

    @Override
    public String getQueue() {
        return getTextFrom(cellDetails, R.id.queue_edit);
    }

    @Override
    public TagType getSelectedTagType() {
        if ("Task".equals(tagTypeSpinner.getSelectedItem())) {
            return TagType.TASK;
        }

        return TagType.CELL;
    }

    @Override
    public String getTaskId() {
        return getTextFrom(taskDetails, R.id.task_id_edit);
    }

    @Override
    public String getTaskName() {
        return getTextFrom(taskDetails, R.id.task_name_edit);
    }

    @Override
    public String getTaskSize() {
        return getTextFrom(taskDetails, R.id.task_size_edit);
    }

    private String getTextFrom(View container, int id) {
        return ((EditText) container.findViewById(id)).getText().toString();
    }

    @Override
    public void setSelectedTagType(TagType type) {
        switch (type) {
        case TASK:
            tagTypeSpinner.setSelection(0);
            break;
        case CELL:
            tagTypeSpinner.setSelection(1);
            break;
        default:
            throw new InvalidTagTypeProvided(type);
        }
    }

    private static class InvalidTagTypeProvided extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InvalidTagTypeProvided(TagType tagType) {
            super("Invalid TagType provided: " + tagType);
        }
    }

    @Override
    public void setTaskId(String id) {
        setTextOn(taskDetails, R.id.task_id_edit, id);
    }

    @Override
    public void setTaskName(String name) {
        setTextOn(taskDetails, R.id.task_name_edit, name);
    }

    @Override
    public void setTaskSize(String size) {
        setTextOn(taskDetails, R.id.task_size_edit, size);
    }

    @Override
    public void setSwimlane(String swimlane) {
        setTextOn(cellDetails, R.id.swimlane_edit, swimlane);
    }

    @Override
    public void setQueue(String queue) {
        setTextOn(cellDetails, R.id.queue_edit, queue);
    }

    private void setTextOn(View container, int id, String text) {
        ((EditText) container.findViewById(id)).setText(text);
    }
}
