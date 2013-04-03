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
package org.kdt.model;

public class Cell implements Scanable, Programable {

    private final String swimlane;
    private final String queue;

    public Cell(String swimlane, String queue) {
        this.swimlane = swimlane;
        this.queue = queue;
    }

    private Cell(Builder builder) {
        this.swimlane = builder.swimlane;
        this.queue = builder.queue;
    }

    public String getSwimlane() {
        return swimlane;
    }

    public String getQueue() {
        return queue;
    }

    @Override
    public String getDisplayName() {
        return swimlane + " - " + queue;
    }

    @Override
    public String getDataString() {
        return swimlane + TagType.FIELD_DELIMITER + queue;
    }

    @Override
    public String getMimeType() {
        return TagType.CELL.mimeType();
    }

    public static class Builder {
        private String swimlane;
        private String queue;

        public Cell build() {
            return new Cell(this);
        }

        public Builder swimlane(String swimlane) {
            this.swimlane = swimlane;
            return this;
        }

        public Builder queue(String queue) {
            this.queue = queue;
            return this;
        }
    }
}
