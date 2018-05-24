package infinity.to.loop.betteryoutube.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityMainBinding
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel

        binding.signIn.animate()
                .alpha(1f)
                .setStartDelay(1000)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start()

    }

    @dagger.Module()
    abstract class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(activity: MainActivity,
                          @Named("clientID") clientID: String,
                          authState: AuthState,
                          authService: AuthorizationService) =
                    MainViewModel(activity, clientID, authState, authService)
        }
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<MainActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<MainActivity>()

    }
}