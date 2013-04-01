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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

public class ProgramFragment extends Fragment implements IntentListener,
        ProgramView {

    private ProgramPresenter presenter;

    private View taskDetails;
    private View cellDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_program, container,
                false);

        taskDetails = rootView.findViewById(R.id.task_details);
        cellDetails = rootView.findViewById(R.id.cell_details);

        final Spinner spinner = (Spinner) rootView
                .findViewById(R.id.tag_type_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                if ("Task".equals(spinner.getSelectedItem())) {
                    presenter.typeChangedTo(TagType.TASK);
                }

                if ("Cell".equals(spinner.getSelectedItem())) {
                    presenter.typeChangedTo(TagType.CELL);
                }
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
        presenter = new ProgramPresenter(this);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.IntentListener#onNewIntent(android.content.Intent)
     */
    @Override
    public void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub

    }

}
