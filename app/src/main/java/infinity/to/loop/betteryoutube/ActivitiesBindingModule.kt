package infinity.to.loop.betteryoutube

import dagger.Module
import dagger.android.ContributesAndroidInjector
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.home.HomeActivity
import infinity.to.loop.betteryoutube.main.MainActivity


@Module
abstract class ActivitiesBindingModule {

    @ContributesAndroidInjector(modules = [MainActivity.Module::class, AuthConfigurationModule::class])
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [HomeActivity.Module::class, AuthConfigurationModule::class])
    abstract fun homeActivity(): HomeActivity

}
