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

import org.kdt.Visible;
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

    public ProgramPresenter(ProgramView view, ProgramModel model,
            Programer tagProgrammer) {
        this.view = view;
        this.tagProgrammer = tagProgrammer;
        this.model = model;
    }

    public void typeChangedTo(TagType tagType) {
        if (tagType == TagType.CELL) {
            view.setIsCellDetailsVisible(true);
            view.setIsTaskDetailsVisible(false);
        } else {
            view.setIsCellDetailsVisible(false);
            view.setIsTaskDetailsVisible(true);
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
        TagType selectedTagType = view.getSelectedTagType();

        Programable tag = createTag(selectedTagType);
        ThereWas thereWas = tagProgrammer.programTag(tag);

        if (thereWas == ThereWas.A_TAG_TO_PROGRAM)
            view.showMessage(ProgramView.Message.TAG_PROGRAMMED);
    }

    private Programable createTag(TagType selectedTagType) {
        if (selectedTagType == TagType.TASK) {
            return new Task(
                    view.getTaskId(),
                    view.getTaskName(),
                    view.getTaskSize());
        }

        return new Cell(view.getSwimlane(), view.getQueue());
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
        if (selectedTag > -1)
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
}
