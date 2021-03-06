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
package org.kanbansalad.scanner.client.tag;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * 
 */
public class CellTagTest {

    @Test
    public void creates_displayName_based_on_name() throws Exception {
        assertThat(new CellTag("Swimlane", "Queue").getDisplayName(),
                is(equalTo("Swimlane - Queue")));
    }

    @Test
    public void creates_data_string() throws Exception {
        assertThat(
                new CellTag("Swimlane", "Queue").toDataString(Integer.MAX_VALUE),
                is(equalTo("Swimlane" + TagType.FIELD_DELIMITER + "Queue")));

    }

    @Test
    public void truncates_swimline_if_data_string_too_long() throws Exception {
        assertThat(
                new CellTag("Swimlane", "Queue").toDataString(10),
                is(equalTo("Swim" + TagType.FIELD_DELIMITER + "Queue")));

    }
}
