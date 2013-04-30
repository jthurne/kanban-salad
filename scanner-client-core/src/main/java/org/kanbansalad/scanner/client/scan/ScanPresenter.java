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
package org.kanbansalad.scanner.client.scan;

import static org.kanbansalad.scanner.client.CommonConstants.NONE;
import static org.kanbansalad.scanner.client.Visible.VISIBLE;

import java.io.File;
import java.io.IOException;

import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.scanner.client.tag.ScanableTag;

public class ScanPresenter {
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

    public void tagTapped() {
        ScanableTag scannedTag = scanner.scan();
        if (scannedTag == null)
            return;

        model.add(scannedTag);

        display(scannedTag);
        view.selectTag(model.getCount() - 1);
    }

    private void display(ScanableTag scannedTag) {
        view.appendToTags(scannedTag);
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
        view.closeTagContextMenu();
        model.clear();
        view.clearTags();
    }

    public void deleteTagClicked(int position) {
        model.remove(position);
        view.deleteTag(position);
        view.closeTagContextMenu();

        if (position > 0)
            view.selectTag(position - 1);
    }

    public void tagSelected(int position) {
        model.setSelectedTag(position);
        view.showTagContextMenu();
    }

    public void visibilityChanged(Visible visible) {
        if (visible == VISIBLE) {
            restoreViewOfSelectedTag();
            view.refreshTags();
            view.hideSoftKeyboard();
        } else {
            closeContextMenuIfVisible();
        }
    }

    private void closeContextMenuIfVisible() {
        if (view.getContextMenuVisible() == VISIBLE)
            view.closeTagContextMenu();
    }

    private void restoreViewOfSelectedTag() {
        if (model.getSelectedTagIndex() != NONE) {
            view.selectTag(model.getSelectedTagIndex());
        }
    }

    public void tagUnselected() {
        if (view.getVisisble() == VISIBLE)
            model.setSelectedTag(NONE);
    }
}
