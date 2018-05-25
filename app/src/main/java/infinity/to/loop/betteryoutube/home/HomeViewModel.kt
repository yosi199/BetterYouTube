package infinity.to.loop.betteryoutube.home

import android.content.SharedPreferences
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val sharedPrefs: SharedPreferences,
                                        private val state: AuthState,
                                        private val service: AuthorizationService,
                                        private val configuration: AuthorizationServiceConfiguration) {

}