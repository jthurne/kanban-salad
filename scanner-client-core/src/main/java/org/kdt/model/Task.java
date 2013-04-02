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

public class Task implements Scanable, Programable {

    private final String id;
    private final String name;
    private final String size;

    public Task(String id, String name, String size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    private Task(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.size = builder.size;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String getDisplayName() {
        return "\t\t" + name;
    }

    @Override
    public String getDataString() {
        return id + TagType.FIELD_DELIMINATOR + name
                + TagType.FIELD_DELIMINATOR + size;
    }

    @Override
    public String getMimeType() {
        return TagType.TASK.mimeType();
    }

    @Override
    public String toString() {
        return name;
    }

    public static class Builder {
        private String id;
        private String name;
        private String size;

        public Task build() {
            return new Task(this);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }
    }

}
