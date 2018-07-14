package infinity.to.loop.betteryoutube.home

import android.app.Fragment
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.KeyEvent.KEYCODE_DEL
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.api.services.youtube.YouTube
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityHomeBinding
import infinity.to.loop.betteryoutube.home.feed.FeedFragment
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import infinity.to.loop.betteryoutube.persistance.FirebaseDb
import infinity.to.loop.betteryoutube.persistance.YouTubeDataManager
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


class HomeActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ViewTreeObserver.OnGlobalFocusChangeListener, View.OnFocusChangeListener {


    @Inject lateinit var viewModel: HomeViewModel
    @Inject lateinit var playlistFragment: PlaylistFragment
    @Inject lateinit var feedFragment: FeedFragment
    @Inject lateinit var eventBus: EventBus

    private lateinit var binding: ActivityHomeBinding
    private var keyBuffer = StringBuilder()
    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Setup menu
        val playlistsMenuItem = binding.navView.menu.findItem(R.id.menu_my_playlists)
        onNavigationItemSelected(playlistsMenuItem)

        // Setup toolbar
        setToolbar()
        eventBus.register(this)

        // Load data
        viewModel.loadChannels()
        viewModel.loadSubscriptions()

        binding.searchBar.viewTreeObserver.addOnGlobalFocusChangeListener(this)
        binding.searchBar.setOnQueryTextFocusChangeListener(this)
        binding.searchBar.layoutParams = Toolbar.LayoutParams(Gravity.END)
        binding.searchBar.setOnClickListener {
            Toast.makeText(this, "Touched", Toast.LENGTH_SHORT).show()
            binding.searchBar.isIconified = false
            showKeybaord(binding.searchBar)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        viewModel.openDrawer.observe(this, Observer {
            it?.let { if (it) binding.drawer.openDrawer(Gravity.START) }
        })
    }

    @Subscribe
    fun onKeyEvent(event: Pair<Int, KeyEvent>) {
        onKeyUp(event.first, event.second)
        if (event.second.keyCode == KEYCODE_DEL) {
            keyBuffer = StringBuilder(keyBuffer.removeRange(keyBuffer.length - 1, keyBuffer.length))
        } else {
            keyBuffer.append(event.second.unicodeChar.toChar())
        }
        binding.searchBar.setQuery(keyBuffer.toString(), false)
        Log.d(HomeActivity::class.java.name, event.toString())
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

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        v.let {
            val view = it!!.findViewById<EditText>(R.id.search_src_text)
            if (hasFocus) {
                showKeybaord(view)
            } else {
                hideKeyboard(view)
            }
        }
    }

    override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
        Log.d(HomeActivity::class.java.name, "FOCUS: " + newFocus.toString())
    }

    fun interceptsSearchQuery(listener: OnQueryTextListener) {
        binding.searchBar.setOnQueryTextListener(listener)
    }

    fun showKeybaord(v: View) {
        v.requestFocus()
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun hideKeyboard(v: View) {
        inputMethodManager.hideSoftInputFromInputMethod(v.windowToken, 0)
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
                          @Named("clientID") clientID: String,
                          youTube: YouTube,
                          handler: Handler,
                          state: Provider<AuthState>,
                          service: AuthorizationService,
                          firebaseDb: FirebaseDb,
                          youTubeDataManager: YouTubeDataManager): HomeViewModel {
                return HomeViewModel(sharedPrefs,
                        youTube,
                        clientID,
                        handler,
                        state,
                        service,
                        firebaseDb,
                        youTubeDataManager)
            }
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



