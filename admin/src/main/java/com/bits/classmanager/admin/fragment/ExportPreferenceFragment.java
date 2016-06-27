package com.bits.classmanager.admin.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ExportPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.id);
    }
}
