package infinity.to.loop.betteryoutube.home

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityHomeBinding
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import infinity.to.loop.betteryoutube.player.CustomYouTubePlayer
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject


class HomeActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModel: HomeViewModel
    @Inject lateinit var playlistFragment: PlaylistFragment
    @Inject lateinit var customYouTubePlayer: CustomYouTubePlayer

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel

        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, playlistFragment)
                .commit()

        setSupportActionBar(binding.toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu)
            this.show()
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            binding.drawer.closeDrawers()

            Toast.makeText(this, "Clicked ${menuItem.title}", Toast.LENGTH_SHORT).show()
            true
        }

        viewModel.openDrawer.observe(this, Observer {
            it?.let { if (it) binding.drawer.openDrawer(Gravity.START) }
        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @dagger.Module()
    class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(sharedPrefs: SharedPreferences,
                          handler: Handler,
                          state: AuthState,
                          service: AuthorizationService,
                          configuration: AuthorizationServiceConfiguration): HomeViewModel = HomeViewModel(sharedPrefs, handler, state, service, configuration)
        }

        @Provides
        fun playlistFragment(): PlaylistFragment = PlaylistFragment.newInstance()

    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<HomeActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<HomeActivity>()
    }
}



