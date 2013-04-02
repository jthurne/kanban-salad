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

import org.kdt.kanbandatatracker.R;
import org.kdt.model.TagType;
import org.kdt.program.ProgramPresenter;
import org.kdt.program.ProgramView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
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
    private Spinner tagTypeSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_program,
                container,
                false);

        taskDetails = rootView.findViewById(R.id.task_details);
        cellDetails = rootView.findViewById(R.id.cell_details);

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
                        // taskDetails.setVisibility(View.GONE);
                        // cellDetails.setVisibility(View.GONE);
                    }
                });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        presenter = new ProgramPresenter(this, new NfcProgramer(activity));
    }

    @Override
    public void setIsTaskDetailsVisible(boolean isVisible) {
        if (isVisible) {
            taskDetails.setVisibility(View.VISIBLE);
        } else {
            taskDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public void setIsCellDetailsVisible(boolean isVisible) {
        if (isVisible) {
            cellDetails.setVisibility(View.VISIBLE);
        } else {
            cellDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        presenter.tagScanned();
    }

    @Override
    public void showMessage(Message message) {
        int messageId = stringIdFor(message);
        Toast.makeText(this.getActivity(),
                getString(messageId),
                Toast.LENGTH_LONG).show();
    }

    private int stringIdFor(Message message) {
        switch (message) {
        case TAG_PROGRAMMED:
            return R.string.tag_programmed;
        }
        return -1;
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

}
