package infinity.to.loop.betteryoutube.home.feed

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    companion object {
        fun newInstance() = FeedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_feed, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
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