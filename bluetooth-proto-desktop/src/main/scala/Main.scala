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

object Main {

  def main(args: Array[String]) {
    val mLocalDevice = LocalDevice.getLocalDevice();

    val connectionNotifier =
      Connector.open("btspp://localhost:" +
        "DEADBEEFDEADBEEFDEADBEEFDEADBEEF;name=Prototype;" +
        "authenticate=false;encrypt=false;master=false").asInstanceOf[StreamConnectionNotifier];

    println("accepting on " + mLocalDevice.getBluetoothAddress());
    val streamConnection = connectionNotifier.acceptAndOpen();
    val is = streamConnection.openInputStream();
    val reader = new BufferedReader(new InputStreamReader(is))

    while (reader.ready()) {
      println(reader.readLine());
    }
  }

}