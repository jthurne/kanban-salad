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
package org.kdt.scan;

import org.kdt.kanbandatatracker.R;
import org.kdt.scan.ScanModel;
import org.kdt.tag.Cell;
import org.kdt.tag.Scanable;
import org.kdt.tag.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScanModelAdaptor extends BaseAdapter {

    private static final int TASK_VIEW_TYPE = 0;
    private static final int CELL_VIEW_TYPE = 1;
    private static final int OTHER_VIEW_TYPE = 2;

    private final Context context;
    private final ScanModel model;

    public ScanModelAdaptor(Context context, ScanModel model) {
        this.context = context;
        this.model = model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return model.getCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Scanable getItem(int position) {
        return model.get(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.BaseAdapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        Scanable tag = this.getItem(position);

        // TODO ANOTHER InstanceOf check...
        if (tag instanceof Cell) {
            return CELL_VIEW_TYPE;
        }
        if (tag instanceof Task) {
            return TASK_VIEW_TYPE;
        }

        return OTHER_VIEW_TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.BaseAdapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Scanable tag = this.getItem(position);

        int viewType = getItemViewType(position);
        switch (viewType) {
        case CELL_VIEW_TYPE:
            return getViewForCell((Cell) tag, convertView, parent);
        case TASK_VIEW_TYPE:
            return getViewForTask((Task) tag, convertView, parent);
        default:
            return getViewForOtherTag(tag, convertView, parent);
        }
    }

    private View getViewForCell(Cell cell, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = this.getInflator().inflate(R.layout.scanned_cell, parent,
                    false);
        }

        TextView swimlaneView = (TextView) row.findViewById(R.id.swimlane);
        TextView queueView = (TextView) row.findViewById(R.id.queue);

        swimlaneView.setText(cell.getSwimlane());
        queueView.setText(cell.getQueue());

        return row;
    }

    private View getViewForTask(Task task, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = this.getInflator().inflate(R.layout.scanned_task, parent,
                    false);
        }

        TextView taskIdView = (TextView) row.findViewById(R.id.task_id);
        TextView taskNameView = (TextView) row.findViewById(R.id.task_name);
        TextView taskSizeView = (TextView) row.findViewById(R.id.task_size);

        taskIdView.setText(task.getId());
        taskNameView.setText(task.getName());
        taskSizeView.setText(task.getSize());

        return row;
    }

    private View getViewForOtherTag(Scanable tag, View convertView,
            ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = this.getInflator().inflate(R.layout.scanned_tag, parent,
                    false);
        }

        ((TextView) row).setText(tag.getDisplayName());

        return row;
    }

    private LayoutInflater getInflator() {
        return (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

}
