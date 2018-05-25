package infinity.to.loop.betteryoutube.main

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.View
import com.google.api.services.youtube.YouTubeScopes
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import net.openid.appauth.*
import javax.inject.Inject


class MainViewModel @Inject constructor(private val context: Activity,
                                        private val clientID: String,
                                        private val state: AuthState,
                                        private val service: AuthorizationService,
                                        private val sharedPrefs: SharedPreferences) {

    val authenticated = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    companion object {
        const val SCOPES = "${YouTubeScopes.YOUTUBE} ${YouTubeScopes.YOUTUBE_READONLY} ${YouTubeScopes.YOUTUBE_FORCE_SSL} ${YouTubeScopes.YOUTUBE_UPLOAD}"
        const val AUTH_REQ = 199
    }

    init {
        authenticated.postValue(state.isAuthorized)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTH_REQ && data != null) {
            onAuthorizationResponse(data)
        }
    }

    private fun onAuthorizationResponse(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)
        state.update(response, exception)
        if (response != null) {
            state.update(response, exception)
            exchangeToken(response)
        }
    }

    private fun exchangeToken(response: AuthorizationResponse) {
        service.performTokenRequest(response.createTokenExchangeRequest(),
                { resp, ex ->
                    resp?.let {
                        state.update(it, ex)
                        sharedPrefs
                                .edit()
                                .putString(SharedPreferenceKeys.authState, state.jsonSerializeString())
                                .apply()
                        authenticated.postValue(state.isAuthorized)
                    }
                })
    }


    fun authenticate(v: View) {
        val authRequest = AuthorizationRequest
                .Builder(state.authorizationServiceConfiguration!!, clientID,
                        ResponseTypeValues.CODE, Uri.parse(context.getString(R.string.redirect_url)))
                .setScope(SCOPES)
                .build()

        val authIntent: Intent = service.getAuthorizationRequestIntent(authRequest)
        context.startActivityForResult(authIntent, AUTH_REQ)
        loading.postValue(true)
    }
}