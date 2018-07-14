package infinity.to.loop.betteryoutube.home.playlists.playlist.item

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItemListResponse
import infinity.to.loop.betteryoutube.home.playlists.PlaylistActionListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Provider

class PlaylistItemViewModel @Inject constructor(private val context: Context,
                                                private val api: YouTube,
                                                private val clientId: String,
                                                private val sharedPreferences: SharedPreferences,
                                                private val state: Provider<AuthState?>,
                                                private val service: AuthorizationService) : PlaylistActionListener {

    val playlistUpdate = MutableLiveData<PlaylistItemListResponse>()
    val trackSelection = MutableLiveData<Pair<String, Int>>()

    companion object {
        val TAG = PlaylistItemViewModel::class.java.name
    }

    fun load(playlistId: String) {
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            val request = api.playlistItems().list("snippet,contentDetails")
            request.playlistId = playlistId
            request.key = clientId
            request.maxResults = 50
            request.oauthToken = accessToken

            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        playlistUpdate.postValue(it)
                    }, {
                        Log.e(TAG, "Couldn't fetch playlist ${it.message}")
                    })
        }
    }

    override fun clickedItem(id: String, index: Int) {
        trackSelection.postValue(Pair(id, index))
    }
}