import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import dagger.Module
import dagger.Provides
import infinity.to.loop.betteryoutube.application.App
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
    @Named("googleClientId")
    fun cliendId() = "AIzaSyDV1zMWQL2JYiUM_ey6cl9ijmecKz1a2hk"


    @Provides
    @Singleton
    fun sharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun mainThreadHandler(): Handler = Handler(Looper.getMainLooper())

}