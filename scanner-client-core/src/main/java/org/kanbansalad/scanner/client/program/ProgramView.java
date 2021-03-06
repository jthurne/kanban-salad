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

import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.scanner.client.tag.TagType;

public interface ProgramView {
    public enum Message {
        TAG_PROGRAMMED, TASK_NOT_FOUND, TASK_LOOKUP_FAILED
    }

    public void setTaskDetailsVisible(Visible visible);

    public void setCellDetailsVisible(Visible visible);

    public void setLookupButtonVisible(Visible visible);

    public String getSwimlane();

    public String getQueue();

    public TagType getSelectedTagType();

    public String getTaskId();

    public String getTaskSummary();

    public String getTaskSize();

    public void showMessage(Message message);

    public void showException(Exception exception);

    public void showException(Message message, Exception exception);

    public void setSelectedTagType(TagType type);

    public void setTaskId(String id);

    public void setTaskSummary(String name);

    public void setTaskSize(String size);

    public void setSwimlane(String swimlane);

    public void setQueue(String queue);
}
