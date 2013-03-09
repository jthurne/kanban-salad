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

import org.kdt.model.Scanable;
import org.kdt.model.Task;

/**
 * 
 */
public class ScanPresenter {

    private final ScanView view;
    private final ScanModel model;
    private final Scanner scanner;

    public ScanPresenter(ScanView view, ScanModel model, Scanner scanner) {
        this.view = view;
        this.model = model;
        this.scanner = scanner;
    }

    public void tryToScanTag() {
        Scanable scannedTag = scanner.scan();
        model.add(scannedTag);

        display(scannedTag);
        view.selectScannedTag(model.getNumScannedTags() - 1);
    }

    private void display(Scanable scannedTag) {
        if (scannedTag == null)
            return;

        if (isATask(scannedTag)) {
            view.appendToScannedTags("\t\t" + scannedTag.getPayload());
        } else {
            view.appendToScannedTags(scannedTag.getPayload());
        }
    }

    private boolean isATask(Scanable scannedTag) {
        return scannedTag instanceof Task;
    }

    public void saveClicked() {
        model.clearScannedTags();
        view.clearScannedTags();
    }

    public void helpMenuItemClicked() {
        view.showHelp();
    }

    public void aboutMenuItemClicked() {
        view.showAbout();
    }

    public void tagSelected(int position) {
        view.showScannedTagContextMenu();
    }

    public void deleteTagClicked(int position) {
        model.remove(position);
        view.deleteScannedTag(position);
        view.closeScannedTagContextMenu();
    }

    public void programTagClicked(int position) {

    }
}
