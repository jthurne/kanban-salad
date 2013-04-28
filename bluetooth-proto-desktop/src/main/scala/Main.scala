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
/**
 *
 */
import java.io._
import javax.bluetooth._
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection
import javax.microedition.io.StreamConnectionNotifier
import org.kanbansalad.bluetooth.TaskLookupRequest
import org.kanbansalad.bluetooth.TaskLookupResponse

object Main {

  def main(args: Array[String]) {
    val mLocalDevice = LocalDevice.getLocalDevice();

    val connectionNotifier =
      Connector.open("btspp://localhost:" +
        "792510F682524C8480F5E1F17386AECF;name=Prototype;" +
        "authenticate=true;encrypt=true;master=false").asInstanceOf[StreamConnectionNotifier];

    println("accepting on " + mLocalDevice.getBluetoothAddress());

    while (true) {
      val streamConnection = connectionNotifier.acceptAndOpen();

      try {
        val in = new ObjectInputStream(streamConnection.openInputStream());
        val out = new ObjectOutputStream(streamConnection.openOutputStream());

        while (true) {
          val request = in.readObject().asInstanceOf[TaskLookupRequest]
          out.writeObject(new TaskLookupResponse(request.getId(), "Lookup tasks with bluetooth", "2"))
        }
      } catch {
        case e: EOFException => println("Client closed the connection.")
      } finally {
        streamConnection.close()
      }
    }
  }

}