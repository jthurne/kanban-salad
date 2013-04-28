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
package org.kanbansalad.scanner.client.program;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kanbansalad.bluetooth.TaskLookupRequest;
import org.kanbansalad.bluetooth.TaskLookupResponse;
import org.kanbansalad.scanner.client.program.BluetoothConnection;
import org.kanbansalad.scanner.client.program.BluetoothConnectionProvider;
import org.kanbansalad.scanner.client.program.BluetoothTaskFinder;
import org.kanbansalad.scanner.client.program.TaskFinder;
import org.kanbansalad.trackable.Task;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BluetoothTaskFinderTest {

    @Rule
    public ExpectedException we = ExpectedException.none();

    @Mock
    private BluetoothConnection mockConnection;

    @Mock
    private BluetoothConnectionProvider mockProvider;

    private BluetoothTaskFinder finder;

    private ByteArrayOutputStream outputStream;
    private Task returnedTask;

    @Before
    public void given_a_bluetooth_task_finder() throws Exception {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();
        when(mockProvider.get()).thenReturn(mockConnection);
        when(mockConnection.getOutputStream()).thenReturn(outputStream);

        the_connection_returns_response(new TaskLookupResponse(Task.NONE));

        finder = new BluetoothTaskFinder(mockProvider);
    }

    @Test
    public void sends_request() throws Exception {
        when.attempt_to_find_task("5555");
        then.it_should_send_a_task_request_for("5555");
    }

    @Test
    public void returns_task_built_from_the_response() throws Exception {
        given.the_connection_returns_response(new TaskLookupResponse("5555",
                "Eat Lunch", "1"));
        when.attempt_to_find_task("5555");
        then.it_should_return(new Task("5555", "Eat Lunch", "1"));
    }

    @Test
    public void throws_exception__if_one_of_the_streams_throws_an_IOException()
            throws Exception {
        given.the_input_stream_generates_an_IOException();

        we.expect(TaskFinder.LookupFailed.class);
        when.attempt_to_find_task("5555");
    }

    @Test
    public void closes_the_connection__when_request_completes_normally()
            throws Exception {
        when.attempt_to_find_task("5555");
        then.it_should_close_the_connection();
    }

    @Test
    public void closes_the_connection__when_request_fails_with_an_exception()
            throws Exception {
        given.the_input_stream_generates_an_IOException();

        try {
            when.attempt_to_find_task("5555");
        } catch (TaskFinder.LookupFailed e) {
            // We expect this
        }

        then.it_should_close_the_connection();
    }

    private void it_should_close_the_connection() throws Exception {
        verify(mockConnection).close();
    }

    private void the_input_stream_generates_an_IOException() throws Exception {
        // An attempt to read from an empty byte array will geneate an
        // IOException
        InputStream inputStream = new ByteArrayInputStream(new byte[] {});
        when(mockConnection.getInputStream()).thenReturn(inputStream);
    }

    private void the_connection_returns_response(
            TaskLookupResponse taskLookupResponse) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(taskLookupResponse);

        InputStream inputStream = new ByteArrayInputStream(
                out.toByteArray());
        when(mockConnection.getInputStream()).thenReturn(inputStream);
    }

    private void attempt_to_find_task(String id) {
        returnedTask = finder.find(id);
    }

    private void it_should_return(Task expectedTask) {
        assertThat(returnedTask,
                hasProperty("id", equalTo(expectedTask.getId())));
        assertThat(returnedTask,
                hasProperty("name", equalTo(expectedTask.getName())));
        assertThat(returnedTask,
                hasProperty("size", equalTo(expectedTask.getSize())));
    }

    private void it_should_send_a_task_request_for(String expectedId)
            throws Exception {
        byte[] output = outputStream.toByteArray();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
                output));

        TaskLookupRequest request = (TaskLookupRequest) in.readObject();
        assertThat(request.getId(), is(equalTo(expectedId)));
    }

    @SuppressWarnings("unused")
    private BluetoothTaskFinderTest given = this, when = this, then = this,
            and = this, with = this;
}
