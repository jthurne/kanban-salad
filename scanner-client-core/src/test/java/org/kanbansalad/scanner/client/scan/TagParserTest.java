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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.kanbansalad.scanner.client.tag.TagType.CELL;
import static org.kanbansalad.scanner.client.tag.TagType.TASK;

import org.junit.Before;
import org.junit.Test;
import org.kanbansalad.scanner.client.tag.CellTag;
import org.kanbansalad.scanner.client.tag.EmptyTag;
import org.kanbansalad.scanner.client.tag.IncorrectlyFormattedTag;
import org.kanbansalad.scanner.client.tag.ScanableTag;
import org.kanbansalad.scanner.client.tag.TaskTag;

public class TagParserTest {

    private TagParser parser;
    private ScanableTag parsedObject;

    @Before
    public void given_a_parser() {
        parser = new TagParser();
    }

    @Test
    public void parses_empty_tags__when_no_mime_type_is_provided()
            throws Exception {
        when.parsedObject = parser.parse("", "some unknown data");
        then.parsedObject_should_be_a(EmptyTag.class);
    }

    @Test
    public void parses_empty_tags__when_unkown_mime_type_is_provided()
            throws Exception {
        when.parsedObject = parser.parse("application/unknown",
                "some unknown data");
        then.parsedObject_should_be_a(EmptyTag.class);
    }

    @Test
    public void parses_empty_tags__when_data_is_an_empty_string__when_task_mime_type()
            throws Exception {
        when.parsedObject = parser.parse(TASK.mimeType(), "");
        then.parsedObject_should_be_a(EmptyTag.class);
    }

    @Test
    public void parses_empty_tags__when_data_is_an_empty_string__when_cell_mime_type()
            throws Exception {
        when.parsedObject = parser.parse(CELL.mimeType(), "");
        then.parsedObject_should_be_a(EmptyTag.class);
    }

    @Test
    public void parses_task_tags__when_the_mime_type_is_a_task()
            throws Exception {
        when.parsedObject = parser
                .parse(TASK.mimeType(), "the-id\tthe-name\t3");
        then.parsedObject_should_be_a(TaskTag.class);
    }

    @Test
    public void parses_task_tags__when_the_mime_type_is_a_task__and_the_size_field_is_empty()
            throws Exception {
        when.parsedObject = parser
                .parse(TASK.mimeType(), "the-id\tthe-name\t");
        then.parsedObject_should_be_a(TaskTag.class);
    }

    @Test
    public void parses_task_tags__when_the_mime_type_is_a_task__and_the_name_field_is_empty()
            throws Exception {
        when.parsedObject = parser
                .parse(TASK.mimeType(), "the-id\t\t3");
        then.parsedObject_should_be_a(TaskTag.class);
    }

    @Test
    public void parses_task_tags__when_the_mime_type_is_a_task__and_the_id_field_is_empty()
            throws Exception {
        when.parsedObject = parser
                .parse(TASK.mimeType(), "t\tname\t3");
        then.parsedObject_should_be_a(TaskTag.class);
    }

    @Test
    public void sets_correct_properties_on_a_task() throws Exception {
        when.parsedObject = parser
                .parse(TASK.mimeType(), "the-id\tthe-name\t3");
        then.the_parsedObject_should_have_property("id", "the-id");
        and.the_parsedObject_should_have_property("summary", "the-name");
        and.the_parsedObject_should_have_property("size", "3");
    }

    @Test
    public void parses_cell_tags__when_the_mime_type_is_a_cell()
            throws Exception {
        when.parsedObject = parser.parse(CELL.mimeType(),
                "the-swimlane\tthe-queue");
        then.parsedObject_should_be_a(CellTag.class);
    }

    @Test
    public void parses_cell_tags__when_the_queue_is_empty()
            throws Exception {
        when.parsedObject = parser.parse(CELL.mimeType(),
                "the-swimlane\t");
        then.parsedObject_should_be_a(CellTag.class);
    }

    @Test
    public void parses_cell_tags__when_the_swimlane_is_empty()
            throws Exception {
        when.parsedObject = parser.parse(CELL.mimeType(),
                "\tthe-queue");
        then.parsedObject_should_be_a(CellTag.class);
    }

    @Test
    public void sets_correct_properties_on_a_cell() throws Exception {
        when.parsedObject = parser.parse(CELL.mimeType(),
                "the-swimlane\tthe-queue");
        then.the_parsedObject_should_have_property("swimlane", "the-swimlane");
        and.the_parsedObject_should_have_property("queue", "the-queue");
    }

    @Test
    public void parses_incorrectly_formatted_task_tag() throws Exception {
        when.parsedObject = parser.parse(TASK.mimeType(), "totally-incorrect");
        then.parsedObject_should_be_a(IncorrectlyFormattedTag.class);
    }

    @Test
    public void parses_incorrectly_formatted_cell_tag() throws Exception {
        when.parsedObject = parser.parse(CELL.mimeType(), "totally-incorrect");
        then.parsedObject_should_be_a(IncorrectlyFormattedTag.class);
    }

    private void the_parsedObject_should_have_property(String name,
            String expectedValue) {
        assertThat(parsedObject, hasProperty(name, equalTo(expectedValue)));
    }

    private void parsedObject_should_be_a(
            @SuppressWarnings("rawtypes") Class expectedClass) {
        assertThat(parsedObject.getClass(), is(equalTo(expectedClass)));
    }

    @SuppressWarnings("unused")
    private TagParserTest given = this, when = this, then = this, and = this,
            with = this;
}
