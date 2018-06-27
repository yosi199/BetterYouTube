package infinity.to.loop.betteryoutube.player

import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityPlayerBinding
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class PlayerActivity : DaggerAppCompatActivity(), ViewTreeObserver.OnGlobalLayoutListener {

    @Inject @Named("clientID") lateinit var clientID: String
    @Inject lateinit var viewModel: PlayerViewModel
    @Inject lateinit var playerProvider: Provider<CustomYouTubePlayer>

    private lateinit var binding: ActivityPlayerBinding

    private var mCurrentX: Int = 0
    private var mCurrentY: Int = 0

    private var mDx: Int = 0
    private var mDy: Int = 0

    companion object {
        fun start(context: Context, video: String) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REORDER_TO_FRONT
            intent.putExtra("video", video);
            context.startActivity(intent)
        }
    }

    private lateinit var minimizeBtnLocationAnimator: ObjectAnimator
    private lateinit var minimizeScaleXAnimator: ObjectAnimator
    private lateinit var minimizeScaleYAnimator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        binding.viewModel = viewModel
        setWindowParams(MATCH_PARENT, MATCH_PARENT)

        val video = intent.getStringExtra("video")
        val fragment = playerProvider.get()

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

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(this)

        viewModel.minimize.observe(this, Observer { setWindowParams(600, 600) })
        viewModel.menu.observe(this, Observer {
            minimizeBtnLocationAnimator.start()
            minimizeScaleXAnimator.start()
            minimizeScaleYAnimator.start()
        })
    }

    override fun onGlobalLayout() {
        minimizeBtnLocationAnimator = newLocationAnimator(binding.minimizeBtn)
        minimizeScaleXAnimator = scaleAnimatorX(binding.minimizeBtn)
        minimizeScaleYAnimator = scaleAnimatorY(binding.minimizeBtn)
        binding.minimizeBtn.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private fun newLocationAnimator(target: View): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "y", target.y, target.y - (target.height * 2f))
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        return animator
    }

    private fun scaleAnimatorX(target: View): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "scaleX", target.scaleX, target.scaleX * 1.5f)
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        return animator
    }

    private fun scaleAnimatorY(target: View): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(target, "scaleY", target.scaleY, target.scaleY * 1.5f)
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        return animator
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



