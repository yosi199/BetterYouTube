package infinity.to.loop.betteryoutube.home

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.SubscriptionListResponse
import infinity.to.loop.betteryoutube.home.playlists.PlaylistActionListener
import infinity.to.loop.betteryoutube.persistance.FirebaseDb
import infinity.to.loop.betteryoutube.persistance.SharedPreferenceKeys
import infinity.to.loop.betteryoutube.persistance.YouTubeDataManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Provider

class HomeViewModel @Inject constructor(private val sharedPrefs: SharedPreferences,
                                        private val api: YouTube,
                                        private val clientId: String,
                                        private val handler: Handler,
                                        private val state: Provider<AuthState>,
                                        private val service: AuthorizationService,
                                        private val firebaseDb: FirebaseDb,
                                        private val youTubeDataManager: YouTubeDataManager) : PlaylistActionListener<SearchResult> {

    val openDrawer = MutableLiveData<Boolean>()
    val searchResults = MutableLiveData<SearchListResponse>()
    val searchItemClicked = MutableLiveData<SearchResult>()

    companion object {
        val TAG = HomeViewModel::class.java.name
    }

    fun onResume() {
        maybeFirstTime()
    }

    fun loadChannels() {
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            val request = api.channels().list("id, snippet")
            request.key = clientId
            request.mine = true
            request.oauthToken = accessToken

            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val id = it.items[0]["id"] as String
                        firebaseDb.setUserId(id)
                    }, {
                        Log.e(TAG, "Couldn't fetch channel info ${it.message}")
                    })
        }
    }

    /**
     * Get's the user's subscriptions (mine - indicating we should retrieve channels that I as the user follow)
     *
     */
    fun loadSubscriptions() {
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            val request = api.subscriptions().list("id, snippet")
            request.key = clientId
            request.mine = true
            request.oauthToken = accessToken
            request.maxResults = 50

            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        youTubeDataManager.channels = it
                        youTubeDataManager.channelsSnippet = it.items.map { it.snippet }
                        addFriends(it)
                    }, {
                        Log.e(TAG, "Couldn't fetch channel info ${it.message}")
                    })
        }
    }

    private fun addFriends(subscriptionList: SubscriptionListResponse) {
        subscriptionList.items.forEach {
            val id = it.snippet.resourceId.channelId
            val snippet = it.snippet
            youTubeDataManager.addFriend(id, snippet)
        }
    }

    private fun maybeFirstTime() {
        val firstTime = sharedPrefs.getBoolean(SharedPreferenceKeys.firstTime, false)
        handler.postDelayed({ openDrawer.postValue(firstTime) }, 1500)
        sharedPrefs.edit().putBoolean(SharedPreferenceKeys.firstTime, false).apply()
    }

    fun search(text: String) {
        state.get()?.performActionWithFreshTokens(service) { accessToken, _, _ ->
            val request = api.search().list("id, snippet")
            request.q = text
            request.key = clientId
            request.oauthToken = accessToken
            request.maxResults = 50

            Single.just(request)
                    .subscribeOn(Schedulers.io())
                    .map { request.execute() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        searchResults.postValue(it)
                    }, {
                        Log.e(TAG, "Couldn't find search query ${it.message}")
                    })
        }
    }

    override fun clickedItem(item: SearchResult, index: Int) {
        searchItemClicked.postValue(item)
    }
}