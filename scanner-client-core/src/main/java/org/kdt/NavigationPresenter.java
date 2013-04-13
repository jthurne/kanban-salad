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

/**
 * Presenter for overall navigation actions.
 */
public class NavigationPresenter {

    private final NavigationView view;

    public NavigationPresenter(NavigationView view) {
        this.view = view;
    }

    public void helpMenuItemClicked() {
        view.showHelp();
    }

    public void aboutMenuItemClicked() {
        view.showAbout();
    }

    public void settingsMenuItemClicked() {
        view.showSettings();
    }
}
