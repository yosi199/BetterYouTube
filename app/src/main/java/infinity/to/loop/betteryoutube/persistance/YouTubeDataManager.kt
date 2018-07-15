package infinity.to.loop.betteryoutube.persistance

import com.google.api.services.youtube.model.SubscriptionListResponse
import com.google.api.services.youtube.model.SubscriptionSnippet

class YouTubeDataManager {

    var channels: SubscriptionListResponse? = null

    /**
     * All the subscription snippets
     */
    var channelsSnippet: List<SubscriptionSnippet>? = null

    /**
     * Map holding the user's subscriptions (which we call "friends") id's as the keys
     * and the values is each "friend's" data snippet.
     */
    private val friendsMap = HashMap<String, SubscriptionSnippet>()

    fun addFriend(id: String, snippet: SubscriptionSnippet) {
        friendsMap[id] = snippet
    }

    /**
     * Returns the requested id subscription snippet if it's available. Null otherwise
     */
    fun getFriend(id: String): SubscriptionSnippet? = friendsMap[id]
}