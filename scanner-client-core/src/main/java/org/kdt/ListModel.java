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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kdt.model.Cell;
import org.kdt.model.Scanable;
import org.kdt.program.ProgramModel;
import org.kdt.scan.ScanModel;

public class ListModel implements ScanModel, ProgramModel {
    private static final int NONE = -1;

    private final String filenameTemplate;
    private final List<Scanable> scannedTags;
    private int selectedTagIndex = -1;

    public ListModel(String filenameTemplate) {
        scannedTags = new ArrayList<Scanable>();
        this.filenameTemplate = filenameTemplate;
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
    public void clear() {
        scannedTags.clear();
        selectedTagIndex = NONE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kdt.ScanModel#getNumScannedTags()
     */
    @Override
    public int getCount() {
        return scannedTags.size();
    }

    @Override
    public Scanable get(int position) {
        return scannedTags.get(position);
    }

    public List<Scanable> getScannedTags() {
        return scannedTags;
    }

    @Override
    public void setSelectedTag(int index) {
        this.selectedTagIndex = index;
    }

    @Override
    public int getSelectedTagIndex() {
        return selectedTagIndex;
    }

    @Override
    public File dumpToCsv() throws IOException {
        return dumpToCsv(new Date());
    }

    public File dumpToCsv(Date snapshotDate) throws IOException {
        File csvFile = generateFileFrom(snapshotDate);

        PrintWriter writer = new PrintWriter(csvFile);
        try {
            writeHeader(writer);
            writeBody(snapshotDate, writer);
        } finally {
            writer.close();
        }

        return csvFile;
    }

    private File generateFileFrom(Date snapshotDate) {
        File csvFile = new File(String.format(filenameTemplate, snapshotDate));
        return csvFile;
    }

    private void writeHeader(PrintWriter writer) {
        writer.println("Date\tTime\tSwimlane\tQueue\tID\tName\tSize");
    }

    private void writeBody(Date snapshotDate, PrintWriter writer) {
        Scanable cell = null;
        for (Scanable tag : scannedTags) {
            if (tag instanceof Cell) {
                cell = tag;
            } else if (tag.isValid()) {
                writeRecord(snapshotDate, cell, tag, writer);
            }
        }
    }

    private void writeRecord(Date snapshotDate, Scanable cell, Scanable task,
            PrintWriter writer) {
        writer.printf("%tF\t%<tR\t%s\t%s%n",
                snapshotDate,
                cell.getDataString(),
                task.getDataString());
    }
}
