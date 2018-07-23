package infinity.to.loop.betteryoutube.persistance

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject


class FirebaseDb @Inject constructor(private val database: FirebaseDatabase) {

    private var friendsDB: String = "friends"
    private lateinit var currentUserId: String

    fun setUserId(id: String) {
        this.currentUserId = id
    }

    fun removeSelf() {
        val friends = database.getReference(friendsDB)
        friends.child(currentUserId).removeValue()
    }

    fun updateCurrentlyPlaying(data: CurrentlyPlaying) {
        val friends = database.getReference(friendsDB)
        friends.child(currentUserId).setValue(data)
    }

    fun registerEventListener(eventListener: ValueEventListener) {
        val friends = database.getReference(friendsDB)
        friends.addValueEventListener(eventListener)
    }

    fun unregisterEventLisetner(eventListener: ValueEventListener) {
        val friends = database.getReference(friendsDB)
        friends.removeEventListener(eventListener)
    }


}