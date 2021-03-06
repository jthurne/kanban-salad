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
package org.kanbansalad.scanner.client.android;

import org.kanbansalad.scanner.R;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        createLicenseText();
    }

    private void createLicenseText() {
        Resources res = getResources();
        CharSequence licenseText = Html.fromHtml(res
                .getString(R.string.license));

        TextView licenseView = (TextView) findViewById(R.id.license);
        licenseView.setText(licenseText, TextView.BufferType.SPANNABLE);
    }

    public void closeButtonClicked(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }
}
