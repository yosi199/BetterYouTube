package infinity.to.loop.betteryoutube.home.playlists.playlist.item

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.api.services.youtube.YouTube
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.DaggerFragment
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.application.App
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.FragPlaylistItemBinding
import infinity.to.loop.betteryoutube.home.HomeActivity
import infinity.to.loop.betteryoutube.player.CustomYouTubePlayer
import infinity.to.loop.betteryoutube.player.PlayerActivity
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class PlaylistItemFragment : DaggerFragment() {

    @Inject @Named("clientID") lateinit var clientID: String
    @Inject lateinit var viewModel: PlaylistItemViewModel
    @Inject lateinit var playerProvider: Provider<CustomYouTubePlayer>
    private lateinit var binding: FragPlaylistItemBinding

    companion object {
        const val ARG_KEY_ID: String = "PlaylistID"
        fun newInstance() = PlaylistItemFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val playlistId = arguments.getString(ARG_KEY_ID)
        viewModel.load(playlistId)
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_playlist_item, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        viewModel.playlistUpdate.observe(activity as HomeActivity, Observer {
            it?.let {
                binding.playlistItemList.layoutManager = LinearLayoutManager(activity)
                binding.playlistItemList.adapter = SpecificPlaylistAdapter(viewModel)
                (binding.playlistItemList.adapter as SpecificPlaylistAdapter).addData(it)
            }
        })

        viewModel.trackSelection.observe(activity as HomeActivity, Observer {
            it?.let { video ->
                PlayerActivity.start(activity, video)
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
                          youTube: YouTube,
                          @Named("clientID") clientID: String,
                          sharedPrefs: SharedPreferences,
                          authState: Provider<AuthState?>,
                          authService: AuthorizationService): PlaylistItemViewModel {

                return PlaylistItemViewModel(app,
                        youTube,
                        clientID,
                        sharedPrefs,
                        authState,
                        authService)
            }
        }

        @Provides
        fun clientId(@Named("clientID") clientID: String) = clientID

    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<PlaylistItemFragment> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<PlaylistItemFragment>()
    }

}
