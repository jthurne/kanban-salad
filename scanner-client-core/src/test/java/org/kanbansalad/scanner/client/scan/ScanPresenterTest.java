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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.kanbansalad.scanner.client.Visible.HIDDEN;
import static org.kanbansalad.scanner.client.Visible.VISIBLE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.kanbansalad.scanner.client.ListModel;
import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.trackable.Cell;
import org.kanbansalad.trackable.Scanable;
import org.kanbansalad.trackable.Task;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ScanPresenterTest {
    private static final int NONE = -1;
    private static final Cell A_CELL_TAG = new Cell("Swimlane", "Queue");

    private ScanPresenter presenter;

    @Mock
    private ScanView mockView;

    @Mock
    private Scanner mockScanner;

    @Mock
    private Sender mockSender;

    private ListModel model;
    private Exception anException;

    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = Mockito.spy(new ListModel(
                "does-not-matter-because-it-is-not-used.csv"));

        doReturn(new File("does-not-matter-because-it-is-not-used.csv")).when(
                model).dumpToCsv();

        presenter = new ScanPresenter(mockView, model, mockScanner, mockSender);
    }

    @Test
    public void adds_a_scanned_tag_to_the_view__when_a_tag_is_scanned()
            throws Exception {
        given.the_scanner_returns(A_CELL_TAG);
        when.presenter.tagTapped();
        then.it_should_add_to_the_view(A_CELL_TAG);
    }

    @Test
    public void adds_scanned_tag_to_the_model() throws Exception {
        given.the_scanner_returns(new Task("1234", "Do Something", "1"));
        when.presenter.tagTapped();
        then.the_model_should_contain(new Task("1234", "Do Something", "1"));
    }

    @Test
    public void selects_tag__when_a_task_tag_is_scanned() throws Exception {
        given.the_scanner_returns(new Task("1234", "Do Something", "1"));
        when.presenter.tagTapped();
        then.it_should_select_tag(0);
    }

    @Test
    public void selects_tag__when_a_cell_tag_is_scanned() throws Exception {
        given.the_scanner_returns(A_CELL_TAG);
        when.presenter.tagTapped();
        then.it_should_select_tag(0);
    }

    @Test
    public void selects_last_scanned_tag__when_more_than_one_tag_is_scanned()
            throws Exception {
        given.the_scanner_returns(new Task("1234", "Do Something", "1"));
        when.presenter.tagTapped();
        when.presenter.tagTapped();
        when.presenter.tagTapped();
        then.it_should_select_tag(0);
        then.it_should_select_tag(1);
        then.it_should_select_tag(2);
    }

    @Test
    public void does_not_select_last_scanned_tag__on_null_scan()
            throws Exception {
        given.the_scanner_returns(null);
        when.presenter.tagTapped();
        then.it_should_not_select_a_tag();
    }

    @Test
    public void does_not_add_tag_to_model__on_null_scan()
            throws Exception {
        given.the_scanner_returns(null);
        when.presenter.tagTapped();
        then.the_model_should_be_empty();
    }

    @Test
    public void does_not_display_tag__on_null_scan()
            throws Exception {
        given.the_scanner_returns(null);
        when.presenter.tagTapped();
        then.the_scan_should_NOT_be_displayed();
    }

    @Test
    public void displays_the_scanned_tag_context_menu__when_a_tag_is_selected()
            throws Exception {
        when.presenter.tagSelected(1);
        then.the_scanned_tag_context_menu_should_be_displayed();
    }

    @Test
    public void informs_the_model_a_tag_was_selected__when_a_tag_is_selected()
            throws Exception {
        when.presenter.tagSelected(1);
        then.it_should_tell_the_model_what_tag_was_selected(1);
    }

    @Test
    public void closes_the_scanned_tag_context_menu__when_the_view_is_hidden__if_the_context_menu_was_showing()
            throws Exception {
        given.the_context_menu_is(VISIBLE);
        when.presenter.visibilityChanged(HIDDEN);
        then.the_scanned_tag_context_menu_should_be_closed();
    }

    @Test
    public void does_not_close_the_scanned_tag_context_menu__when_the_view_is_hidden__if_the_context_menu_was_already_not_showing()
            throws Exception {
        given.the_context_menu_is(HIDDEN);
        when.presenter.visibilityChanged(HIDDEN);
        then.it_should_not_try_to_close_the_context_menu();
    }

    @Test
    public void selects_previously_selected_tag__when_the_view_is_shown()
            throws Exception {
        given.model.setSelectedTag(2);
        when.presenter.visibilityChanged(VISIBLE);
        then.it_should_select_tag(2);
    }

    @Test
    public void does_not_select_previously_selected_tag__when_the_view_is_shown_but_the_model_indicates_no_tag_is_selected()
            throws Exception {
        given.model.setSelectedTag(NONE);
        when.presenter.visibilityChanged(VISIBLE);
        then.it_should_not_select_a_tag();
    }

    @Test
    public void refreshes_the_dispaly_of_scanned_tags__when_the_view_is_shown()
            throws Exception {
        when.presenter.visibilityChanged(VISIBLE);
        then.it_should_refresh_the_display_of_scanned_tags();
    }

    @Test
    public void hides_the_soft_keyboard__when_the_view_is_shown()
            throws Exception {
        when.presenter.visibilityChanged(VISIBLE);
        then.it_should_hide_the_soft_keyboard();
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

    @Test
    public void unselects_tag_in_the_model__when_a_tag_is_unselected_in_the_view__and_the_view_is_visible()
            throws Exception {
        given.model.setSelectedTag(2);
        and.the_view_is(VISIBLE);
        when.presenter.tagUnselected();
        then.it_should_tell_the_model_what_tag_was_selected(NONE);
    }

    @Test
    public void does_not_unselect_tag_in_the_model__when_a_tag_is_unselected_in_the_view__and_the_view_is_NOT_visible()
            throws Exception {
        given.model.setSelectedTag(2);
        and.the_view_is(HIDDEN);
        when.presenter.tagUnselected();
        then.it_should_tell_the_model_what_tag_was_selected(2);
    }

    @Test
    public void sends_csv_file__when_send_button_clicked()
            throws Exception {
        given.the_model_dumps_to_csv_file("test.csv");
        when.presenter.sendClicked();
        then.it_should_send_the_csv_file("test.csv");
    }

    @Test
    public void clears_the_view__when_send_button_clicked() throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.sendClicked();
        then.the_display_of_scanned_tags_should_be_cleared();
    }

    @Test
    public void clears_the_model__when_send_button_clicked() throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.sendClicked();
        then.the_model_should_be_cleared();
    }

    @Test
    public void closes_the_context_menu__when_send_button_clicked()
            throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.sendClicked();
        then.the_scanned_tag_context_menu_should_be_closed();
    }

    @Test
    public void displays_error_message__if_dumping_to_csv_fails()
            throws Exception {
        given.dumping_to_csv_throws_an_exception();
        when.presenter.sendClicked();
        then.it_should_show_the_exception_to_the_user();
    }

    @Test
    public void does_not_clear_the_view__when_exception_generated_when_dumping_to_csv()
            throws Exception {
        given.some_tags_have_been_scanned();
        and.dumping_to_csv_throws_an_exception();
        when.presenter.sendClicked();
        then.the_display_of_scanned_tags_should_NOT_be_cleared();
    }

    @Test
    public void does_not_clear_the_model__when_exception_generated_when_dumping_to_csv()
            throws Exception {
        given.some_tags_have_been_scanned();
        and.dumping_to_csv_throws_an_exception();
        when.presenter.sendClicked();
        then.the_model_should_NOT_be_cleared();
    }

    @Test
    public void clears_the_view__when_clear_menu_item_clicked()
            throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.clearClicked();
        then.the_display_of_scanned_tags_should_be_cleared();
    }

    @Test
    public void clears_the_model__when_clear_menu_item_clicked()
            throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.clearClicked();
        then.the_model_should_be_cleared();
    }

    @Test
    public void closes_the_context_menu__when_clear_menu_item_clicked()
            throws Exception {
        given.some_tags_have_been_scanned();
        when.presenter.clearClicked();
        then.the_scanned_tag_context_menu_should_be_closed();
    }

    private void it_should_show_the_exception_to_the_user() {
        verify(mockView).showException(anException);
    }

    private void dumping_to_csv_throws_an_exception() throws Exception {
        anException = new IOException("I can't do that Dave");
        doThrow(anException).when(model).dumpToCsv();
    }

    private void the_model_dumps_to_csv_file(String filename) throws Exception {
        doReturn(new File(filename)).when(model).dumpToCsv();
    }

    private void it_should_send_the_csv_file(String filename) {
        verify(mockSender).send(new File(filename));
    }

    private void the_view_is(Visible visible) {
        when(mockView.getVisisble()).thenReturn(visible);
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
        when.presenter.tagTapped();
        reset(mockView);
    }

    private void the_display_of_scanned_tags_should_be_cleared() {
        verify(mockView).clearTags();
    }

    private void the_display_of_scanned_tags_should_NOT_be_cleared() {
        verify(mockView, times(0)).clearTags();
    }

    private void it_should_add_to_the_view(Scanable toDisplay) {
        verify(mockView).appendToTags(toDisplay);
    }

    private void the_scan_should_NOT_be_displayed() {
        verify(mockView, times(0)).appendToTags(
                Mockito.any(Scanable.class));
    }

    private void the_corresponding_tag_should_be_removed_from_the_view(
            int scannedItemIndex) {
        verify(mockView).deleteTag(scannedItemIndex);
    }

    private void it_should_select_tag(int position) {
        verify(mockView).selectTag(position);
    }

    private void it_should_not_select_a_tag() {
        verify(mockView, times(0)).selectTag(Mockito.anyInt());
    }

    private void the_scanned_tag_context_menu_should_be_displayed() {
        verify(mockView).showTagContextMenu();
    }

    private void the_scanned_tag_context_menu_should_be_closed() {
        verify(mockView).closeTagContextMenu();
    }

    private void the_model_should_contain(Scanable expectedScanable) {
        assertThat(model.getScannedTags(),
                contains(samePropertyValuesAs(expectedScanable)));
    }

    private void the_model_should_be_cleared() {
        assertThat(model.getScannedTags(), is(empty()));
    }

    private void the_model_should_be_empty() {
        assertThat(model.getScannedTags(), is(empty()));
    }

    private void the_model_should_NOT_be_cleared() {
        assertThat(model.getScannedTags(), is(not(empty())));
    }

    private void the_model_should_not_contain(Scanable shouldNotExist) {
        for (Scanable scanable : model.getScannedTags()) {
            assertThat(scanable, not(samePropertyValuesAs(shouldNotExist)));
        }
    }

    private void it_should_tell_the_model_what_tag_was_selected(
            int expectedIndex) {
        assertThat(model.getSelectedTagIndex(), is(equalTo(expectedIndex)));
    }

    private void the_context_menu_is(Visible visible) {
        when(mockView.getContextMenuVisible()).thenReturn(visible);
    }

    private void it_should_not_try_to_close_the_context_menu() {
        verify(mockView, times(0)).closeTagContextMenu();
    }

    private void it_should_refresh_the_display_of_scanned_tags() {
        verify(mockView).refreshTags();
    }

    private void it_should_hide_the_soft_keyboard() {
        verify(mockView).hideSoftKeyboard();
    }

    @SuppressWarnings("unused")
    private ScanPresenterTest given = this, when = this, then = this,
            and = this, with = this;

}
