package infinity.to.loop.betteryoutube

import dagger.Module
import dagger.android.ContributesAndroidInjector
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.home.HomeActivity
import infinity.to.loop.betteryoutube.home.feed.FeedFragment
import infinity.to.loop.betteryoutube.home.playlists.PlaylistFragment
import infinity.to.loop.betteryoutube.home.playlists.playlist.item.PlaylistItemFragment
import infinity.to.loop.betteryoutube.main.MainActivity
import infinity.to.loop.betteryoutube.player.PlayerActivity


@Module
abstract class ActivitiesBindingModule {

    // Activities
    @ContributesAndroidInjector(modules = [MainActivity.Module::class, AuthConfigurationModule::class])
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [HomeActivity.Module::class, AuthConfigurationModule::class])
    abstract fun homeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [PlayerActivity.Module::class, AuthConfigurationModule::class])
    abstract fun playerActivity(): PlayerActivity

    // Fragments
    @ContributesAndroidInjector(modules = [PlaylistFragment.Module::class, AuthConfigurationModule::class])
    abstract fun playlistsFragment(): PlaylistFragment

    @ContributesAndroidInjector(modules = [PlaylistItemFragment.Module::class, AuthConfigurationModule::class])
    abstract fun playlistItemFragment(): PlaylistItemFragment

    @ContributesAndroidInjector(modules = [FeedFragment.Module::class, AuthConfigurationModule::class])
    abstract fun feedFragment(): FeedFragment

}
