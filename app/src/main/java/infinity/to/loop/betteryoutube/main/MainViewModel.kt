package infinity.to.loop.betteryoutube.main

import android.animation.ObjectAnimator
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.api.services.youtube.YouTubeScopes
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.home.HomeActivity
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import net.openid.appauth.*
import javax.inject.Inject


class MainViewModel @Inject constructor(private val context: Activity,
                                        private val clientID: String,
                                        private val state: AuthState,
                                        private val service: AuthorizationService,
                                        private val sharedPrefs: SharedPreferences) {

    private val signInAnimation = ObjectAnimator.ofFloat(0f, 1f).apply {
        this.startDelay = 1000
        this.interpolator = AccelerateDecelerateInterpolator()
        this.duration = 500
    }

    private val logoAnimation = ObjectAnimator.ofFloat(1f, 0f).apply {
        this.interpolator = AccelerateDecelerateInterpolator()
        this.duration = 200
    }

    private lateinit var authIntent: Intent
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
            sharedPrefs.edit().putBoolean(SharedPreferenceKeys.firstTime, true).apply()
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

        authIntent = service.getAuthorizationRequestIntent(authRequest)
        loading.postValue(true)
    }

    fun animateSignInBtn(signIn: View) {
        signInAnimation.addUpdateListener { anim -> signIn.alpha = anim.animatedValue as Float }
        signInAnimation.start()
    }

    fun animateToAuthScreen(logo: View) {
        logoAnimation.addUpdateListener { anim ->
            val value = anim.animatedValue as Float
            logo.scaleX = value
            if (value == 0f) startAuthScreen()
        }
        logoAnimation.start()
    }

    private fun startAuthScreen() {
        context.startActivityForResult(authIntent, MainViewModel.AUTH_REQ)
    }

    fun startHomeScreen() {
        context.startActivity(Intent(context, HomeActivity::class.java))
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.finish()
    }

    fun onStop() {
        signInAnimation.cancel()
        logoAnimation.cancel()
    }
}