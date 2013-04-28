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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kanbansalad.bluetooth.Response;
import org.kanbansalad.bluetooth.TaskLookupRequest;
import org.kanbansalad.trackable.Task;

/**
 * 
 */
public class BluetoothTaskFinder implements TaskFinder {

    private final BluetoothConnectionProvider connectionProvider;

    public BluetoothTaskFinder(BluetoothConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Task find(String taskId) {
        BluetoothConnection conn = connectionProvider.get();
        try {
            sendRequestFor(taskId, conn);
            return readResponseFrom(conn).getResult();
        } catch (IOException e) {
            throw new TaskFinder.LookupFailed(e);
        } catch (ClassNotFoundException e) {
            // Not tested because it is very hard to cause the input stream to
            // generate a ClassNotFoundExcepiton
            throw new TaskFinder.LookupFailed(e);
        } finally {
            try {
                conn.close();
            } catch (IOException e) {
                // Not much we can do
            }
        }
    }

    private void sendRequestFor(String taskId, BluetoothConnection conn)
            throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(
                conn.getOutputStream());
        out.writeObject(new TaskLookupRequest(taskId));
    }

    @SuppressWarnings("unchecked")
    private Response<Task> readResponseFrom(BluetoothConnection conn)
            throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
        Response<Task> response = (Response<Task>) in
                .readObject();
        return response;
    }

}
