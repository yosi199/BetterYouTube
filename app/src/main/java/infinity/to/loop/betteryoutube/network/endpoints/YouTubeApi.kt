package infinity.to.loop.betteryoutube.network.endpoints

import infinity.to.loop.betteryoutube.network.responses.PlaylistsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    @GET("playlists")
    fun userPlaylists(@Query("part") part: String = "snippet",
                      @Query("mine") mine: Boolean = true,
                      @Query("key") key: String,
                      @Query("access_token") accessToken: String): Single<PlaylistsResponse>
}