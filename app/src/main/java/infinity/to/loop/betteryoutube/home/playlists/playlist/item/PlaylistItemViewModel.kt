package infinity.to.loop.betteryoutube.home.playlists.playlist.item

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.api.services.youtube.YouTube
import infinity.to.loop.betteryoutube.home.playlists.PlaylistActionListener
import infinity.to.loop.betteryoutube.home.playlists.RequestInfo
import infinity.to.loop.betteryoutube.persistance.YouTubeDataManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Provider

class PlaylistItemViewModel @Inject constructor(private val api: YouTube,
                                                private val clientId: String,
                                                private val youTubeDataManager: YouTubeDataManager,
                                                private val state: Provider<AuthState?>,
                                                private val service: AuthorizationService) : PlaylistActionListener<TrackItemInfo>, RequestInfo<TrackItemInfo> {

    val playlistUpdate = MutableLiveData<MutableList<TrackItemInfo>>()
    val trackSelection = MutableLiveData<Pair<TrackItemInfo, Int>>()
    val statsUpdate = MutableLiveData<Pair<TrackItemInfo, Int>>()

    private val trackItemInfoList = mutableListOf<TrackItemInfo>()

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
                    .map {
                        youTubeDataManager.lastPlayItemListResponse = it
                        it.items.forEach {
                            trackItemInfoList.add(TrackItemInfo(it))
                        }
                        return@map trackItemInfoList
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        playlistUpdate.postValue(it)
                    },
                            { Log.e(TAG, "Couldn't fetch playlist ${it.message}") })
        }
    }

    override fun retrieveStatsFor(value: TrackItemInfo, index: Int) {
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            val request = api.videos().list("snippet,contentDetails,statistics")
            request.key = clientId
            request.oauthToken = accessToken
            request.id = value.item.snippet.resourceId.videoId
            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val views = it.items[0].statistics.viewCount
                        val comments = it.items[0].statistics.commentCount
                        val likes = it.items[0].statistics.likeCount
                        value.views = formatString(views)
                        value.comments = formatString(comments)
                        value.likes = formatString(likes)
                        statsUpdate.postValue(Pair(value, index))
                    }, {})
        }
    }

    private fun formatString(value: BigInteger): String {
        return if (value < BigInteger.valueOf(1000)) {
            value.toString()
        } else {
            value.divide(BigInteger.valueOf(1000)).toString() + "k"
        }
    }

    override fun clickedItem(item: TrackItemInfo, index: Int) {
        trackSelection.postValue(Pair(item, index))
    }
}