package infinity.to.loop.betteryoutube.home

import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.youtube.player.YouTubePlayerFragment
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityHomeBinding
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject

class HomeActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModel: HomeViewModel
    @Inject lateinit var youTubePlayerFragment: YouTubePlayerFragment
    @Inject lateinit var playlistFragment: PlaylistFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
        binding.viewModel = viewModel

        fragmentManager
                .beginTransaction()
                .replace(R.id.player_container, youTubePlayerFragment)
                .replace(R.id.content_container, playlistFragment)
                .commit()
    }

    @dagger.Module()
    class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(sharedPrefs: SharedPreferences,
                          state: AuthState,
                          service: AuthorizationService,
                          configuration: AuthorizationServiceConfiguration): HomeViewModel = HomeViewModel(sharedPrefs, state, service, configuration)
        }

        @Provides
        fun playlistFragment(): PlaylistFragment = PlaylistFragment.newInstance()

        @Provides
        fun youTubeFragment(): YouTubePlayerFragment = YouTubePlayerFragment.newInstance()
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<HomeActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<HomeActivity>()
    }
}



