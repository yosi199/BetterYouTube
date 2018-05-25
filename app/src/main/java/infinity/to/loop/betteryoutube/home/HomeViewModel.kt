package infinity.to.loop.betteryoutube.home

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.common.eventbus.EventBus
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import net.openid.appauth.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val context: Context,
                                        private val eventBus: EventBus,
                                        private val api: YouTubeApi,
                                        private val clientId: String,
                                        private val configuration: AuthorizationServiceConfiguration,
                                        private val authState: AuthState,
                                        private val sharedPreferences: SharedPreferences,
                                        private val authService: AuthorizationService) {

    val authenticated = MutableLiveData<Boolean>()

    fun setIntent(intent: Intent?) {
        intent?.apply {
            val response = AuthorizationResponse.fromIntent(this)
            val exception = AuthorizationException.fromIntent(this)

            response?.let {
                authState.update(response, exception)
                exchangeToken(response)
            }
        }
    }

    private fun exchangeToken(response: AuthorizationResponse) {
        authService.performTokenRequest(response.createTokenExchangeRequest(),
                { resp, ex ->
                    resp?.let {
                        authState.update(it, ex)
                        sharedPreferences
                                .edit()
                                .putString(SharedPreferenceKeys.userToken, authState.jsonSerializeString())
                                .apply()
                        authenticated.postValue(true)
                    }
                })
    }
}