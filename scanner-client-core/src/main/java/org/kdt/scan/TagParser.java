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
package org.kdt.scan;

import org.kdt.tag.Empty;
import org.kdt.tag.Scanable;
import org.kdt.tag.TagType;

/**
 * Parses Scanables from strings.
 */
public class TagParser {

    /**
     * Parses a Scanable based on the given MIME Type and data.
     * 
     * @param mimeType
     * @param data
     * @return If the MIME Type is not recognized, an Empty is returned. If the
     *         data is incorrectly formatted, and IncorrectlyFormatted is
     *         returned. Otherwise the appropriate Scanable is returned.
     */
    public Scanable parse(String mimeType, String data) {
        if (data.isEmpty()) {
            return new Empty();
        }

        return TagType.forMimeType(mimeType).parse(data);
    }
}
