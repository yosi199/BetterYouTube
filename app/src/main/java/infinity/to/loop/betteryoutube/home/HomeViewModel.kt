package infinity.to.loop.betteryoutube.home

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.os.Handler
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val sharedPrefs: SharedPreferences,
                                        private val handler: Handler,
                                        private val state: AuthState,
                                        private val service: AuthorizationService,
                                        private val configuration: AuthorizationServiceConfiguration) {

    val openDrawer = MutableLiveData<Boolean>()

    init {
        val firstTime = sharedPrefs.getBoolean(SharedPreferenceKeys.firstTime, false)
        handler.postDelayed({ openDrawer.postValue(firstTime) }, 500)
    }

}