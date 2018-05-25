package infinity.to.loop.betteryoutube.home

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.youtube.player.YouTubePlayerFragment
import com.google.common.eventbus.EventBus
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.application.App
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityHomeBinding
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject
import javax.inject.Named

class HomeActivity : DaggerAppCompatActivity() {

    @Inject private lateinit var viewModel: HomeViewModel
    @Inject private lateinit var youTubePlayerFragment: YouTubePlayerFragment
    @Inject private lateinit var playlistFragment: PlaylistFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
        viewModel.setIntent(intent)
        binding.viewModel = viewModel

        viewModel.authenticated.observe(this, Observer {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.youtube_container, youTubePlayerFragment)
                    .replace(R.id.content_container, playlistFragment)
                    .commit()
        })
    }

    @dagger.Module()
    class Module {

        @Provides
        fun playlistFragment(): PlaylistFragment = PlaylistFragment.newInstance()

        @Provides
        fun youTubeFragment(): YouTubePlayerFragment = YouTubePlayerFragment.newInstance()

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(app: App,
                          eventBus: EventBus,
                          youTubeApi: YouTubeApi,
                          @Named("clientID") clientID: String,
                          configuration: AuthorizationServiceConfiguration,
                          authState: AuthState,
                          sharedPrefs: SharedPreferences,
                          authService: AuthorizationService): HomeViewModel {

                return HomeViewModel(app,
                        eventBus,
                        youTubeApi,
                        clientID,
                        configuration,
                        authState,
                        sharedPrefs,
                        authService)
            }
        }
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<HomeActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<HomeActivity>()
    }
}



