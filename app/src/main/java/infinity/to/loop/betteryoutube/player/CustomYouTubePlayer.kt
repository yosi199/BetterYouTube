package infinity.to.loop.betteryoutube.player

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubePlayerFragment

class CustomYouTubePlayer : YouTubePlayerFragment() {

    init {
        Log.d(CustomYouTubePlayer::class.java.name, "ALIVE")
    }

    companion object {
        val TAG = CustomYouTubePlayer::class.java.name
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated")

    }

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
        Log.d(TAG, "onCreate")

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")

    }

    override fun onCreateView(p0: LayoutInflater?, p1: ViewGroup?, p2: Bundle?): View {
        return super.onCreateView(p0, p1, p2)
        Log.d(TAG, "onCreateView")

    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        Log.d(TAG, "onAttachFragment")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}