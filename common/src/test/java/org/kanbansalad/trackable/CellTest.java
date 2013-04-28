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
package org.kanbansalad.trackable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.kanbansalad.trackable.Cell;
import org.kanbansalad.trackable.TagType;

/**
 * 
 */
public class CellTest {

    @Test
    public void creates_displayName_based_on_name() throws Exception {
        assertThat(new Cell("Swimlane", "Queue").getDisplayName(),
                is(equalTo("Swimlane - Queue")));
    }

    @Test
    public void creates_data_string() throws Exception {
        assertThat(new Cell("Swimlane", "Queue").getDataString(),
                is(equalTo("Swimlane" + TagType.FIELD_DELIMITER + "Queue")));

    }
}
