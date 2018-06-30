package infinity.to.loop.betteryoutube.persistance

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject


class FirebaseDb @Inject constructor(private val database: FirebaseDatabase) {

    fun updateFriends(data: Any) {
        val friends = database.getReference("friends")
        friends.child("friend1").child("currentlyPlaying").setValue(data)
    }
}