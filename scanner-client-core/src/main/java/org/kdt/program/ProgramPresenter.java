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

import org.kdt.model.Cell;
import org.kdt.model.Scanable;
import org.kdt.model.TagType;
import org.kdt.model.Task;

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

    public void tagScanned() {
        try {
            attemptToProgramTag();
        } catch (ProgramingException e) {
            view.showException(e);
        }
    }

    private void attemptToProgramTag() {
        TagType selectedTagType = view.getSelectedTagType();

        if (selectedTagType == TagType.TASK) {
            tagProgrammer.programTag(new Task(
                    view.getTaskId(),
                    view.getTaskName(),
                    view.getTaskSize()));
        }

        if (selectedTagType == TagType.CELL) {
            tagProgrammer.programTag(
                    new Cell(view.getSwimlane(), view.getQueue()));
        }

        view.showMessage(ProgramView.Message.TAG_PROGRAMMED);
    }

    public void tagSelected(int position) {
        Scanable tag = model.getTagAt(position);

        // TODO Another instanceof test...hmm
        if (tag instanceof Task) {
            Task task = (Task) tag;
            view.setSelectedTagType(TagType.TASK);
            view.setTaskId(task.getId());
            view.setTaskName(task.getName());
            view.setTaskSize(task.getSize());
        }

        if (tag instanceof Cell) {
            Cell cell = (Cell) tag;
            view.setSelectedTagType(TagType.CELL);
            view.setSwimlane(cell.getSwimlane());
            view.setQueue(cell.getQueue());
        }
    }
}
