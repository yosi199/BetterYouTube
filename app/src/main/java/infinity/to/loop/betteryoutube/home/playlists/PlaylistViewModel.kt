package infinity.to.loop.betteryoutube.home.playlists

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistListResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Provider

class PlaylistViewModel @Inject constructor(private val context: Context,
                                            private val youtube: YouTube,
                                            private val clientId: String,
                                            private val sharedPreferences: SharedPreferences,
                                            private val authState: Provider<AuthState?>,
                                            private val authService: AuthorizationService) : PlaylistActionListener {


    val playlistUpdate = MutableLiveData<PlaylistListResponse>()
    val chosenPlaylistId = MutableLiveData<String>()

    init {
        getUserPlaylist()
    }

    private fun getUserPlaylist() {
        authState.get()?.performActionWithFreshTokens(authService) { accessToken, _, _ ->

            val request = youtube.playlists().list("snippet,contentDetails")
            request.mine = true
            request.key = clientId
            request.oauthToken = accessToken


            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.etag
                        playlistUpdate.postValue(it)
                    }, {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    })

        }
    }

    override fun clickedItem(id: String, index: Int) {
        chosenPlaylistId.postValue(id)
    }
}