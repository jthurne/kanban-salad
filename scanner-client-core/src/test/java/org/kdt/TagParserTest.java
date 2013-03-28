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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.kdt.model.Cell;
import org.kdt.model.Empty;
import org.kdt.model.IncorrectlyFormatted;
import org.kdt.model.MimeTypes;
import org.kdt.model.Scanable;
import org.kdt.model.Task;

/**
 * 
 */
public class TagParserTest {

    private TagParser parser;
    private Scanable parsedObject;

    @Before
    public void given_a_parser() {
        parser = new TagParser();
    }

    @Test
    public void parses_empty_tags__when_no_mime_type_is_provided()
            throws Exception {
        when.parsedObject = parser.parse("", "some unknown data");
        then.parsedObject_should_be_a(Empty.class);
    }

    @Test
    public void parses_empty_tags__when_unkown_mime_type_is_provided()
            throws Exception {
        when.parsedObject = parser.parse("application/unknown",
                "some unknown data");
        then.parsedObject_should_be_a(Empty.class);
    }

    @Test
    public void parses_task_tags__when_the_mime_type_is_a_task()
            throws Exception {
        when.parsedObject = parser.parse(MimeTypes.TASK, "the-id:the-name:3");
        then.parsedObject_should_be_a(Task.class);
    }

    @Test
    public void sets_correct_properties_on_a_task() throws Exception {
        when.parsedObject = parser.parse(MimeTypes.TASK, "the-id:the-name:3");
        then.the_parsedObject_should_have_property("id", "the-id");
        and.the_parsedObject_should_have_property("name", "the-name");
        and.the_parsedObject_should_have_property("size", "3");
    }

    @Test
    public void parses_cell_tags__when_the_mime_type_is_a_cell()
            throws Exception {
        when.parsedObject = parser.parse(MimeTypes.CELL,
                "the-swimlane:the-queue");
        then.parsedObject_should_be_a(Cell.class);
    }

    @Test
    public void sets_correct_properties_on_a_cell() throws Exception {
        when.parsedObject = parser.parse(MimeTypes.CELL,
                "the-swimlane:the-queue");
        then.the_parsedObject_should_have_property("swimlane", "the-swimlane");
        and.the_parsedObject_should_have_property("queue", "the-queue");
    }

    @Test
    public void parses_incorrectly_formatted_task_tag() throws Exception {
        when.parsedObject = parser.parse(MimeTypes.TASK, "totally-incorrect");
        then.parsedObject_should_be_a(IncorrectlyFormatted.class);
    }

    @Test
    public void parses_incorrectly_formatted_cell_tag() throws Exception {
        when.parsedObject = parser.parse(MimeTypes.CELL, "totally-incorrect");
        then.parsedObject_should_be_a(IncorrectlyFormatted.class);
    }

    private void the_parsedObject_should_have_property(String name,
            String expectedValue) {
        assertThat(parsedObject, hasProperty(name, equalTo(expectedValue)));
    }

    private void parsedObject_should_be_a(
            Class<? extends Scanable> expectedClass) {
        assertTrue(parsedObject.getClass().equals(expectedClass));
    }

    @SuppressWarnings("unused")
    private TagParserTest given = this, when = this, then = this, and = this,
            with = this;
}
