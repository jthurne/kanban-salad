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

import static org.kanbansalad.scanner.client.CommonConstants.NONE;

import java.util.LinkedList;
import java.util.List;

public class DataBuilder {

    private List<String> values = new LinkedList<String>();
    private int canTruncateIndex = NONE;

    public DataBuilder add(String value) {
        values.add(value);
        return this;
    }

    public DataBuilder canTruncate() {
        canTruncateIndex = values.size() - 1;
        return this;
    }

    public String toDataString(int maxSize) {
        int size = calcSize();

        if (shouldTruncate(size, maxSize)) {
            truncateValues(size, maxSize);
        }

        return generateDataString();
    }

    /**
     * @param size
     * @param maxSize
     */
    private void truncateValues(int size, int maxSize) {
        String value = values.get(canTruncateIndex);
        int numToRemove = size - maxSize;
        int newLen = value.length() - numToRemove;
        String truncated = value.substring(0, newLen);
        values.set(canTruncateIndex, truncated);
    }

    private boolean shouldTruncate(int size, int maxSize) {
        return size > maxSize && canTruncateIndex != NONE;
    }

    private int calcSize() {
        return generateDataString().length();
    }

    private String generateDataString() {
        String data = "";
        for (String value : values) {
            data += value;
            data += TagType.FIELD_DELIMITER;
        }
        return data.substring(0, data.length() - 1);
    }
}
