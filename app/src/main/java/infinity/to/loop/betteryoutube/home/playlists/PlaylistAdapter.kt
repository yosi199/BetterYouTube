package infinity.to.loop.betteryoutube.home.playlists

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.api.services.youtube.model.PlaylistListResponse
import infinity.to.loop.betteryoutube.R

class PlaylistAdapter(private val response: PlaylistListResponse,
                      private val listener: PlaylistActionListener) : RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistHolder(root)
    }

    override fun getItemCount() = response.items.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val item = response.items[position]
        val snippet = item.snippet
        val contentDetails = item.contentDetails
        snippet?.let {
            holder.title.text = it.title
            holder.description.text = it.description
            it.thumbnails?.let {
                Glide.with(holder.title.context).load(it.default.url).into(holder.thumbnails)
            }
        }

        contentDetails?.let {
            holder.trackCount.text = "${it.itemCount} Tracks"
        }

        item.id?.let { id -> holder.itemView.setOnClickListener { listener.clickedItem(id) } }
    }

    class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val thumbnails: ImageView = itemView.findViewById(R.id.thumbnail)
        val trackCount: TextView = itemView.findViewById(R.id.track_count)
    }
}