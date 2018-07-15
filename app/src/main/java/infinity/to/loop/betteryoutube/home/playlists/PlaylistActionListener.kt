package infinity.to.loop.betteryoutube.home.playlists

interface PlaylistActionListener<T> {
    fun clickedItem(item: T, index: Int)
}