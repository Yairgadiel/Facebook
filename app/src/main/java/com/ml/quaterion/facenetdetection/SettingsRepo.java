package com.ml.quaterion.facenetdetection;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

public class SettingsRepo {

    // Region Singleton

    private static SettingsRepo _instance;

    private SettingsRepo(){
        _instance = this;
        _resultsNum = new MutableLiveData<>(3);
    }

    public static SettingsRepo getInstance() {
        return _instance == null ? new SettingsRepo() : _instance;
    }

    public void init(Context context) {
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        _sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals(context.getString(R.string.max_results_key))) {
                _resultsNum.setValue(Integer.parseInt(
                        sharedPreferences.getString(key,"3")));
            }
        });
    }

    // endregion

    // region Members

    private SharedPreferences _sharedPreferences;
    private MutableLiveData<Integer> _resultsNum;

    // endregion

    // region Properties

    public MutableLiveData<Integer> getResultsNum() {
        return _resultsNum;
    }

    // endregion

    // region Public Methods



}
