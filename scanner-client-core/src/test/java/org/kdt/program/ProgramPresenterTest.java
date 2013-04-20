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
package org.kdt.program;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.kdt.Visible.VISIBLE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.kdt.program.Programer.ThereWas;
import org.kdt.tag.Cell;
import org.kdt.tag.Empty;
import org.kdt.tag.Programable;
import org.kdt.tag.Scanable;
import org.kdt.tag.TagType;
import org.kdt.tag.Task;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ProgramPresenterTest {

    private ProgramPresenter presenter;

    @Mock
    private ProgramView mockView;

    @Mock
    private Programer mockTagProgramer;

    @Mock
    private ProgramModel mockModel;

    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ProgramPresenter(mockView, mockModel, mockTagProgramer);
    }

    @Test
    public void shows_cell_fields__when_tag_type_is_set_to_cell()
            throws Exception {
        when.presenter.typeChangedTo(TagType.CELL);
        then.verify(mockView).setIsTaskDetailsVisible(false);
        then.verify(mockView).setIsCellDetailsVisible(true);
    }

    @Test
    public void shows_task_fields__when_tag_type_is_set_to_task()
            throws Exception {
        when.presenter.typeChangedTo(TagType.TASK);
        then.verify(mockView).setIsTaskDetailsVisible(true);
        then.verify(mockView).setIsCellDetailsVisible(false);
    }

    @Test
    public void shows_task_fields__when_tag_type_is_anything_else()
            throws Exception {
        when.presenter.typeChangedTo(TagType.EMPTY);
        then.verify(mockView).setIsTaskDetailsVisible(true);
        then.verify(mockView).setIsCellDetailsVisible(false);
    }

    @Test
    public void programs_a_cell_tag__when_tag_is_tapped() throws Exception {
        given.the_selected_tag_type_is(TagType.CELL);
        and.the_swimline_is_set_to("Android App");
        and.the_queue_is_set_to("Code Review");
        when.presenter.tagTapped();
        then.the_tag_is_programmed_using(new Cell("Android App", "Code Review"));
    }

    @Test
    public void programs_a_task_tag__when_tag_is_tapped() throws Exception {
        given.the_selected_tag_type_is(TagType.TASK);
        and.the_id_is_set_to("1234");
        and.the_name_is_set_to("User can program a tag");
        and.the_size_is_set_to("XL");
        when.presenter.tagTapped();
        then.the_tag_is_programmed_using(new Task("1234",
                "User can program a tag", "XL"));
    }

    @Test
    public void tells_the_user_when_a_cell_tag_is_successfully_programmed()
            throws Exception {
        given.the_selected_tag_type_is(TagType.TASK);
        and.the_programer_returns(ThereWas.A_TAG_TO_PROGRAM);
        when.presenter.tagTapped();
        then.verify(mockView).showMessage(ProgramView.Message.TAG_PROGRAMMED);
    }

    @Test
    public void tells_the_user_when_programing_a_tag_failed() {
        given.the_selected_tag_type_is(TagType.TASK);
        and.given.the_programer_throws(an_exception());
        when.presenter.tagTapped();
        then.verify(mockView).showException(the_exception());

    }

    @Test
    public void does_tells_the_user_when_a_tag_is_programmed__if_there_was_no_tag_to_program()
            throws Exception {
        given.the_programer_returns(ThereWas.NO_TAG_TO_PROGRAM);
        when.presenter.tagTapped();
        then.the_programed_tag_message_should_not_be_shown();
    }

    @Test
    public void updates_view_with_task_details__if_task_selected_when_the_view_is_made_visible()
            throws Exception {
        given.the_model_has_a_tag(1, new Task("1234", "User can program a tag",
                "XL"));
        and.the_selected_tag_is(1);
        when.presenter.visibilityChanged(VISIBLE);
        then.verify(mockView).setSelectedTagType(TagType.TASK);
        then.verify(mockView).setTaskId("1234");
        then.verify(mockView).setTaskName("User can program a tag");
        then.verify(mockView).setTaskSize("XL");
    }

    @Test
    public void updates_view_with_cell_details__if_cell_selected_when_the_view_is_made_visible()
            throws Exception {
        given.the_model_has_a_tag(1, new Cell("Android App", "Code Review"));
        and.the_selected_tag_is(1);
        when.presenter.visibilityChanged(VISIBLE);
        then.verify(mockView).setSelectedTagType(TagType.CELL);
        then.verify(mockView).setSwimlane("Android App");
        then.verify(mockView).setQueue("Code Review");
    }

    @Test
    public void clears_view__if_an_invalid_tag_selected_when_the_view_is_made_visible()
            throws Exception {
        given.the_model_has_a_tag(1, new Empty());
        and.the_selected_tag_is(1);
        when.presenter.visibilityChanged(VISIBLE);
        then.the_view_should_be_cleared_and_task_type_should_be_selected();
    }

    @Test
    public void clears_view__if_no_tag_selected_when_the_view_is_made_visible()
            throws Exception {
        given.the_selected_tag_is(-1);
        when.presenter.visibilityChanged(VISIBLE);
        then.the_view_should_be_cleared_and_task_type_should_be_selected();
    }

    private void the_model_has_a_tag(int position, Scanable tag) {
        when(mockModel.get(position)).thenReturn(tag);
    }

    private void the_selected_tag_is(int position) {
        when(mockModel.getSelectedTagIndex()).thenReturn(position);
        if (position < 0) {
            when(mockModel.get(position)).thenThrow(
                    new IndexOutOfBoundsException());
        }
    }

    private void the_programer_throws(ProgramingException an_exception) {
        doThrow(an_exception).when(mockTagProgramer).programTag(
                Mockito.<Programable> any());
    }

    private ProgramingException an_exception() {
        testException = new ProgramingException("Something bad");
        return testException;
    }

    private ProgramingException the_exception() {
        return testException;
    }

    private void the_tag_is_programmed_using(Cell cell) {
        // TODO Pull into a commonly used matcher??
        Mockito.verify(mockTagProgramer)
                .programTag(
                        Mockito.argThat(both(
                                Matchers.<Cell> hasProperty("swimlane",
                                        equalTo(cell.getSwimlane()))).and(
                                Matchers.<Cell> hasProperty("queue",
                                        equalTo(cell.getQueue())))));
    }

    private void the_tag_is_programmed_using(Task task) {
        // TODO Pull into a commonly used matcher??
        Mockito.verify(mockTagProgramer)
                .programTag(
                        Mockito.argThat(allOf(
                                Matchers.<Task> hasProperty("id",
                                        equalTo(task.getId())),
                                Matchers.<Task> hasProperty("name",
                                        equalTo(task.getName())),
                                Matchers.<Task> hasProperty("size",
                                        equalTo(task.getSize()))
                                )));
    }

    private void the_selected_tag_type_is(TagType type) {
        Mockito.when(mockView.getSelectedTagType()).thenReturn(type);
    }

    private void the_swimline_is_set_to(String swimlane) {
        Mockito.when(mockView.getSwimlane()).thenReturn(swimlane);
    }

    private void the_queue_is_set_to(String queue) {
        Mockito.when(mockView.getQueue()).thenReturn(queue);
    }

    private void the_id_is_set_to(String id) {
        Mockito.when(mockView.getTaskId()).thenReturn(id);
    }

    private void the_name_is_set_to(String name) {
        Mockito.when(mockView.getTaskName()).thenReturn(name);
    }

    private void the_size_is_set_to(String size) {
        Mockito.when(mockView.getTaskSize()).thenReturn(size);
    }

    private <T> T verify(T mock) {
        return Mockito.verify(mock);
    }

    private void the_programed_tag_message_should_not_be_shown() {
        Mockito.verify(mockView, times(0)).showMessage(
                ProgramView.Message.TAG_PROGRAMMED);
    }

    private void the_programer_returns(ThereWas whatThereWas) {
        when(mockTagProgramer.programTag(Mockito.any(Programable.class)))
                .thenReturn(whatThereWas);
    }

    private void the_view_should_be_cleared_and_task_type_should_be_selected() {
        verify(mockView).setSelectedTagType(TagType.TASK);
        verify(mockView).setTaskId("");
        verify(mockView).setTaskName("");
        verify(mockView).setTaskSize("");
    }

    @SuppressWarnings("unused")
    private ProgramPresenterTest given = this, when = this, then = this,
            and = this, with = this;

    private ProgramingException testException;
}
