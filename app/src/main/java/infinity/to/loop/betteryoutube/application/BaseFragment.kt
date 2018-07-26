package infinity.to.loop.betteryoutube.application

import dagger.android.DaggerFragment

open class BaseFragment : DaggerFragment() {

    internal var customTitle = BaseFragment::class.java.name

    fun updateTitle() {
        activity.title = customTitle
    }

}