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
package org.kanbansalad.trackable;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Task NONE = new Task("", "", "");

    private final String id;
    private final String summary;
    private final String size;

    public Task(String id, String summary, String size) {
        this.id = id;
        this.summary = summary;
        this.size = size;
    }

    private Task(Builder builder) {
        this.id = builder.id;
        this.summary = builder.name;
        this.size = builder.size;
    }

    public String getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return summary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
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
