package infinity.to.loop.betteryoutube.persistance

class CurrentlyPlaying() {
    lateinit var currentlyPlaying: String
    lateinit var value: String

    constructor(currentlyPlaying: String, value: String) : this() {
        this.currentlyPlaying = currentlyPlaying
        this.value = value
    }
}