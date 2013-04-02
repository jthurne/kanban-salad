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

public enum TagType {
    TASK("application/vnd.kdt.task") {
        @Override
        public Scanable parse(String data) {
            String[] dataTokens = data.split(":");

            if (dataTokens.length < 3)
                return INCORRECTLY_FORMATTED.parse(data);

            return new Task(dataTokens[0], dataTokens[1], dataTokens[2]);
        }
    },

    CELL("application/vnd.kdt.cell") {
        @Override
        public Scanable parse(String data) {
            String[] dataTokens = data.split(":");

            if (dataTokens.length < 2)
                return INCORRECTLY_FORMATTED.parse(data);

            return new Cell(dataTokens[0], dataTokens[1]);
        }
    },

    EMPTY("") {
        @Override
        public Scanable parse(String data) {
            return new Empty();
        }
    },

    INCORRECTLY_FORMATTED("*") {
        @Override
        public Scanable parse(String data) {
            return new IncorrectlyFormatted(data);
        }
    };

    private final String mimeType;

    private TagType(String mimeType) {
        this.mimeType = mimeType;
    }

    public abstract Scanable parse(String data);

    public String mimeType() {
        return mimeType;
    }

    public static TagType forMimeType(String mimeType) {
        for (TagType type : TagType.values()) {
            if (type.mimeType.equals(mimeType)) {
                return type;
            }
        }

        return EMPTY;
    }
}
