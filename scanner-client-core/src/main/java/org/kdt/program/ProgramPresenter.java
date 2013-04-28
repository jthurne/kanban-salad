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
package org.kdt.program;

import static org.kdt.CommonConstants.NONE;
import static org.kdt.Visible.HIDDEN;
import static org.kdt.Visible.VISIBLE;
import static org.kdt.program.ProgramView.Message.TAG_PROGRAMMED;
import static org.kdt.program.ProgramView.Message.TASK_NOT_FOUND;
import static org.kdt.program.Programer.ThereWas.A_TAG_TO_PROGRAM;
import static org.kdt.tag.TagType.CELL;
import static org.kdt.tag.TagType.TASK;

import org.kdt.Settings;
import org.kdt.Visible;
import org.kdt.program.ProgramView.Message;
import org.kdt.program.Programer.ThereWas;
import org.kdt.tag.Cell;
import org.kdt.tag.Programable;
import org.kdt.tag.Scanable;
import org.kdt.tag.TagType;
import org.kdt.tag.Task;

public class ProgramPresenter {

    private final ProgramView view;
    private final ProgramModel model;
    private final Programer tagProgrammer;
    private final Settings settings;
    private final TaskFinder taskFinder;

    public ProgramPresenter(
            ProgramView view,
            ProgramModel model,
            Settings settings,
            Programer tagProgrammer,
            TaskFinder taskFinder) {

        this.view = view;
        this.tagProgrammer = tagProgrammer;
        this.model = model;
        this.settings = settings;
        this.taskFinder = taskFinder;
    }

    public void viewInitalized() {
        settingsUpdated();
    }

    public void settingsUpdated() {
        if (settings.isBluetoothSupported() && settings.isBluetoothEnabled()) {
            view.setLookupButtonVisible(VISIBLE);
        } else {
            view.setLookupButtonVisible(HIDDEN);
        }
    }

    public void typeChangedTo(TagType tagType) {
        if (tagType == CELL) {
            view.setCellDetailsVisible(VISIBLE);
            view.setTaskDetailsVisible(HIDDEN);
        } else {
            view.setCellDetailsVisible(HIDDEN);
            view.setTaskDetailsVisible(VISIBLE);
        }
    }

    public void tagTapped() {
        try {
            attemptToProgramTag();
        } catch (ProgramingException e) {
            view.showException(e);
        }
    }

    private void attemptToProgramTag() {
        Programable tag = createTag();
        ThereWas thereWas = tagProgrammer.programTag(tag);

        if (thereWas == A_TAG_TO_PROGRAM) {
            view.showMessage(TAG_PROGRAMMED);
            replaceSelectedTagWith(tag);
        }
    }

    private Programable createTag() {
        TagType selectedTagType = view.getSelectedTagType();

        if (selectedTagType == TASK) {
            return new Task(
                    view.getTaskId(),
                    view.getTaskName(),
                    view.getTaskSize());
        }

        return new Cell(view.getSwimlane(), view.getQueue());
    }

    private void replaceSelectedTagWith(Programable tag) {
        if (shouldReplaceSelectedTag()) {
            model.replace(model.getSelectedTagIndex(), tag);
        }
    }

    private boolean shouldReplaceSelectedTag() {
        return model.getSelectedTagIndex() != NONE;
    }

    public void visibilityChanged(Visible visible) {
        Scanable tag = getSelectedTag();

        // TODO Another instanceof test...hmm
        if (tag instanceof Task) {
            resetViewForTask((Task) tag);
        } else if (tag instanceof Cell) {
            resetViewForCell((Cell) tag);
        } else {
            clearView();
        }
    }

    private Scanable getSelectedTag() {
        int selectedTag = model.getSelectedTagIndex();
        if (selectedTag != NONE)
            return model.get(selectedTag);

        return null;
    }

    private void resetViewForTask(Task task) {
        view.setSelectedTagType(TagType.TASK);
        view.setTaskId(task.getId());
        view.setTaskName(task.getName());
        view.setTaskSize(task.getSize());
    }

    private void resetViewForCell(Cell cell) {
        view.setSelectedTagType(TagType.CELL);
        view.setSwimlane(cell.getSwimlane());
        view.setQueue(cell.getQueue());
    }

    private void clearView() {
        view.setSelectedTagType(TagType.TASK);
        view.setTaskId("");
        view.setTaskName("");
        view.setTaskSize("");
    }

    public void lookupClicked() {
        if (thereIsNoIdToLookUp())
            return;

        try {
            attemptLookup();
        } catch (TaskFinder.LookupFailed e) {
            view.showException(Message.TASK_LOOKUP_FAILED, e);
        }
    }

    private boolean thereIsNoIdToLookUp() {
        return view.getTaskId() == null || view.getTaskId().isEmpty();
    }

    private void attemptLookup() {
        Task task = taskFinder.find(view.getTaskId());

        if (task == Task.NONE) {
            view.showMessage(TASK_NOT_FOUND);
        } else {
            view.setTaskName(task.getName());
            view.setTaskSize(task.getSize());
        }
    }

}
