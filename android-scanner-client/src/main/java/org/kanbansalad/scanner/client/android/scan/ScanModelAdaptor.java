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
import org.kanbansalad.scanner.client.scan.ScanModel;
import org.kanbansalad.scanner.client.tag.CellTag;
import org.kanbansalad.scanner.client.tag.ScanableTag;
import org.kanbansalad.scanner.client.tag.TaskTag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScanModelAdaptor extends BaseAdapter {
    private static final int NUM_VIEW_TYPES = 3;
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
     * @see android.widget.BaseAdapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        ScanableTag tag = this.getItem(position);

        // TODO ANOTHER InstanceOf check...
        if (tag instanceof CellTag) {
            return CELL_VIEW_TYPE;
        }
        if (tag instanceof TaskTag) {
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
        return NUM_VIEW_TYPES;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScanableTag tag = this.getItem(position);

        int viewType = getItemViewType(position);
        switch (viewType) {
        case CELL_VIEW_TYPE:
            return getViewForCell((CellTag) tag, convertView, parent);
        case TASK_VIEW_TYPE:
            return getViewForTask((TaskTag) tag, convertView, parent);
        default:
            return getViewForOtherTag(tag, convertView, parent);
        }
    }

    private View getViewForCell(CellTag cell, View convertView, ViewGroup parent) {
        View row = createOrReuse(convertView, R.layout.scanned_cell, parent);

        updateText(row, R.id.swimlane, cell.getSwimlane());
        updateText(row, R.id.queue, cell.getQueue());

        return row;
    }

    private View getViewForTask(TaskTag task, View convertView, ViewGroup parent) {
        View row = createOrReuse(convertView, R.layout.scanned_task, parent);

        updateText(row, R.id.task_id, task.getId());
        updateText(row, R.id.task_name, task.getSummary());
        updateText(row, R.id.task_size, task.getSize());

        return row;
    }

    private View getViewForOtherTag(ScanableTag tag, View convertView,
            ViewGroup parent) {
        View row = createOrReuse(convertView, R.layout.scanned_tag, parent);

        ((TextView) row).setText(tag.getDisplayName());

        return row;
    }

    private View createOrReuse(View row, int layoutId, ViewGroup parent) {
        if (row == null) {
            return this.getInflator().inflate(layoutId, parent,
                    false);
        }

        return row;
    }

    private LayoutInflater getInflator() {
        return (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    private void updateText(View row, int id, String text) {
        TextView textView = (TextView) row.findViewById(id);
        textView.setText(text);
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
    public ScanableTag getItem(int position) {
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
}
