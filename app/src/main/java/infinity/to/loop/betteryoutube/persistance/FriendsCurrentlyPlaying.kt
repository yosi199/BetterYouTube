package infinity.to.loop.betteryoutube.persistance

import com.google.api.services.youtube.model.SubscriptionSnippet

data class FriendsCurrentlyPlaying(val subscriptionSnippet: List<SubscriptionSnippet>,
                                   val currentlyPlaying: List<CurrentlyPlaying>)