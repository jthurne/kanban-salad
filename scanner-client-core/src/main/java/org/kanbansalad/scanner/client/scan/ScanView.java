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
package org.kanbansalad.scanner.client.scan;

import org.kanbansalad.scanner.client.Visible;
import org.kanbansalad.scanner.client.tag.ScanableTag;

/**
 * 
 */
public interface ScanView {
    public void appendToTags(ScanableTag tag);

    public void deleteTag(int position);

    public void selectTag(int position);

    public void clearTags();

    public void showTagContextMenu();

    public void closeTagContextMenu();

    public Visible getVisisble();

    public Visible getContextMenuVisible();

    public void showException(Exception exception);

    public void refreshTags();

    public void hideSoftKeyboard();
}
