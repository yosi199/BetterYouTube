package infinity.to.loop.betteryoutube.home.feed

import android.arch.lifecycle.MutableLiveData
import com.google.api.services.youtube.model.SubscriptionSnippet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import infinity.to.loop.betteryoutube.persistance.CurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.FriendsCurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.YouTubeDataManager
import javax.inject.Inject

class FeedViewModel @Inject constructor(private val youTubeDataManager: YouTubeDataManager) : ValueEventListener {

    val loadFeed = MutableLiveData<FriendsCurrentlyPlaying>()

    override fun onCancelled(error: DatabaseError) {
    }

    override fun onDataChange(data: DataSnapshot) {
        val snippets = ArrayList<SubscriptionSnippet>()
        val currentlyPlaying = ArrayList<CurrentlyPlaying>()

        // We need to combine data from the firebase db changes AND the youtube api friend's list
        // so we can display who is playing (from the youtube data) and what he is playing (from the firebase data)
        val onlineUsers = data.children.first().child("friends").value as HashMap<String, HashMap<String, CurrentlyPlaying>>
        onlineUsers.forEach { userId, map ->
            val subscriptionSnippet = youTubeDataManager.channelsSnippet?.find { it.resourceId.channelId == userId }
            if (subscriptionSnippet != null) {
                snippets.add(subscriptionSnippet)
                currentlyPlaying.add(map["currentlyPlaying"]!!)
            }
        }
        if (currentlyPlaying.isNotEmpty() && snippets.isNotEmpty()) {
            loadFeed.postValue(FriendsCurrentlyPlaying(snippets, currentlyPlaying))
        }
    }
}