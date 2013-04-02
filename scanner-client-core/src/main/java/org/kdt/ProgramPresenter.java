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

import org.kdt.model.Cell;
import org.kdt.model.TagType;
import org.kdt.model.Task;

public class ProgramPresenter {

    private final ProgramView view;
    private final Programer tagProgrammer;

    public ProgramPresenter(ProgramView view, Programer tagProgrammer) {
        this.view = view;
        this.tagProgrammer = tagProgrammer;
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

        // TODO Handle exceptions thrown by tagProgrammer
    }
}
