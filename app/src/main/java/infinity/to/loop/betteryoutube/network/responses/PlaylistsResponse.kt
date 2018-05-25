package infinity.to.loop.betteryoutube.network.responses

import com.google.api.client.util.DateTime

data class PlaylistsResponse(val kind: String?,
                             val etag: String?,
                             val nextPageToken: String?,
                             val prevPageToken: String?,
                             val pageInfo: PageInfo?,
                             val items: List<Playlist>?)

data class PageInfo(val totalResults: Int?, val resultsPerPage: Int?)

data class Playlist(val kind: String?,
                    val etag: String?,
                    val id: String?,
                    val snippet: Snippet?,
                    val status: Status?,
                    val contentDetails: ContentDetails?,
                    val player: Player?,
                    val localization: Map<String, Localized>?)

data class Snippet(val publishedAt: DateTime?,
                   val channelId: String?,
                   val title: String?,
                   val description: String?,
                   val thumbnails: Map<String, Thumbnails>?,
                   val channelTitle: String?,
                   val tags: List<String>?,
                   val defaultLanguage: String?,
                   val localized: Localized?)

data class Thumbnails(val url: String?,
                      val width: Int?,
                      val height: Int?)

data class Localized(val title: String?, val description: String?)

data class Status(val privacyStatus: String?)

data class ContentDetails(val itemCount: Int?)

data class Player(val embedHtml: String?)