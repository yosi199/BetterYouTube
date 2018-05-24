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
import infinity.to.loop.betteryoutube.application.App
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityHomeBinding
import infinity.to.loop.betteryoutube.network.interceptor.endpoints.YouTubeApi
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject
import javax.inject.Named


class HomeActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var youTubePlayerFragment: YouTubePlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
        viewModel.setIntent(intent)
        binding.viewModel = viewModel

        fragmentManager
                .beginTransaction()
                .replace(R.id.youtube_container, youTubePlayerFragment)
                .commit()
    }

    @dagger.Module()
    abstract class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(app: App,
                          youTubeApi: YouTubeApi,
                          @Named("clientID") clientID: String,
                          configuration: AuthorizationServiceConfiguration,
                          authState: AuthState,
                          sharedPrefs: SharedPreferences,
                          authService: AuthorizationService): HomeViewModel {
                return HomeViewModel(app, youTubeApi, clientID, configuration, authState, sharedPrefs, authService)
            }

            @JvmStatic
            @Provides
            fun youTubeFragment(): YouTubePlayerFragment = YouTubePlayerFragment.newInstance()
        }
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<HomeActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<HomeActivity>()

    }

}



