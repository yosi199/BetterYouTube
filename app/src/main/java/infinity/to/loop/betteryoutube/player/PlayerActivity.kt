package infinity.to.loop.betteryoutube.player

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityPlayerBinding
import infinity.to.loop.betteryoutube.home.HomeActivity
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class PlayerActivity : DaggerAppCompatActivity(), GestureDetector.OnGestureListener {
    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true

    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return true

    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true

    }

    override fun onLongPress(ev: MotionEvent) {
        Log.d(HomeActivity::class.java.name, "X ${ev.rawX} Y ${ev.rawY}")
        val params = window.attributes
        params.x = ev.x.toInt()
        params.y = ev.y.toInt()
        window.attributes = params

    }

    @Inject @Named("clientID") lateinit var clientID: String
    @Inject lateinit var viewModel: PlayerViewModel
    @Inject lateinit var playerProvider: Provider<CustomYouTubePlayer>

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var gestureDetector: GestureDetector

    companion object {
        fun start(context: Context, video: String) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REORDER_TO_FRONT
            intent.putExtra("video", video);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        binding.viewModel = viewModel
        setWindowParams()

        val video = intent.getStringExtra("video")
        val fragment = playerProvider.get()

        gestureDetector = GestureDetector(this, this)

        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()


        fragment.initialize(clientID, object : YouTubePlayer.OnInitializedListener {

            override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
                player?.let { player.loadVideo(video) }
            }

            override fun onInitializationFailure(provider: YouTubePlayer.Provider?, error: YouTubeInitializationResult?) {
                Log.e("ERROR", error.toString())
                error?.getErrorDialog(this@PlayerActivity, 201)?.show()
            }
        })
    }


    private fun setWindowParams() {
        val params = window.attributes
        params.dimAmount = 0f
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.width = 600
        params.height = 600
        window.attributes = params
    }

//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        Log.d(HomeActivity::class.java.name, "X ${ev.rawX} Y ${ev.rawY}")
//        val params = window.attributes
//        params.x = ev.x.toInt()
//        params.y = ev.y.toInt()
//        window.attributes = params
//        return super.dispatchTouchEvent(ev)
//    }

    override fun onResume() {
        super.onResume()
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



