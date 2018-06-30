package infinity.to.loop.betteryoutube.home

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import com.google.api.services.youtube.YouTube
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject
import javax.inject.Provider

class HomeViewModel @Inject constructor(private val sharedPrefs: SharedPreferences,
                                        private val api: YouTube,
                                        private val clientId: String,
                                        private val handler: Handler,
                                        private val state: Provider<AuthState>,
                                        private val service: AuthorizationService,
                                        private val configuration: AuthorizationServiceConfiguration) {
    val openDrawer = MutableLiveData<Boolean>()

    companion object {
        val TAG = HomeViewModel::class.java.name
    }

    fun onResume() {
        maybeFirstTime()
    }

    private fun maybeFirstTime() {
        val firstTime = sharedPrefs.getBoolean(SharedPreferenceKeys.firstTime, false)
        handler.postDelayed({ openDrawer.postValue(firstTime) }, 1500)
        sharedPrefs.edit().putBoolean(SharedPreferenceKeys.firstTime, false).apply()
    }

    fun loadChannels() {
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            val request = api.channels().list("id, mine")
            request.key = clientId
            request.oauthToken = accessToken

            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.items
                    }, {
                        Log.e(TAG, "Couldn't fetch channel info ${it.message}")
                    })
        }
    }
}