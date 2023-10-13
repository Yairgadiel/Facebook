package com.ml.quaterion.facenetdetection;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {


    // region Creator

    public SettingsViewModel() {
        _maxResults = SettingsRepo.getInstance().getResultsNum();
    }

    // endregion

    // region Members

    private MutableLiveData<Integer> _maxResults;

    // endregion

    // region Properties

    public MutableLiveData<Integer> getMaxResults() {
        return _maxResults;
    }

    // endregion
}
