package infinity.to.loop.betteryoutube.home.feed

import android.arch.lifecycle.MutableLiveData
import com.google.api.services.youtube.model.SubscriptionSnippet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import infinity.to.loop.betteryoutube.home.playlists.PlaylistActionListener
import infinity.to.loop.betteryoutube.persistance.CurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.FriendsCurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.YouTubeDataManager
import javax.inject.Inject

class FeedViewModel @Inject constructor(private val youTubeDataManager: YouTubeDataManager) : ValueEventListener, PlaylistActionListener<CurrentlyPlaying> {

    val loadFeed = MutableLiveData<FriendsCurrentlyPlaying>()
    val feedItemClicked = MutableLiveData<Pair<CurrentlyPlaying, Int>>()

    override fun onCancelled(error: DatabaseError) {
    }

    override fun onDataChange(data: DataSnapshot) {
        val snippetsList = ArrayList<SubscriptionSnippet>()
        val currentlyPlayingList = ArrayList<CurrentlyPlaying>()

        // We need to combine data from the firebase db changes AND the youtube api friend's list
        // so we can display who is playing (from the youtube data) and what he is playing (from the firebase data)
        data.children.forEach {
            val userIdKey = it.key
            val map = it.value

            val userSnippet = youTubeDataManager.getFriend(userIdKey!!)

            if (userSnippet != null) {
                snippetsList.add(userSnippet)

                val userCurrentlyPlaying = with(map as HashMap<String, String>) {
                    CurrentlyPlaying(this["videoId"]!!,
                            this["playlistId"],
                            this["value"]!!,
                            this["thumbnailsUrl"]!!,
                            this["trackTitle"]!!,
                            this["trackAuthor"]!!)
                }

                currentlyPlayingList.add(userCurrentlyPlaying)
            }
        }
        if (currentlyPlayingList.isNotEmpty() && snippetsList.isNotEmpty()) {
            loadFeed.postValue(FriendsCurrentlyPlaying(snippetsList, currentlyPlayingList))
        }
    }

    override fun clickedItem(item: CurrentlyPlaying, index: Int) {
        feedItemClicked.postValue(Pair(item, index))
    }
}
