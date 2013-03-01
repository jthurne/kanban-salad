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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.kdt.model.Task;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
public class CapturePresenterTest {
    
    private CapturePresenter presenter;
    
    @Mock
    private CaptureView mockView;
    
    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new CapturePresenter(mockView);
    }

    @Test
    public void displays_name_of_a_scanned_task() throws Exception {
        when.presenter.scanned(new Task("Do Something"));
        then.the_task_should_be_displayed_as("Do Something");
    }
    
    private void the_task_should_be_displayed_as(String textToDisplay) {
        Mockito.verify(mockView).appendToLog(textToDisplay);
    }

    @SuppressWarnings("unused")
    private CapturePresenterTest given = this, when = this, then = this, and = this, with = this;

}
