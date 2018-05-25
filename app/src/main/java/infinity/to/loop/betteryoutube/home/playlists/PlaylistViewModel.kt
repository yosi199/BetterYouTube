package infinity.to.loop.betteryoutube.home.playlists

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject

class PlaylistViewModel @Inject constructor(private val context: Context,
                                            private val api: YouTubeApi,
                                            private val clientId: String,
                                            private val configuration: AuthorizationServiceConfiguration,
                                            private val authState: AuthState,
                                            private val sharedPreferences: SharedPreferences,
                                            private val authService: AuthorizationService) {

    fun getUserPlaylists(v: View) {
        authState.performActionWithFreshTokens(authService, { accessToken, _, _ ->
            api.userPlaylists(key = clientId, accessToken = accessToken!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
        })
    }
}