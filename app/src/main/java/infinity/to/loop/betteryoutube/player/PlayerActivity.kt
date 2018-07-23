package infinity.to.loop.betteryoutube.player

import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.common.eventbus.EventBus
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityPlayerBinding
import infinity.to.loop.betteryoutube.persistance.CurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.FirebaseDb
import infinity.to.loop.betteryoutube.utils.newLocationAnimator
import infinity.to.loop.betteryoutube.utils.scaleAnimatorX
import infinity.to.loop.betteryoutube.utils.scaleAnimatorY
import javax.inject.Inject
import javax.inject.Named

class PlayerActivity : DaggerAppCompatActivity(), ViewTreeObserver.OnGlobalLayoutListener,
        YouTubePlayer.OnInitializedListener {

    @Inject @Named("clientID") lateinit var clientID: String
    @Inject lateinit var viewModel: PlayerViewModel
    @Inject lateinit var playerProvider: CustomYouTubePlayer
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var firebase: FirebaseDb

    private lateinit var binding: ActivityPlayerBinding

    private var mCurrentX: Int = 0
    private var mCurrentY: Int = 0

    private var mDx: Int = 0
    private var mDy: Int = 0

    private lateinit var minimizeBtnLocationAnimator: ObjectAnimator
    private lateinit var minimizeScaleXAnimator: ObjectAnimator
    private lateinit var minimizeScaleYAnimator: ObjectAnimator

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
        setWindowParams(MATCH_PARENT, MATCH_PARENT)

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, playerProvider)
                .commit()

        playerProvider.initialize(clientID, this)

//        binding.root.viewTreeObserver.addOnGlobalLayoutListener(this)

        viewModel.minimize.observe(this, Observer {
            val windowSize = resources.getDimension(R.dimen.youtube_player_size_1).toInt()
            setWindowParams(windowSize, windowSize)
            binding.minimizeBtn.visibility = View.GONE
            binding.floatingBtn.visibility = View.GONE
        })
        viewModel.menu.observe(this, Observer {
            minimizeBtnLocationAnimator.start()
            minimizeScaleXAnimator.start()
            minimizeScaleYAnimator.start()
        })
    }


    override fun onResume() {
        super.onResume()

        currentlyPlaying = intent.getParcelableExtra(CurrentlyPlaying.TAG)

        if (player != null && player?.isPlaying!!) {
            finish()
            PlayerActivity.start(this, currentlyPlaying)
        }
    }

//    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
//        eventBus.post(Pair(keyCode, event))
//        return false
//    }

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

    override fun onGlobalLayout() {
//        createAnimators()
        binding.minimizeBtn.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private fun createAnimators() {
        minimizeBtnLocationAnimator = newLocationAnimator(binding.minimizeBtn, 2f)
        minimizeScaleXAnimator = scaleAnimatorX(binding.minimizeBtn, 1.5f)
        minimizeScaleYAnimator = scaleAnimatorY(binding.minimizeBtn, 1.5f)
    }

    private fun setWindowParams(width: Int, height: Int) {
        val params = window.attributes
        params.dimAmount = 0f
        params.flags = FLAG_LAYOUT_NO_LIMITS or FLAG_NOT_TOUCH_MODAL
        params.width = width
        params.height = height
        window.attributes = params
    }

    private fun maybeMoveWindow(ev: MotionEvent): Boolean {
        if (viewModel.minimize.value != null && viewModel.minimize.value == true)
            when (ev.action.and(MotionEvent.ACTION_MASK)) {
                MotionEvent.ACTION_DOWN -> {
                    mDx = mCurrentX - ev.rawX.toInt()
                    mDy = mCurrentY - ev.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val params = window.attributes

                    mCurrentX = (ev.rawX + mDx).toInt()
                    mCurrentY = (ev.rawY + mDy).toInt()

                    params.x = mCurrentX
                    params.y = mCurrentY
                    window.attributes = params
                    return true
                }
            }
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return maybeMoveWindow(ev)
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



