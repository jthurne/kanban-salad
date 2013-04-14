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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kdt.model.Cell;
import org.kdt.model.Empty;
import org.kdt.model.IncorrectlyFormatted;
import org.kdt.model.Scanable;
import org.kdt.model.Task;

public class ListModelTest {
    private static final int NONE = -1;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ListModel model;
    private File csvFile;

    private int lineIndex;

    @Before
    public void given_a_model() throws Exception {
        File tempFile = folder.newFile();
        String filenameTemplate = tempFile.toString() + "-%tF-%<tH%<tM.csv";
        model = new ListModel(filenameTemplate);
    }

    @Test
    public void adds_header_to_csv_file() throws Exception {
        when.model_dumped_to_csv();
        then.line(0).should_be_equal_to(
                "Date\tTime\tSwimlane\tQueue\tID\tName\tSize");
    }

    @Test
    public void uses_date_in_resulting_file_name() throws Exception {
        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.the_generated_file_name_should_contain("2013-04-12-1323");
    }

    @Test
    public void writes_cell_and_tasks_on_one_line() throws Exception {
        given.the_model_contains(
                new Cell("Wings", "Review"),
                new Task("12345", "John can move the flap", "2")
                );
        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.line(1)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t12345\tJohn can move the flap\t2");
    }

    @Test
    public void writes_multiple_tasks_following_a_cell() throws Exception {
        given.the_model_contains(
                new Cell("Wings", "Review"),
                new Task("12345", "John can move the flap", "2"),
                new Task("54321", "Jane can fill the wing with gas", "8")
                );

        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.line(1)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t12345\tJohn can move the flap\t2");
        then.line(2)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t54321\tJane can fill the wing with gas\t8");
    }

    @Test
    public void writes_multiple_cell_and_task_clusters() throws Exception {
        given.the_model_contains(
                new Cell("Wings", "Review"),
                new Task("12345", "John can move the flap", "2"),
                new Task("54321", "Jane can fill the wing with gas", "8"),
                new Cell("Flight Control", "Done"),
                new Task("45433", "Jill can read the speed", "XL")
                );

        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.line(1)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t12345\tJohn can move the flap\t2");
        then.line(2)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t54321\tJane can fill the wing with gas\t8");
        then.line(3)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tFlight Control\tDone\t45433\tJill can read the speed\tXL");
    }

    @Test
    public void skips_empty_cells() throws Exception {
        given.the_model_contains(
                new Cell("Empty", "No Tasks"),
                new Cell("Wings", "Review"),
                new Task("12345", "John can move the flap", "2"),
                new Task("54321", "Jane can fill the wing with gas", "8")
                );

        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.line(1)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t12345\tJohn can move the flap\t2");
        then.line(2)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t54321\tJane can fill the wing with gas\t8");
    }

    @Test
    public void ignores_empty_tags() throws Exception {
        given.the_model_contains(
                new Cell("Wings", "Review"),
                new Task("12345", "John can move the flap", "2"),
                new Empty(),
                new Task("54321", "Jane can fill the wing with gas", "8")
                );

        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.line(1)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t12345\tJohn can move the flap\t2");
        then.line(2)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t54321\tJane can fill the wing with gas\t8");
    }

    @Test
    public void ignores_incorrectly_formatted_tags() throws Exception {
        given.the_model_contains(
                new Cell("Wings", "Review"),
                new Task("12345", "John can move the flap", "2"),
                new IncorrectlyFormatted("bad data"),
                new Task("54321", "Jane can fill the wing with gas", "8")
                );

        when.model_dumped_to_csv_with(date_april_12_2013_at_13_23());
        then.line(1)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t12345\tJohn can move the flap\t2");
        then.line(2)
                .should_be_equal_to(
                        "2013-04-12\t13:23\tWings\tReview\t54321\tJane can fill the wing with gas\t8");
    }

    @Test
    public void resets_the_selected_tag__when_the_model_is_cleared()
            throws Exception {
        given.the_model_contains(
                new Task("12345", "John can move the flap", "2"),
                new Task("54321", "Jane can fill the wing with gas", "8")
                );
        and.the_selected_tag_is(1);
        when.model.clearScannedTags();
        then.the_selected_tag_should_be(NONE);
    }

    private void the_selected_tag_should_be(int expected) {
        assertThat(model.getSelectedTagIndex(), is(equalTo(expected)));
    }

    private void the_selected_tag_is(int selectedTagIndex) {
        model.setSelectedTag(selectedTagIndex);
    }

    private void the_model_contains(Scanable... scanables) {
        for (Scanable scanable : scanables) {
            model.add(scanable);
        }
    }

    private Date date_april_12_2013_at_13_23() {
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 3, 12, 13, 23);
        return cal.getTime();
    }

    private void model_dumped_to_csv() throws IOException {
        csvFile = model.dumpToCsv();
    }

    private void model_dumped_to_csv_with(Date currentDate) throws IOException {
        csvFile = model.dumpToCsv(currentDate);
    }

    private void should_be_equal_to(String expectedContent) throws Exception {
        List<String> lines = FileUtils.readLines(csvFile);
        assertThat(lines.get(lineIndex), is(equalTo(expectedContent)));
    }

    private void the_generated_file_name_should_contain(String dateString) {
        assertThat(csvFile.toString(), containsString(dateString));
    }

    private ListModelTest line(int i) {
        this.lineIndex = i;
        return this;
    }

    @SuppressWarnings("unused")
    private ListModelTest given = this, when = this, then = this,
            and = this, with = this;
}
