import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dagger.Module
import dagger.Provides
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.application.App
import infinity.to.loop.betteryoutube.network.interceptor.AuthorizationInterceptor
import infinity.to.loop.betteryoutube.network.interceptor.endpoints.YouTubeApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun context(app: App): Context = app.applicationContext

    //TODO - security! must use a better obfuscated way to store the Oauth clientID
    @Provides
    @Singleton
    @Named("clientID")
    fun clientID() = "534178296249-im13gnb2tcmb98jfs08jec89dsu50j6e.apps.googleusercontent.com"

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
    fun youtubeApi(retrofit: Retrofit): YouTubeApi = retrofit.create(YouTubeApi::class.java)

    @Provides
    @Singleton
    fun sharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

}