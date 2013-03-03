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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.kdt.model.Cell;
import org.kdt.model.Scanable;
import org.kdt.model.Task;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScanPresenterTest {

    private ScanPresenter presenter;

    @Mock
    private ScanView mockView;
    @Mock
    private Scanner mockScanner;

    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ScanPresenter(mockView, mockScanner);
    }

    @Test
    public void displays_name_of_a_scanned_cell__when_a_cell_tag_is_scanned()
            throws Exception {
        given.the_scanner_returns(new Cell("Swimlane - Queue"));
        when.presenter.tryToScanTag();
        then.the_scan_should_be_displayed_as("Swimlane - Queue");
    }

    @Test
    public void displays_name_of_a_scanned_task__when_a_task_tag_is_scanned()
            throws Exception {
        given.the_scanner_returns(new Task("Do Something"));
        when.presenter.tryToScanTag();
        then.the_scan_should_be_displayed_as("\t\tDo Something");
    }

    @Test
    public void ignores_null_scans() throws Exception {
        given.the_scanner_returns(null);
        when.presenter.tryToScanTag();
    }

    @Test
    public void clears_the_log_when_the_snapshot_is_saved() throws Exception {
        when.presenter.saveClicked();
        then.the_display_of_scanned_tags_should_be_cleared();
    }

    @Test
    public void displays_help__when_help_menu_selected() throws Exception {
        when.presenter.helpMenuItemClicked();
        then.the_help_should_be_displayed();
    }

    @Test
    public void displays_about__when_about_menu_selected() throws Exception {
        when.presenter.aboutMenuItemClicked();
        then.about_should_be_displayed();
    }

    @Test
    public void deletes_item_from_scan_log__when_item_is_selected_and_delete_is_pressed()
            throws Exception {
        when.presenter.deleteTagClicked(2);
        then.the_corresponding_tag_should_be_removed_from_the_view(2);
    }

    private void the_scanner_returns(Scanable scanable) {
        when(mockScanner.scan()).thenReturn(scanable);
    }

    private void the_display_of_scanned_tags_should_be_cleared() {
        verify(mockView).clearScannedTags();
    }

    private void the_scan_should_be_displayed_as(String textToDisplay) {
        verify(mockView).appendToScannedTags(textToDisplay);
    }

    private void the_help_should_be_displayed() {
        verify(mockView).showHelp();
    }

    private void about_should_be_displayed() {
        verify(mockView).showAbout();
    }

    private void the_corresponding_tag_should_be_removed_from_the_view(
            int scannedItemIndex) {
        verify(mockView).deleteScannedTag(scannedItemIndex);
    }

    @SuppressWarnings("unused")
    private ScanPresenterTest given = this, when = this, then = this,
            and = this, with = this;

}
