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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.kdt.model.Cell;
import org.kdt.model.Scanable;
import org.kdt.model.Task;
import org.kdt.scan.ScanPresenter;
import org.kdt.scan.ScanView;
import org.kdt.scan.Scanner;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScanPresenterTest {
    private static final Cell A_CELL_TAG = new Cell("Swimlane", "Queue");

    private ScanPresenter presenter;

    @Mock
    private ScanView mockView;
    @Mock
    private Scanner mockScanner;

    private ListScanModel model;

    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = new ListScanModel();
        presenter = new ScanPresenter(mockView, model, mockScanner);
    }

    @Test
    public void displays_name_of_a_scanned_tag__when_a_tag_is_scanned()
            throws Exception {
        given.the_scanner_returns(A_CELL_TAG);
        when.presenter.tagScanned();
        then.the_scan_should_be_displayed_as(A_CELL_TAG.getDisplayName());
    }

    @Test
    public void adds_scanned_tag_to_the_model() throws Exception {
        given.the_scanner_returns(new Task("1234", "Do Something", "1"));
        when.presenter.tagScanned();
        then.the_model_should_contain(new Task("1234", "Do Something", "1"));
    }

    @Test
    public void selects_tag__when_a_task_tag_is_scanned() throws Exception {
        given.the_scanner_returns(new Task("1234", "Do Something", "1"));
        when.presenter.tagScanned();
        then.it_should_select_tag(0);
    }

    @Test
    public void selects_tag__when_a_cell_tag_is_scanned() throws Exception {
        given.the_scanner_returns(A_CELL_TAG);
        when.presenter.tagScanned();
        then.it_should_select_tag(0);
    }

    @Test
    public void selects_last_scanned_tag__when_more_than_one_tag_is_scanned()
            throws Exception {
        given.the_scanner_returns(new Task("1234", "Do Something", "1"));
        when.presenter.tagScanned();
        when.presenter.tagScanned();
        when.presenter.tagScanned();
        then.it_should_select_tag(0);
        then.it_should_select_tag(1);
        then.it_should_select_tag(2);
    }

    @Test
    public void ignores_null_scans() throws Exception {
        given.the_scanner_returns(null);
        when.presenter.tagScanned();
    }

    @Test
    public void displays_the_scanned_tag_context_menu__when_a_tag_is_selected()
            throws Exception {
        when.presenter.tagSelected(1);
        then.the_scanned_tag_context_menu_should_be_displayed();
    }

    @Test
    public void clears_the_view_when_the_snapshot_is_saved() throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.saveClicked();
        then.the_display_of_scanned_tags_should_be_cleared();
    }

    @Test
    public void clears_the_model_when_the_snapshot_is_saved() throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.saveClicked();
        then.the_model_should_be_cleared();
    }

    @Test
    public void closes_the_context_menu__when_item_is_selected_and_delete_is_pressed()
            throws Exception {
        given.two_tags_have_been_scanned();
        when.presenter.deleteTagClicked(1);
        then.the_scanned_tag_context_menu_should_be_closed();
    }

    @Test
    public void deletes_scanned_tag_from_the_view__when_item_is_selected_and_delete_is_pressed()
            throws Exception {
        given.two_tags_have_been_scanned();
        when.presenter.deleteTagClicked(1);
        then.the_corresponding_tag_should_be_removed_from_the_view(1);
    }

    @Test
    public void deletes_scanned_tag_from_the_model__when_item_is_selected_and_delete_is_pressed()
            throws Exception {
        given.scanned_tag(new Task("1234", "Do Something", "1"));
        given.scanned_tag(new Task("12345", "Do Something Else", "1"));
        given.scanned_tag(A_CELL_TAG);
        when.presenter.deleteTagClicked(1);
        then.the_model_should_not_contain(new Task("12345",
                "Do Something Else", "1"));
    }

    @Test
    public void selects_previous_tag__when_a_task_tag_is_deleted()
            throws Exception {
        given.scanned_tag(new Task("1234", "Do Something", "1"));
        given.scanned_tag(new Task("12345", "Do Something Else", "1"));
        given.scanned_tag(A_CELL_TAG);

        when.presenter.deleteTagClicked(2);
        then.it_should_select_tag(1);
    }

    @Test
    public void selects_no_tags__when_a_tag_is_deleted__and_there_are_no_remaining_tags()
            throws Exception {
        given.scanned_tag(A_CELL_TAG);
        when.presenter.deleteTagClicked(0);
    }

    private void the_scanner_returns(Scanable scanable) {
        when(mockScanner.scan()).thenReturn(scanable);
    }

    private void some_tags_have_been_scanned() {
        two_tags_have_been_scanned();
    }

    private void two_tags_have_been_scanned() {
        scanned_tag(new Task("1234", "Do Something", "1"));
        scanned_tag(A_CELL_TAG);
    }

    private void scanned_tag(Scanable scanable) {
        given.the_scanner_returns(scanable);
        when.presenter.tagScanned();
        reset(mockView);
    }

    private void the_display_of_scanned_tags_should_be_cleared() {
        verify(mockView).clearScannedTags();
    }

    private void the_scan_should_be_displayed_as(String textToDisplay) {
        verify(mockView).appendToScannedTags(textToDisplay);
    }

    private void the_corresponding_tag_should_be_removed_from_the_view(
            int scannedItemIndex) {
        verify(mockView).deleteScannedTag(scannedItemIndex);
    }

    private void it_should_select_tag(int position) {
        verify(mockView).selectScannedTag(position);
    }

    private void the_scanned_tag_context_menu_should_be_displayed() {
        verify(mockView).showScannedTagContextMenu();
    }

    private void the_scanned_tag_context_menu_should_be_closed() {
        verify(mockView).closeScannedTagContextMenu();
    }

    private void the_model_should_contain(Scanable expectedScanable) {
        assertThat(model.getScannedTags(),
                contains(samePropertyValuesAs(expectedScanable)));
    }

    private void the_model_should_be_cleared() {
        assertThat(model.getScannedTags(), Matchers.is(Matchers.empty()));
    }

    private void the_model_should_not_contain(Scanable shouldNotExist) {
        for (Scanable scanable : model.getScannedTags()) {
            assertThat(scanable, not(samePropertyValuesAs(shouldNotExist)));
        }
    }

    @SuppressWarnings("unused")
    private ScanPresenterTest given = this, when = this, then = this,
            and = this, with = this;

}