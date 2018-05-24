package infinity.to.loop.betteryoutube.network.interceptor.endpoints

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    @GET("playlists")
    fun userPlaylists(@Query("part") part: String = "snippet",
                      @Query("mine") mine: Boolean = true,
                      @Query("key") key: String,
                      @Query("access_token") accessToken: String): Single<String>
}