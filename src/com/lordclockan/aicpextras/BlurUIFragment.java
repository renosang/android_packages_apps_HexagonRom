/*
 * Copyright (C) 2016 The Xperia Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidvnteam.hexagonrom;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.droidvnteam.R;
import com.droidvnteam.hexagonrom.widget.SeekBarPreferenceCham;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class BlurUIFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.content_main, new BlurUISettingsFragment())
                .commit();
    }

    public static class BlurUISettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {

        public BlurUISettingsFragment() {
        }

        private static final String TAG = "BlurUI";

        private SharedPreferences mBlurUISettings;
        private Editor toEditBlurUISettings;
        private String sQuickSett = "false";

        //Switch Preferences
        private SwitchPreference mExpand;
        private SwitchPreference mQuickSett;
        private TwoStatePreference mRecentsSett;

        //Transluency,Radius and Scale
        private SeekBarPreferenceCham mScale;
        private SeekBarPreferenceCham mRadius;
        private SeekBarPreferenceCham mQuickSettPerc;

        //Recents Radius and Scale
        private SeekBarPreferenceCham mRecentsScale;
        private SeekBarPreferenceCham mRecentsRadius;

        //Colors
        private ColorPickerPreference mDarkBlurColor;
        private ColorPickerPreference mLightBlurColor;
        private ColorPickerPreference mMixedBlurColor;

        private static final int BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT = Color.DKGRAY;
        private static final int BLUR_MIXED_COLOR_PREFERENCE_DEFAULT = Color.GRAY;
        private static final int BLUR_DARK_COLOR_PREFERENCE_DEFAULT = Color.LTGRAY;
        private static final String PREF_BLUR_COLORS_DEFAULTS = "blur_default_colors";

        private Preference mBlurDefaultColors;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.blur_ui);

            PreferenceScreen prefSet = getPreferenceScreen();
            ContentResolver resolver = getActivity().getContentResolver();

            //Some help here
            int intLightColor;
            int intDarkColor;
            int intMixedColor;
            String hexLightColor;
            String hexDarkColor;
            String hexMixedColor;

            mBlurUISettings = getActivity().getSharedPreferences("BlurUI", Context.MODE_PRIVATE);

            mExpand = (SwitchPreference) prefSet.findPreference("blurred_status_bar_expanded_enabled_pref");
            mExpand.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_ENABLED_PREFERENCE_KEY, 0) == 1));

            mScale = (SeekBarPreferenceCham) findPreference("statusbar_blur_scale");
            mScale.setValue(Settings.System.getInt(resolver, Settings.System.BLUR_SCALE_PREFERENCE_KEY, 10));
            mScale.setOnPreferenceChangeListener(this);

            mRadius = (SeekBarPreferenceCham) findPreference("statusbar_blur_radius");
            mRadius.setValue(Settings.System.getInt(resolver, Settings.System.BLUR_RADIUS_PREFERENCE_KEY, 5));
            mRadius.setOnPreferenceChangeListener(this);

            mQuickSett = (SwitchPreference) prefSet.findPreference("translucent_quick_settings_pref");
            mQuickSett.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY, 0) == 1));

            mQuickSettPerc = (SeekBarPreferenceCham) findPreference("quick_settings_transluency");
            mQuickSettPerc.setValue(Settings.System.getInt(resolver, Settings.System.TRANSLUCENT_QUICK_SETTINGS_PRECENTAGE_PREFERENCE_KEY, 60));
            mQuickSettPerc.setOnPreferenceChangeListener(this);

            mRecentsScale = (SeekBarPreferenceCham) findPreference("recents_blur_scale");
            mRecentsScale.setValue(Settings.System.getInt(resolver, Settings.System.RECENT_APPS_SCALE_PREFERENCE_KEY, 6));
            mRecentsScale.setOnPreferenceChangeListener(this);

            mRecentsRadius = (SeekBarPreferenceCham) findPreference("recents_blur_radius");
            mRecentsRadius.setValue(Settings.System.getInt(resolver, Settings.System.RECENT_APPS_RADIUS_PREFERENCE_KEY, 3));
            mRecentsRadius.setOnPreferenceChangeListener(this);

            mLightBlurColor = (ColorPickerPreference) findPreference("blur_light_color");
            mLightBlurColor.setOnPreferenceChangeListener(this);
            intLightColor = Settings.System.getInt(resolver, Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT);
            hexLightColor = String.format("#%08x", (0xffffffff & intLightColor));
            mLightBlurColor.setSummary(hexLightColor);
            mLightBlurColor.setNewPreviewColor(intLightColor);

            mDarkBlurColor = (ColorPickerPreference) findPreference("blur_dark_color");
            mDarkBlurColor.setOnPreferenceChangeListener(this);
            intDarkColor = Settings.System.getInt(resolver, Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, BLUR_DARK_COLOR_PREFERENCE_DEFAULT);
            hexDarkColor = String.format("#%08x", (0xffffffff & intDarkColor));
            mDarkBlurColor.setSummary(hexDarkColor);
            mDarkBlurColor.setNewPreviewColor(intDarkColor);

            mMixedBlurColor = (ColorPickerPreference) findPreference("blur_mixed_color");
            mMixedBlurColor.setOnPreferenceChangeListener(this);
            intMixedColor = Settings.System.getInt(resolver, Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, BLUR_MIXED_COLOR_PREFERENCE_DEFAULT);
            hexMixedColor = String.format("#%08x", (0xffffffff & intMixedColor));
            mMixedBlurColor.setSummary(hexMixedColor);
            mMixedBlurColor.setNewPreviewColor(intMixedColor);

            mBlurDefaultColors = findPreference(PREF_BLUR_COLORS_DEFAULTS);
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mScale) {
                int value = ((Integer)newValue).intValue();
                Settings.System.putInt(
                        resolver, Settings.System.BLUR_SCALE_PREFERENCE_KEY, value);
                return true;
            } else if (preference == mRadius) {
                int value = ((Integer)newValue).intValue();
                Settings.System.putInt(
                        resolver, Settings.System.BLUR_RADIUS_PREFERENCE_KEY, value);
                return true;
            } else if (preference == mQuickSettPerc) {
                int value = ((Integer)newValue).intValue();
                Settings.System.putInt(
                        resolver, Settings.System.TRANSLUCENT_QUICK_SETTINGS_PRECENTAGE_PREFERENCE_KEY, value);
                return true;
            } else if (preference == mRecentsScale) {
                int value = ((Integer)newValue).intValue();
                Settings.System.putInt(
                    resolver, Settings.System.RECENT_APPS_SCALE_PREFERENCE_KEY, value);
                return true;
            } else if(preference == mRecentsRadius) {
                int value = ((Integer)newValue).intValue();
                Settings.System.putInt(
                    resolver, Settings.System.RECENT_APPS_RADIUS_PREFERENCE_KEY, value);
                return true;
            } else if (preference == mLightBlurColor) {
                String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, intHex);
                return true;
            } else if (preference == mDarkBlurColor) {
                String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, intHex);
                return true;
            } else if (preference == mMixedBlurColor) {
                String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, intHex);
                return true;
            }
            return false;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                             final Preference preference) {
            final ContentResolver resolver = getActivity().getContentResolver();
            if  (preference == mExpand) {
                boolean enabled = ((SwitchPreference)preference).isChecked();
                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_EXPANDED_ENABLED_PREFERENCE_KEY, enabled ? 1:0);
                updatePrefs();
            } else if (preference == mQuickSett) {
                boolean enabled = ((SwitchPreference)preference).isChecked();
                Settings.System.putInt(resolver,
                        Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY, enabled ? 1:0);
                if (enabled) {
                    sQuickSett = "true";
                } else {
                    sQuickSett = "false";
                }
                toEditBlurUISettings = mBlurUISettings.edit();
                toEditBlurUISettings.putString("quick_settings_transluency", sQuickSett);
                toEditBlurUISettings.commit();
            } else if (preference == mBlurDefaultColors) {
                int intColor;
                String hexColor;

                Settings.System.putInt(resolver,
                        Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT);
                intColor = Settings.System.getInt(resolver,
                        Settings.System.BLUR_LIGHT_COLOR_PREFERENCE_KEY, BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT);
                hexColor = String.format("#%08x", (BLUR_LIGHT_COLOR_PREFERENCE_DEFAULT & intColor));
                mLightBlurColor.setSummary(hexColor);
                mLightBlurColor.setNewPreviewColor(intColor);


                Settings.System.putInt(resolver,
                        Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, BLUR_MIXED_COLOR_PREFERENCE_DEFAULT);
                intColor = Settings.System.getInt(resolver,
                        Settings.System.BLUR_MIXED_COLOR_PREFERENCE_KEY, BLUR_MIXED_COLOR_PREFERENCE_DEFAULT);
                hexColor = String.format("#%08x", (BLUR_MIXED_COLOR_PREFERENCE_DEFAULT & intColor));
                mMixedBlurColor.setSummary(hexColor);
                mMixedBlurColor.setNewPreviewColor(intColor);

                Settings.System.putInt(resolver,
                        Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, BLUR_DARK_COLOR_PREFERENCE_DEFAULT);
                intColor = Settings.System.getInt(resolver,
                        Settings.System.BLUR_DARK_COLOR_PREFERENCE_KEY, BLUR_DARK_COLOR_PREFERENCE_DEFAULT);
                hexColor = String.format("#%08x", (BLUR_DARK_COLOR_PREFERENCE_DEFAULT & intColor));
                mDarkBlurColor.setSummary(hexColor);
                mDarkBlurColor.setNewPreviewColor(intColor);

                Snackbar.make(getView(), R.string.values_restored_title,
                        Snackbar.LENGTH_LONG).show();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        public void sharedPreferences() {
            toEditBlurUISettings = mBlurUISettings.edit();
            toEditBlurUISettings.putString("quick_settings_transluency", sQuickSett);
            toEditBlurUISettings.commit();
        }

        private void updatePrefs() {
            final ContentResolver resolver = getActivity().getContentResolver();

            boolean tempQuickSett = mBlurUISettings.getString("quick_settings_transluency", "").equals("true");

            if (Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_EXPANDED_ENABLED_PREFERENCE_KEY, 0) == 1) {
                Settings.System.putInt(resolver,
                        Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY, tempQuickSett ? 1:0);
            } else {
                Settings.System.putInt(resolver,
                        Settings.System.TRANSLUCENT_QUICK_SETTINGS_PREFERENCE_KEY, 0);
            }
        }
    }
}
