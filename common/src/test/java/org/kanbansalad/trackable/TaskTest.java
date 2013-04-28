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
import org.kanbansalad.trackable.TagType;
import org.kanbansalad.trackable.Task;

/**
 * 
 */
public class TaskTest {

    @Test
    public void creates_displayName_based_on_name() throws Exception {
        assertThat(new Task("1234", "Do Something", "2").getDisplayName(),
                is(equalTo("\t\tDo Something")));
    }

    @Test
    public void creates_data_string() throws Exception {
        assertThat(new Task("1234", "Do Something", "2").getDataString(),
                is(equalTo("1234" + TagType.FIELD_DELIMITER + "Do Something"
                        + TagType.FIELD_DELIMITER + "2")));

    }
}
