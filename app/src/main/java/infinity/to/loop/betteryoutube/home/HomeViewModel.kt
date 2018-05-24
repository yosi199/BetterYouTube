package infinity.to.loop.betteryoutube.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import infinity.to.loop.betteryoutube.network.interceptor.endpoints.YouTubeApi
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val context: Context,
                                        private val api: YouTubeApi,
                                        private val apiKey: String,
                                        private val configuration: AuthorizationServiceConfiguration,
                                        private val authState: AuthState,
                                        private val sharedPreferences: SharedPreferences,
                                        private val authService: AuthorizationService) {

    fun getUserPlaylists(v: View) {
        api.userPlaylists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                })
    }

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
                        authState.update(resp, ex)
                        sharedPreferences
                                .edit()
                                .putString(SharedPreferenceKeys.userToken, authState.jsonSerializeString())
                                .apply()
                    }
                })
    }
}