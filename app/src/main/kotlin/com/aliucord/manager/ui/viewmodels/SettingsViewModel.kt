package com.aliucord.manager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    fun clearCacheDir() {
        getApplication<Application>().cacheDir.deleteRecursively()
    }

}
