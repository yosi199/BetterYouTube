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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.api.services.youtube.YouTube
import com.google.common.base.Strings
import com.google.common.eventbus.EventBus
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityHomeBinding
import infinity.to.loop.betteryoutube.home.feed.FeedFragment
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import infinity.to.loop.betteryoutube.home.search.SearchAdapter
import infinity.to.loop.betteryoutube.persistance.CurrentlyPlaying
import infinity.to.loop.betteryoutube.persistance.FirebaseDb
import infinity.to.loop.betteryoutube.persistance.YouTubeDataManager
import infinity.to.loop.betteryoutube.player.PlayerActivity
import infinity.to.loop.betteryoutube.utils.heightAnimator
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


class HomeActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        ViewTreeObserver.OnGlobalFocusChangeListener,
        View.OnFocusChangeListener,
        SearchView.OnQueryTextListener {

    @Inject lateinit var viewModel: HomeViewModel
    @Inject lateinit var playlistFragment: PlaylistFragment
    @Inject lateinit var feedFragment: FeedFragment
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var firebaseDb: FirebaseDb

    private lateinit var binding: ActivityHomeBinding
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var searchAdapter: SearchAdapter
    private var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.viewModel = viewModel
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        searchAdapter = SearchAdapter(viewModel)

        // Setup menu
        val playlistMenuItem = binding.navView.menu.findItem(R.id.menu_my_playlists)
        onNavigationItemSelected(playlistMenuItem)

        // Setup toolbar
        setToolbar()
        eventBus.register(this)

        // Load data
        viewModel.loadChannels()
        viewModel.loadSubscriptions()

        binding.searchBar.viewTreeObserver.addOnGlobalFocusChangeListener(this)
        binding.searchBar.setOnQueryTextListener(this)
        binding.searchBar.layoutParams = Toolbar.LayoutParams(Gravity.END)
        binding.navView.setNavigationItemSelectedListener(this)

        viewModel.openDrawer.observe(this, Observer {
            it?.let { if (it) binding.drawer.openDrawer(Gravity.START) }
        })

        viewModel.searchResults.observe(this, Observer {
            binding.searchResultsList.layoutManager = LinearLayoutManager(this)
            binding.searchResultsList.adapter = searchAdapter
            searchAdapter.addData(it!!)

            if (height == 0) {
                height = binding.searchBarDropLayout.height
            }
            // Todo - This sucks and very slow
            // Todo - Don't allocate new animator in every query!
            // Todo - Don't redraw the animation for every query - throttle it down
            synchronized(Any()) {
                val animator = heightAnimator(height)

                if (!animator.isStarted && !animator.isRunning) {
                    animator.addUpdateListener {
                        val params = binding.searchBarDropLayout.layoutParams
                        params.height = it.animatedValue as Int
                        binding.searchBarDropLayout.layoutParams = params
                    }
                    animator.start()
                }
            }
        })

        viewModel.searchItemClicked.observe(this, Observer {
            it?.let {
                PlayerActivity.start(this, CurrentlyPlaying(it.id.videoId,
                        null,
                        0.toString(),
                        it.snippet.thumbnails.high.url,
                        it.snippet.title,
                        it.snippet.description))
            }
        })
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        binding.searchBarDropLayout.y = binding.searchBar.y + 100
        binding.searchBarDropLayout.visibility = View.VISIBLE
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (Strings.isNullOrEmpty(newText)) {
            binding.searchBarDropLayout.visibility = View.GONE
        } else {
            if (binding.searchBarDropLayout.visibility != View.VISIBLE) {
                binding.searchBarDropLayout.y = binding.searchBar.y + 100
                binding.searchBarDropLayout.visibility = View.VISIBLE
            }
            viewModel.search(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseDb.removeSelf()
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



