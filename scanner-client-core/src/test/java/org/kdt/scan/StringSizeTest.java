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
package org.kdt.scan;

import java.nio.charset.Charset;

import org.junit.Test;

public class StringSizeTest {
    private static final Charset ASCII = Charset.forName("US-ASCII");

    @Test
    public void output_size_in_bytes() throws Exception {
        String toTest = "550e8400-e29b-41d4-a716-446655440000\tConnector developer can use an API to throttle a connector\t8";
        byte[] asBytes = toTest.getBytes(ASCII);
        System.out.println("Num bytes: " + asBytes.length);
    }
}
