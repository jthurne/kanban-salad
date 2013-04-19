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

import static org.kdt.Visible.VISIBLE;

import java.io.File;
import java.io.IOException;

import org.kdt.Visible;
import org.kdt.model.Scanable;

public class ScanPresenter {

    private static final int NONE = -1;
    private final ScanView view;
    private final ScanModel model;
    private final Scanner scanner;
    private final Sender sender;

    public ScanPresenter(ScanView view, ScanModel model, Scanner scanner,
            Sender sender) {
        this.view = view;
        this.model = model;
        this.scanner = scanner;
        this.sender = sender;
    }

    public void tagScanned() {
        Scanable scannedTag = scanner.scan();
        if (scannedTag == null)
            return;

        model.add(scannedTag);

        display(scannedTag);
        view.selectScannedTag(model.getNumScannedTags() - 1);
    }

    private void display(Scanable scannedTag) {
        view.appendToScannedTags(scannedTag);
    }

    public void sendClicked() {
        try {
            File csvFile = model.dumpToCsv();
            sender.send(csvFile);
            clearClicked();
        } catch (IOException e) {
            view.showException(e);
        }
    }

    public void clearClicked() {
        view.closeScannedTagContextMenu();
        model.clearScannedTags();
        view.clearScannedTags();
    }

    public void deleteTagClicked(int position) {
        model.remove(position);
        view.deleteScannedTag(position);
        view.closeScannedTagContextMenu();

        if (position > 0)
            view.selectScannedTag(position - 1);
    }

    public void tagSelected(int position) {
        model.setSelectedTag(position);
        view.showScannedTagContextMenu();
    }

    public void visibilityChanged(Visible visible) {
        if (visible == VISIBLE) {
            restoreViewOfSelectedTag();
        } else {
            closeContextMenuIfVisible();
        }
    }

    private void closeContextMenuIfVisible() {
        if (view.getContextMenuVisible() == VISIBLE)
            view.closeScannedTagContextMenu();
    }

    private void restoreViewOfSelectedTag() {
        if (model.getSelectedTagIndex() > NONE) {
            view.selectScannedTag(model.getSelectedTagIndex());
        }
    }

    public void tagUnselected() {
        if (view.getVisisble() == VISIBLE)
            model.setSelectedTag(NONE);
    }
}
