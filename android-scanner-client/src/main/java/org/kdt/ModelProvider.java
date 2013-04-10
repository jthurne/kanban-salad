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

import java.io.File;

import android.os.Environment;

// TODO Should we really have a singleton of the model??
// Good: the model doesn't actually need to be persistent
// Possibly Bad: the model will take more memory 
public class ModelProvider {
    private static final ListModel INSTANCE = new ListModel(
            createCsvFilenameTemplate());

    private ModelProvider() {
    }

    public static ListModel getInstance() {
        return INSTANCE;
    }

    private static String createCsvFilenameTemplate() {
        String externalStoragePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        return new File(externalStoragePath, "kanban-snapshot-%tF-%<tH%<tM.csv")
                .toString();
    }
}
