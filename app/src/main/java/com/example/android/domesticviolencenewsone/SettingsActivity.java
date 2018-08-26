package com.example.android.domesticviolencenewsone;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }


    public static class DVArticlePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference pageSize = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(pageSize);

            Preference sectionId = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryToValue(sectionId);
        }

        //ANTI-PLAGIARISM ALERT!! I got help with this code from a Udacity mentor. I was in over my head with the MultiSelectListPreference

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof MultiSelectListPreference) {
                Set<String> values = preferences.getStringSet(preference.getKey(), null);
                onPreferenceChange(preference, values);
            } else {
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference instanceof MultiSelectListPreference) {
                List<String> newValues = new ArrayList<>((HashSet<String>) value);
                preference.setSummary(TextUtils.join(", ", getSummaryListFromValueList(newValues)));
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private List<String> getSummaryListFromValueList(List<String> valueList) {
            String[] allSummaries = getResources().getStringArray(R.array.settings_section_labels);
            String[] allValues = getResources().getStringArray(R.array.settings_section_values);

            List<String> summaryList = new ArrayList<>();
            for (int i = 0; i < allValues.length; i++) {
                for (String value : valueList) {
                    if (allValues[i].equals(value)) {
                        summaryList.add(allSummaries[i]);
                    }
                }
            }
            return summaryList;
        }
    }
}

