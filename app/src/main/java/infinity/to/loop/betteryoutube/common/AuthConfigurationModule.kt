package infinity.to.loop.betteryoutube.common

import android.content.Context
import android.net.Uri
import dagger.Provides
import infinity.to.loop.betteryoutube.R
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
    fun authStateManager(authServiceConfigurations: AuthorizationServiceConfiguration): AuthState {
        return AuthState(authServiceConfigurations)
    }

    @Provides
    fun authService(context: Context) = AuthorizationService(context)

}