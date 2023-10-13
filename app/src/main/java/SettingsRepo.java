import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

public class SettingsRepo {

    // region

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
    }

    // endregion

    // region Properties

    private SharedPreferences _sharedPreferences;
    private MutableLiveData<Integer> _resultsNum;

    // endregion

    // region Public Methods
}
