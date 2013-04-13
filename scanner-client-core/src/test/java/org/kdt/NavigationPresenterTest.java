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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NavigationPresenterTest {

    private NavigationPresenter presenter;

    @Mock
    private NavigationView mockView;

    @Before
    public void given_a_presenter() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new NavigationPresenter(mockView);
    }

    @Test
    public void displays_help__when_help_menu_selected() throws Exception {
        when.presenter.helpMenuItemClicked();
        then.the_help_should_be_displayed();
    }

    @Test
    public void displays_about__when_about_menu_selected() throws Exception {
        when.presenter.aboutMenuItemClicked();
        then.about_should_be_displayed();
    }

    @Test
    public void displays_settings__when_settings_menu_selected()
            throws Exception {
        when.presenter.settingsMenuItemClicked();
        then.settings_should_be_displayed();
    }

    private void the_help_should_be_displayed() {
        verify(mockView).showHelp();
    }

    private void about_should_be_displayed() {
        verify(mockView).showAbout();
    }

    private void settings_should_be_displayed() {
        verify(mockView).showSettings();
    }

    @SuppressWarnings("unused")
    private NavigationPresenterTest given = this, when = this, then = this,
            and = this, with = this;

}
