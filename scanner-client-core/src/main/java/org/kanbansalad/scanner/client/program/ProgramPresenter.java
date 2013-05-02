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
package org.kanbansalad.scanner.client.program;

import static org.kanbansalad.scanner.client.CommonConstants.NONE;
import static org.kanbansalad.scanner.client.Visible.HIDDEN;
import static org.kanbansalad.scanner.client.Visible.VISIBLE;
import static org.kanbansalad.scanner.client.program.ProgramView.Message.TAG_PROGRAMMED;
import static org.kanbansalad.scanner.client.program.ProgramView.Message.TASK_NOT_FOUND;
import static org.kanbansalad.scanner.client.program.Programer.ThereWas.A_TAG_TO_PROGRAM;
import static org.kanbansalad.scanner.client.tag.TagType.CELL;
import static org.kanbansalad.scanner.client.tag.TagType.TASK;

import org.kanbansalad.scanner.client.BackgroundAction;
import org.kanbansalad.scanner.client.BackgroundExecutor;
import org.kanbansalad.scanner.client.Settings;
import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.scanner.client.program.ProgramView.Message;
import org.kanbansalad.scanner.client.program.Programer.ThereWas;
import org.kanbansalad.scanner.client.tag.CellTag;
import org.kanbansalad.scanner.client.tag.ProgramableTag;
import org.kanbansalad.scanner.client.tag.ScanableTag;
import org.kanbansalad.scanner.client.tag.TagType;
import org.kanbansalad.scanner.client.tag.TaskTag;
import org.kanbansalad.trackable.Task;

public class ProgramPresenter {

    private final ProgramView view;
    private final ProgramModel model;
    private final Programer tagProgrammer;
    private final Settings settings;
    private final TaskFinder taskFinder;
    private final BackgroundExecutor executor;

    public ProgramPresenter(
            ProgramView view,
            ProgramModel model,
            Settings settings,
            Programer tagProgrammer,
            TaskFinder taskFinder,
            BackgroundExecutor executor) {

        this.view = view;
        this.tagProgrammer = tagProgrammer;
        this.model = model;
        this.settings = settings;
        this.taskFinder = taskFinder;
        this.executor = executor;
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
        ProgramableTag tag = createTag();
        ThereWas thereWas = tagProgrammer.programTag(tag);

        if (thereWas == A_TAG_TO_PROGRAM) {
            view.showMessage(TAG_PROGRAMMED);
            replaceSelectedTagWith(tag);
        }
    }

    private ProgramableTag createTag() {
        TagType selectedTagType = view.getSelectedTagType();

        if (selectedTagType == TASK) {
            return new TaskTag(
                    view.getTaskId(),
                    view.getTaskSummary(),
                    view.getTaskSize());
        }

        return new CellTag(view.getSwimlane(), view.getQueue());
    }

    private void replaceSelectedTagWith(ProgramableTag tag) {
        if (shouldReplaceSelectedTag()) {
            model.replace(model.getSelectedTagIndex(), tag);
        }
    }

    private boolean shouldReplaceSelectedTag() {
        return model.getSelectedTagIndex() != NONE;
    }

    public void visibilityChanged(Visible visible) {
        ScanableTag tag = getSelectedTag();

        // TODO Another instanceof test...hmm
        if (tag instanceof TaskTag) {
            resetViewForTask((TaskTag) tag);
        } else if (tag instanceof CellTag) {
            resetViewForCell((CellTag) tag);
        } else {
            clearView();
        }
    }

    private ScanableTag getSelectedTag() {
        int selectedTag = model.getSelectedTagIndex();
        if (selectedTag != NONE)
            return model.get(selectedTag);

        return null;
    }

    private void resetViewForTask(TaskTag task) {
        view.setSelectedTagType(TagType.TASK);
        view.setTaskId(task.getId());
        view.setTaskSummary(task.getSummary());
        view.setTaskSize(task.getSize());
    }

    private void resetViewForCell(CellTag cell) {
        view.setSelectedTagType(TagType.CELL);
        view.setSwimlane(cell.getSwimlane());
        view.setQueue(cell.getQueue());
    }

    private void clearView() {
        view.setSelectedTagType(TagType.TASK);
        view.setTaskId("");
        view.setTaskSummary("");
        view.setTaskSize("");
    }

    public void lookupClicked() {
        if (thereIsNoIdToLookUp())
            return;

        executor.execute(new BackgroundAction<Task>() {
            @Override
            public Task doAction() {
                return taskFinder.find(view.getTaskId());
            }

            @Override
            public void onComplete(Task task) {
                if (task.equals(Task.NONE)) {
                    view.showMessage(TASK_NOT_FOUND);
                } else {
                    view.setTaskSummary(task.getSummary());
                    view.setTaskSize(task.getSize());
                }
            }

            @Override
            public void onException(Exception e) {
                view.showException(Message.TASK_LOOKUP_FAILED, e);
            }
        });
    }

    private boolean thereIsNoIdToLookUp() {
        return view.getTaskId() == null || view.getTaskId().isEmpty();
    }
}
