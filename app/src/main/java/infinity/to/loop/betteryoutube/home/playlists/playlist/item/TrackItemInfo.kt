package infinity.to.loop.betteryoutube.home.playlists.playlist.item

import com.google.api.services.youtube.model.PlaylistItem

data class TrackItemInfo(val item: PlaylistItem,
                         var likes: String? = null,
                         var views: String? = null,
                         var comments: String? = null,
                         var showStats: Boolean = false)