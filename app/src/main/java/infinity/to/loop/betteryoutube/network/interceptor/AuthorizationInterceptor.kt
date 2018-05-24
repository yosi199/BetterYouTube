package infinity.to.loop.betteryoutube.network.interceptor

import android.content.SharedPreferences
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationResponse
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(private val preferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = AuthState.jsonDeserialize(preferences.getString(SharedPreferenceKeys.userToken, null))
        token?.let {
            return chain.proceed(chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", AuthorizationResponse.TOKEN_TYPE_BEARER + ' ' + token.accessToken)
                    .build())
        }
        return chain.proceed(chain.request())
    }
}