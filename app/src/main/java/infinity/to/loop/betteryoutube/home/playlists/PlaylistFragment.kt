package infinity.to.loop.betteryoutube.home.playlists

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.common.eventbus.EventBus
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.application.App
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.FragPlaylistBinding
import infinity.to.loop.betteryoutube.network.endpoints.YouTubeApi
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class PlaylistFragment : dagger.android.DaggerFragment() {

    @Inject lateinit var viewModel: PlaylistViewModel

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.frag_playlist, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val binding = DataBindingUtil.setContentView<FragPlaylistBinding>(activity, R.layout.frag_playlist)
        binding.viewModel = viewModel
        viewModel.playlistsUpdate.observe(activity as AppCompatActivity, Observer {
            it?.let {
                binding.playlistList.layoutManager = LinearLayoutManager(activity)
                binding.playlistList.adapter = PlaylistAdapter(it)
            }
        })
    }

    @dagger.Module()
    class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(app: App,
                          eventBus: EventBus,
                          youTubeApi: YouTubeApi,
                          @Named("clientID") clientID: String,
                          sharedPrefs: SharedPreferences,
                          authState: Provider<AuthState?>,
                          authService: AuthorizationService): PlaylistViewModel {

                return PlaylistViewModel(app,
                        youTubeApi,
                        clientID,
                        sharedPrefs,
                        authState,
                        authService)
            }
        }
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<PlaylistFragment> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<PlaylistFragment>()
    }

}