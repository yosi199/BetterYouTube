package infinity.to.loop.betteryoutube.persistance

import android.os.Parcel
import android.os.Parcelable

class CurrentlyPlaying(val videoId: String,
                       val playlistId: String?,
                       val value: String,
                       val thumbnailsUrl: String,
                       val trackTitle: String,
                       val trackAuthor: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(videoId)
        parcel.writeString(playlistId)
        parcel.writeString(value)
        parcel.writeString(thumbnailsUrl)
        parcel.writeString(trackTitle)
        parcel.writeString(trackAuthor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentlyPlaying> {
        const val TAG = "CurrentlyPlaying"

        override fun createFromParcel(parcel: Parcel): CurrentlyPlaying {
            return CurrentlyPlaying(parcel)
        }

        override fun newArray(size: Int): Array<CurrentlyPlaying?> {
            return arrayOfNulls(size)
        }
    }

}