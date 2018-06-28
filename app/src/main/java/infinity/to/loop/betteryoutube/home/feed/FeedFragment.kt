package infinity.to.loop.betteryoutube.home.feed

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.DaggerFragment
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.FragFeedBinding
import javax.inject.Inject


class FeedFragment : DaggerFragment() {

    @Inject lateinit var viewModel: FeedViewModel
    private lateinit var binding: FragFeedBinding
    private val auth = FirebaseAuth.getInstance()

    companion object {
        fun newInstance() = FeedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_feed, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        val currentUser = auth.currentUser
        if (currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@FeedFragment.context, "Signed into firebase", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @dagger.Module()
    class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(): FeedViewModel {
                return FeedViewModel()
            }
        }

    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<FeedFragment> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<FeedFragment>()
    }
}