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

import java.util.Locale;

import org.kdt.kanbandatatracker.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ScanActivity extends FragmentActivity implements
        ActionBar.TabListener, NavigationView {

    private NfcForegroundDispatchController nfcDispatchController;
    private final NavigationPresenter presenter;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private TabPagerAdapter tabPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    public ScanActivity() {
        presenter = new NavigationPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("ScanActivity", "onCreate");
        setContentView(R.layout.activity_scan);
        nfcDispatchController = new NfcForegroundDispatchController(this);

        addTabsToActionBar();
    }

    private void addTabsToActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        configureViewPager();
        enableSwipeToMoveBetweenTabs(actionBar);

        addTabsTo(actionBar);
    }

    private void addTabsTo(final ActionBar actionBar) {
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < tabPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(tabPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }

    private void configureViewPager() {
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tabPagerAdapter);
    }

    private void enableSwipeToMoveBetweenTabs(final ActionBar actionBar) {
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        viewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // TODO Move to presenter?
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        // TODO Move into the navigation presenter??
        Fragment displayedFragment = getDisplayedFragment();
        if (displayedFragment instanceof IntentListener) {
            ((IntentListener) displayedFragment).onNewIntent(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcDispatchController.disableForegroundDispatch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcDispatchController.enableForegroundDispatch();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void helpMenuItemClicked(MenuItem item) {
        presenter.helpMenuItemClicked();
    }

    public void aboutMenuItemClicked(MenuItem item) {
        presenter.aboutMenuItemClicked();
    }

    public void settingsMenuItemClicked(MenuItem item) {
        presenter.settingsMenuItemClicked();
    }

    @Override
    public void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
        // TODO Move to presenter?
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    private Fragment getDisplayedFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentByTag(makeFragmentName(
                viewPager.getId(), viewPager.getCurrentItem()));
    }

    private String makeFragmentName(int containerId, int position) {
        return "android:switcher:" + containerId + ":" + position;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new ScanFragment();
            }
            return new ProgramFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.title_scan_tab).toUpperCase(l);
            case 1:
                return getString(R.string.title_program_tab).toUpperCase(l);
            }
            return null;
        }
    }
}
