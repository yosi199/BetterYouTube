package infinity.to.loop.betteryoutube.persistance

import com.google.api.services.youtube.model.SubscriptionListResponse
import com.google.api.services.youtube.model.SubscriptionSnippet

class YouTubeDataManager {

    var channels: SubscriptionListResponse? = null
    var channelsSnippet: List<SubscriptionSnippet>? = null
}