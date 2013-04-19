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

import java.io.File;
import java.io.IOException;

import org.kdt.model.Scanable;

public interface ScanModel {

    public void add(Scanable scannedTag);

    public void remove(int index);

    public int getNumScannedTags();

    public void clearScannedTags();

    public void setSelectedTag(int index);

    /**
     * Gets the index of the currently selected tag. Returns -1 if no tag is
     * selected.
     * 
     * @return
     */
    public int getSelectedTagIndex();

    public File dumpToCsv() throws IOException;

    public Scanable getTagAt(int position);
}
