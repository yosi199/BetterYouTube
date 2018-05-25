package infinity.to.loop.betteryoutube.home.playlists

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import infinity.to.loop.betteryoutube.network.endpoints.YouTubeApi
import infinity.to.loop.betteryoutube.network.responses.PlaylistsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Provider

class PlaylistViewModel @Inject constructor(private val context: Context,
                                            private val api: YouTubeApi,
                                            private val clientId: String,
                                            private val sharedPreferences: SharedPreferences,
                                            private val authState: Provider<AuthState?>,
                                            private val authService: AuthorizationService) {

    val playlistsUpdate = MutableLiveData<PlaylistsResponse>()

    init {
        getUserPlaylist()
    }

    private fun getUserPlaylist() {
        authState.get()?.performActionWithFreshTokens(authService, { accessToken, _, _ ->
            api.userPlaylists(key = clientId, accessToken = accessToken!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        playlistsUpdate.postValue(it)
                    }, {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
        })
    }
}