package infinity.to.loop.betteryoutube.home.playlists

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.api.services.youtube.YouTube
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.DaggerFragment
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.application.App
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.FragPlaylistBinding
import infinity.to.loop.betteryoutube.home.HomeActivity
import infinity.to.loop.betteryoutube.home.playlists.playlist.item.PlaylistItemFragment
import infinity.to.loop.betteryoutube.utils.fadeAnimation
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


class PlaylistFragment : DaggerFragment(), SearchView.OnQueryTextListener {

    @Inject lateinit var viewModel: PlaylistViewModel
    @Inject lateinit var playlistItemFragment: Provider<PlaylistItemFragment>
    private lateinit var binding: FragPlaylistBinding
    private lateinit var adapter: PlaylistAdapter

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_playlist, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        binding.viewModel = viewModel
        viewModel.playlistUpdate.observe(activity as HomeActivity, Observer {
            it?.let {
                adapter = PlaylistAdapter(it, viewModel)
                binding.playlistList.layoutManager = LinearLayoutManager(activity)
                binding.playlistList.adapter = adapter

                fadeAnimation(binding.playlistList).start()
            }
        })

        viewModel.chosenPlaylistId.observe(activity as HomeActivity, Observer {
            val fragment = playlistItemFragment.get()
            val bundle = Bundle()
            bundle.putString(PlaylistItemFragment.ARG_KEY_ID, it!!.id)
            bundle.putString(PlaylistItemFragment.ARG_KEY_PLAYLIST_NAME, it.snippet.title)
            fragment.arguments = bundle
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, fragment::class.java.name)
                    .addToBackStack(fragment::class.java.name)
                    .commit()

        })
    }

    override fun onResume() {
        super.onResume()
        activity.title = resources.getString(R.string.my_playlists)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { adapter.filter.filter(newText) }
        return true
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
                          authService: AuthorizationService): PlaylistViewModel {

                return PlaylistViewModel(app,
                        youTube,
                        clientID,
                        sharedPrefs,
                        authState,
                        authService)
            }
        }

        @Provides
        fun playlistItemFragment(): PlaylistItemFragment {
            return PlaylistItemFragment.newInstance()
        }
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<PlaylistFragment> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<PlaylistFragment>()
    }

}