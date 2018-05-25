package infinity.to.loop.betteryoutube.home.playlists

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.network.responses.PlaylistsResponse

class PlaylistAdapter(private val response: PlaylistsResponse) : RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistHolder(root)
    }

    override fun getItemCount() = response.items.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val snippet = response.items[position].snippet
        val contentDetails = response.items[position].contentDetails
        snippet?.let {
            holder.title.text = it.title
            holder.description.text = it.description
            it.thumbnails?.let {
                Glide.with(holder.title.context).load(it["default"]?.url).into(holder.thumbnails)
            }
        }

        contentDetails?.let {
            holder.trackCount.text = "${it.itemCount} Tracks"
        }
    }

    class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val thumbnails: ImageView = itemView.findViewById(R.id.thumbnail)
        val trackCount: TextView = itemView.findViewById(R.id.track_count)
    }
}