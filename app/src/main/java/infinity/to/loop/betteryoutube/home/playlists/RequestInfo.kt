package infinity.to.loop.betteryoutube.home.playlists

interface RequestInfo<T> {

    fun retrieveStatsFor(value: T, index: Int)
}