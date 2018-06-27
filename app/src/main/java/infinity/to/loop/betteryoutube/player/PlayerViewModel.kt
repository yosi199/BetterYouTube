package infinity.to.loop.betteryoutube.player

import android.arch.lifecycle.MutableLiveData
import android.view.View
import javax.inject.Inject

class PlayerViewModel @Inject constructor() {

    val minimize = MutableLiveData<Boolean>()
    val menu = MutableLiveData<Boolean>()

    fun minimize(v: View) {
        minimize.postValue(true)
    }

    fun menu(v: View) {
        menu.postValue(true)
    }

}