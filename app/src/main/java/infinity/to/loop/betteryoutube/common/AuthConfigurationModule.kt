package infinity.to.loop.betteryoutube.common

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import dagger.Provides
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration

@dagger.Module
class AuthConfigurationModule {

    @Provides
    fun authServiceConfigurations(context: Context): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
                Uri.parse(context.getString(R.string.authEndpoint)),
                Uri.parse(context.getString(R.string.tokenEndpoint)))
    }

    @Provides
    fun authStateManager(sharedPreferences: SharedPreferences, configuration: AuthorizationServiceConfiguration): AuthState {
        val authString = sharedPreferences.getString(SharedPreferenceKeys.authState, null)
        authString?.let {
            return AuthState.jsonDeserialize(it)
        }
        return AuthState(configuration)
    }

    @Provides
    fun authService(context: Context) = AuthorizationService(context)

}