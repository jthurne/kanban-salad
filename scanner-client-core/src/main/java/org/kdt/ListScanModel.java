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

import java.util.ArrayList;
import java.util.List;

import org.kdt.model.Scanable;

/**
 * 
 */
public class ListScanModel implements ScanModel {

    private final List<Scanable> scannedTags;

    public ListScanModel() {
        scannedTags = new ArrayList<Scanable>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.ScanModel#add(org.kdt.model.Scanable)
     */
    @Override
    public void add(Scanable scannedTag) {
        scannedTags.add(scannedTag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.ScanModel#remove(org.kdt.model.Scanable)
     */
    @Override
    public void remove(int index) {
        scannedTags.remove(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.ScanModel#clearScannedTags()
     */
    @Override
    public void clearScannedTags() {
        scannedTags.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.ScanModel#getNumScannedTags()
     */
    @Override
    public int getNumScannedTags() {
        return scannedTags.size();
    }

    public List<Scanable> getScannedTags() {
        return scannedTags;
    }
}
