package infinity.to.loop.betteryoutube.home.playlists.playlist.item

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.PlaylistItemListResponse
import com.google.api.services.youtube.model.VideoListResponse
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
                                                private val service: AuthorizationService) : PlaylistActionListener<PlaylistItem> {

    val playlistUpdate = MutableLiveData<PlaylistItemListResponse>()
    val statsUpdate = MutableLiveData<VideoListResponse>()
    val trackSelection = MutableLiveData<Pair<PlaylistItem, Int>>()

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
                        loadStatistics(it)
                    }, {
                        Log.e(TAG, "Couldn't fetch playlist ${it.message}")
                    })
        }
    }

    fun loadStatistics(it: PlaylistItemListResponse) {
        // Todo - not effective code! need to find a way to get all the info at once and then load the list
        val itemsId = it.items.map { it.snippet.resourceId }
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            itemsId.forEach {
                val request = api.videos().list("snippet,contentDetails,statistics")
                request.key = clientId
                request.oauthToken = accessToken
                request.id = it.videoId

                Single.just(request)
                        .subscribeOn(Schedulers.io())
                        .map { request.execute() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            statsUpdate.postValue(it)
                        }, {})

            }

        }
    }

    override fun clickedItem(item: PlaylistItem, index: Int) {
        trackSelection.postValue(Pair(item, index))
    }
}