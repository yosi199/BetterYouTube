package infinity.to.loop.betteryoutube.common

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.common.eventbus.EventBus
import dagger.Module
import dagger.Provides
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.network.interceptor.AuthorizationInterceptor
import infinity.to.loop.betteryoutube.player.CustomYouTubePlayer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class LibraryModule {

    @Provides
    @Singleton
    @Named("logging")
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    @Named("authorization")
    fun authInterceptor(preferences: SharedPreferences): AuthorizationInterceptor = AuthorizationInterceptor(preferences)

    @Provides
    @Singleton
    fun okHttp(@Named("logging") interceptor: HttpLoggingInterceptor,
               @Named("authorization") authInterceptor: AuthorizationInterceptor): OkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun objectMapper(): ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    @Provides
    @Singleton
    fun retrofit(client: OkHttpClient, objectMapper: ObjectMapper, context: Context): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(context.getString(R.string.data_api))
                .client(client)
                .build()
    }

    @Provides
    @Singleton
    fun eventBus() = EventBus()

    @Provides
    @Singleton
    fun youTube(): YouTube {
        return YouTube
                .Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer {})
                .build()
    }

    @Provides
    fun youtubePlayerFragment() = CustomYouTubePlayer()
}