package com.sohu.jch.krnt_android_group.view;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.sohu.jch.krnt_android_group.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {

    private MySettingPreferenceFragmet fragmet;
    private MySharePreferenceChangeListener sharePreferenceChangeListener = new
            MySharePreferenceChangeListener();

    private String HOST_KEY;
    private String PORT_KEY;
    private String METHOD_KEY;
    private String STUN_KEY;
    private String report_able;
    private String VIDEO_CALLABLE_KEY;
    private String VIDEO_CODE_KEY;
    private String VIDEO_FRAME_KEY;
    private String VIDEO_RESOLUTION_KEY;
    private String MAX_VIDEO_BITRATE_KEY;
    private String MAX_VIDEO_BITRATE_VALUE_KEY;
    private String AUDIO_CODE_KEY;
    private String AUDIO_PROCESS_KEY;
    private String MAX_AUDIO_BITRATE_KEY;
    private String MAX_AUDIO_BITRATE_VALUE_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HOST_KEY = getString(R.string.pref_key_host);
        PORT_KEY = getString(R.string.pref_key_port);
        METHOD_KEY = getString(R.string.pref_key_method);
        STUN_KEY = getString(R.string.pref_key_stun);
        report_able = getString(R.string.pref_key_report_status);
        VIDEO_CALLABLE_KEY = getString(R.string.pref_key_video_callable);
        VIDEO_CODE_KEY = getString(R.string.pref_key_video_code);
        VIDEO_FRAME_KEY = getString(R.string.pref_key_video_frame);
        VIDEO_RESOLUTION_KEY = getString(R.string.pref_key_video_resulotion);
        MAX_VIDEO_BITRATE_KEY = getString(R.string.pref_maxVideoBitrate_key);
        MAX_VIDEO_BITRATE_VALUE_KEY = getString(R.string.pref_maxVideoBitratevalue_key);
        AUDIO_CODE_KEY = getString(R.string.pref_audiocodec_key);
        AUDIO_PROCESS_KEY = getString(R.string.pref_audioprocessing_key);
        MAX_AUDIO_BITRATE_KEY = getString(R.string.pref_maxAudiobitrate_key);
        MAX_AUDIO_BITRATE_VALUE_KEY = getString(R.string.pref_maxAudiobitratevalue_key);

        FragmentManager fManager = getFragmentManager();
        FragmentTransaction ft = fManager.beginTransaction();
        fragmet = new MySettingPreferenceFragmet();
        ft.replace(android.R.id.content, fragmet);

        ft.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = fragmet.getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharePreferenceChangeListener);

        initSharePreference(sharedPreferences);
    }

    private void initSharePreference(SharedPreferences sharedPreferences) {

        updateSummary(sharedPreferences, HOST_KEY);
        updateSummary(sharedPreferences, PORT_KEY);
        updateSummary(sharedPreferences, METHOD_KEY);
        updateSummary(sharedPreferences, STUN_KEY);
        updateCheckSummary(sharedPreferences, report_able);

        updateCheckSummary(sharedPreferences, VIDEO_CALLABLE_KEY);
        updateSummary(sharedPreferences, VIDEO_CODE_KEY);
        updateSummary(sharedPreferences, VIDEO_FRAME_KEY);
        updateSummary(sharedPreferences, VIDEO_RESOLUTION_KEY);
        setVideoBitrateEnable(sharedPreferences);
        updateSummary(sharedPreferences, MAX_VIDEO_BITRATE_KEY);
        updateSummaryBitrate(sharedPreferences, MAX_VIDEO_BITRATE_VALUE_KEY);

        updateSummary(sharedPreferences, AUDIO_CODE_KEY);
        updateCheckSummary(sharedPreferences, AUDIO_PROCESS_KEY);
        setAudioBitrateEnable(sharedPreferences);
        updateSummary(sharedPreferences, MAX_AUDIO_BITRATE_KEY);
        updateSummaryBitrate(sharedPreferences, MAX_AUDIO_BITRATE_VALUE_KEY);


    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {

        Preference updatePref = fragmet.findPreference(key);
        updatePref.setSummary(sharedPreferences.getString(key, ""));
    }

    private void updateCheckSummary(SharedPreferences preferences, String key) {

        CheckBoxPreference updatePref = (CheckBoxPreference) fragmet.findPreference(key);
        updatePref.setChecked(preferences.getBoolean(key, true));
    }

    private void updateSummaryBitrate(
            SharedPreferences sharedPreferences, String key) {
        Preference updatedPref = fragmet.findPreference(key);
        updatedPref.setSummary(sharedPreferences.getString(key, "") + " kbps");
    }

    private void setVideoBitrateEnable(SharedPreferences sharedPreferences) {
        Preference bitratePreferenceValue =
                fragmet.findPreference(MAX_VIDEO_BITRATE_VALUE_KEY);
        String bitrateTypeDefault = getString(R.string.pref_maxVideoBitrate_default);
        String bitrateType = sharedPreferences.getString(MAX_VIDEO_BITRATE_KEY, bitrateTypeDefault);
        if (bitrateType.equals(bitrateTypeDefault)) {
            bitratePreferenceValue.setEnabled(false);
        } else {
            bitratePreferenceValue.setEnabled(true);
        }
    }

    private void setAudioBitrateEnable(SharedPreferences sharedPreferences) {
        Preference bitratePreferenceValue =
                fragmet.findPreference(MAX_AUDIO_BITRATE_VALUE_KEY);
        String bitrateTypeDefault = getString(R.string.pref_maxAudiobitrate_default);
        String bitrateType = sharedPreferences.getString(MAX_AUDIO_BITRATE_KEY, bitrateTypeDefault);
        if (bitrateType.equals(bitrateTypeDefault)) {
            bitratePreferenceValue.setEnabled(false);
        } else {
            bitratePreferenceValue.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        fragmet.getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(sharePreferenceChangeListener);
        super.onPause();

    }

    /**
     * fragment.
     */
    public static class MySettingPreferenceFragmet extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_pref);
        }
    }


    private class MySharePreferenceChangeListener implements SharedPreferences
            .OnSharedPreferenceChangeListener {

        private static final String key = "key";

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals(HOST_KEY) ||
                    key.equals(PORT_KEY) ||
                    key.equals(METHOD_KEY) ||
                    key.equals(STUN_KEY) ||
                    key.equals(VIDEO_CODE_KEY) ||
                    key.equals(VIDEO_FRAME_KEY) ||
                    key.equals(VIDEO_RESOLUTION_KEY) ||
                    key.equals(AUDIO_CODE_KEY) ) {

                updateSummary(sharedPreferences, key);
            } else if (key.equals(VIDEO_CALLABLE_KEY)||
                    key.equals(AUDIO_PROCESS_KEY)||
                    key.equals(report_able)) {

                updateCheckSummary(sharedPreferences, key);
            } else if (key.equals(MAX_VIDEO_BITRATE_KEY)) {
                setVideoBitrateEnable(sharedPreferences);
                updateSummary(sharedPreferences, key);
            } else if (key.equals(MAX_AUDIO_BITRATE_KEY)) {
                setAudioBitrateEnable(sharedPreferences);
                updateSummary(sharedPreferences, key);
            } else if (key.equals(MAX_AUDIO_BITRATE_VALUE_KEY) ||
                    key.equals(MAX_VIDEO_BITRATE_VALUE_KEY)) {
                updateSummaryBitrate(sharedPreferences, key);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                setResult(RESULT_OK);
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
