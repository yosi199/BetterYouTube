package infinity.to.loop.betteryoutube.player

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityPlayerBinding
import infinity.to.loop.betteryoutube.persistance.CurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.FirebaseDb
import javax.inject.Inject
import javax.inject.Named

class PlayerActivity : DaggerAppCompatActivity(),
        YouTubePlayer.OnInitializedListener {

    @Inject @Named("clientID") lateinit var clientID: String
    @Inject lateinit var viewModel: PlayerViewModel
    @Inject lateinit var playerProvider: CustomYouTubePlayer
    @Inject lateinit var firebase: FirebaseDb

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var currentlyPlaying: CurrentlyPlaying
    private var player: YouTubePlayer? = null

    companion object {
        fun start(context: Context, currentlyPlaying: CurrentlyPlaying) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.flags = FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_NO_HISTORY
            intent.putExtra(CurrentlyPlaying.TAG, currentlyPlaying)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        binding.viewModel = viewModel

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, playerProvider)
                .commit()

        playerProvider.initialize(clientID, this)
    }


    override fun onResume() {
        super.onResume()

        currentlyPlaying = intent.getParcelableExtra(CurrentlyPlaying.TAG)

        if (player != null && player?.isPlaying!!) {
            finish()
            PlayerActivity.start(this, currentlyPlaying)
        }
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        player?.let {
            this@PlayerActivity.player = player
            firebase.updateCurrentlyPlaying(currentlyPlaying)
            currentlyPlaying.playlistId?.let {
                player.loadPlaylist(currentlyPlaying.playlistId, currentlyPlaying.value.toInt(), 0)
                return
            }
            player.loadVideo(currentlyPlaying.videoId)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, error: YouTubeInitializationResult?) {
        Log.e("ERROR", error.toString())
        error?.getErrorDialog(this@PlayerActivity, 201)?.show()
    }

    @dagger.Module()
    class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(): PlayerViewModel = PlayerViewModel()
        }

        @Provides
        fun clientId(@Named("clientID") clientID: String) = clientID
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<PlayerActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<PlayerActivity>()
    }
}



