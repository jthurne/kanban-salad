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
package org.kanbansalad.scanner.client.tag;

public enum TagType {
    TASK("application/vnd.knbnsld.t") {
        @Override
        public ScanableTag parse(String data) {
            String[] dataTokens = data.split(FIELD_DELIMITER);

            if (dataTokens.length < 3)
                return INCORRECTLY_FORMATTED.parse(data);

            return new TaskTag(dataTokens[0], dataTokens[1], dataTokens[2]);
        }
    },

    CELL("application/vnd.knbnsld.c") {
        @Override
        public ScanableTag parse(String data) {
            String[] dataTokens = data.split(FIELD_DELIMITER);

            if (dataTokens.length < 2)
                return INCORRECTLY_FORMATTED.parse(data);

            return new CellTag(dataTokens[0], dataTokens[1]);
        }
    },

    EMPTY("") {
        @Override
        public ScanableTag parse(String data) {
            return new EmptyTag();
        }
    },

    INCORRECTLY_FORMATTED("*") {
        @Override
        public ScanableTag parse(String data) {
            return new IncorrectlyFormattedTag(data);
        }
    };

    public static final String FIELD_DELIMITER = "\t";

    private final String mimeType;

    private TagType(String mimeType) {
        this.mimeType = mimeType;
    }

    public abstract ScanableTag parse(String data);

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
