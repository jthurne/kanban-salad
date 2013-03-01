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

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.kdt.model.Cell;
import org.kdt.model.Task;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    public void displays_name_of_a_scanned_cell__when_a_cell_tag_is_scanned() throws Exception {
        when.presenter.scanned(new Cell("Swimlane - Queue"));
        then.the_scan_should_be_displayed_as("Swimlane - Queue");
    }    
    
    @Test
    public void displays_name_of_a_scanned_task__when_a_task_tag_is_scanned() throws Exception {
        when.presenter.scanned(new Task("Do Something"));
        then.the_scan_should_be_displayed_as("\t\tDo Something");
    }
    
    @Test
    public void ignores_null_scans() throws Exception {
        when.presenter.scanned(null);
    }
    
    @Test
    public void clears_the_log_when_the_snapshot_is_saved() throws Exception {
        when.presenter.saveSnapshot();
        then.the_scan_log_should_be_cleared();
    }
    
    private void the_scan_log_should_be_cleared() {
        verify(mockView).clearLog();
    }

    private void the_scan_should_be_displayed_as(String textToDisplay) {
        verify(mockView).appendToLog(textToDisplay);
    }

    @SuppressWarnings("unused")
    private CapturePresenterTest given = this, when = this, then = this, and = this, with = this;

}
