package infinity.to.loop.betteryoutube.home

import android.app.Fragment
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v7.widget.Toolbar.LayoutParams
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
import infinity.to.loop.betteryoutube.home.feed.FeedFragment
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject


class HomeActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject lateinit var viewModel: HomeViewModel
    @Inject lateinit var playlistFragment: PlaylistFragment
    @Inject lateinit var feedFragment: FeedFragment

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel

        val myPlaylistsMenuItem = binding.navView.menu.findItem(R.id.menu_my_playlists)
        onNavigationItemSelected(myPlaylistsMenuItem)

        setToolbar()

        binding.searchBar.layoutParams = LayoutParams(Gravity.END)
        binding.searchBar.setOnClickListener { Toast.makeText(this, "Touched", Toast.LENGTH_SHORT).show() }
        binding.navView.setNavigationItemSelectedListener(this)

        viewModel.openDrawer.observe(this, Observer {
            it?.let { if (it) binding.drawer.openDrawer(Gravity.START) }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        binding.drawer.closeDrawers()
        when (item.itemId) {
            R.id.menu_my_playlists -> moveToFragment(playlistFragment, item.toString())
            R.id.menu_library -> moveToFragment(feedFragment, item.toString())
        }
        return true
    }

    private fun moveToFragment(fragment: Fragment, title: String) {
        val maybeAlreadyAdded = fragmentManager.findFragmentByTag(fragment::class.java.name)
        if (maybeAlreadyAdded == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragment::class.java.name)
                    .commit()
            setTitle(title)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu)
            this.show()
        }
    }

    fun interceptsSearchQuery(listener: OnQueryTextListener) {
        binding.searchBar.setOnQueryTextListener(listener)
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

        @Provides
        fun feedFragment(): FeedFragment = FeedFragment.newInstance()

    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<HomeActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<HomeActivity>()
    }
}



