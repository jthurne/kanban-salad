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

import org.junit.Before;
import org.junit.Test;
import org.kdt.model.TagType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ProgramPresenterTest {

    private ProgramPresenter presenter;

    @Mock
    private ProgramView mockView;

    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ProgramPresenter(mockView);
    }

    @Test
    public void shows_cell_fields__when_tag_type_is_set_to_task()
            throws Exception {
        when.presenter.typeChangedTo(TagType.CELL);
        then.verify().setIsTaskDetailsVisible(false);
        then.verify().setIsCellDetailsVisible(true);
    }

    @Test
    public void shows_task_fields__when_tag_type_is_set_to_task()
            throws Exception {
        when.presenter.typeChangedTo(TagType.TASK);
        then.verify().setIsTaskDetailsVisible(true);
        then.verify().setIsCellDetailsVisible(false);
    }

    @Test
    public void shows_task_fields__when_tag_type_is_anything_else()
            throws Exception {
        when.presenter.typeChangedTo(TagType.EMPTY);
        then.verify().setIsTaskDetailsVisible(true);
        then.verify().setIsCellDetailsVisible(false);
    }

    private ProgramView verify() {
        return Mockito.verify(mockView);
    }

    @SuppressWarnings("unused")
    private ProgramPresenterTest given = this, when = this, then = this,
            and = this, with = this;
}
