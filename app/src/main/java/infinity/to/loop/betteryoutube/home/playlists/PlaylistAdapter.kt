package infinity.to.loop.betteryoutube.home.playlists

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.PlaylistListResponse
import infinity.to.loop.betteryoutube.R

class PlaylistAdapter(response: PlaylistListResponse,
                      private val listener: PlaylistActionListener) : RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>(), Filterable {

    private var items: MutableList<Playlist> = response.items
    private var filteredItems: MutableList<Playlist> = response.items


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistHolder(root)
    }

    override fun getItemCount() = filteredItems.size

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val item = filteredItems[position]
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val query = charSequence.toString()

                var filtered: MutableList<Playlist> = ArrayList()

                if (query.isEmpty()) {
                    filtered = items
                } else {
                    for (playlist in items) {
                        if (playlist.snippet.title.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(playlist)
                        }
                    }
                }

                val results = Filter.FilterResults()
                results.count = filtered.size
                results.values = filtered
                return results
            }

            override fun publishResults(charSequence: CharSequence, results: Filter.FilterResults) {
                filteredItems = results.values as MutableList<Playlist>
                notifyDataSetChanged()
            }
        }
    }

    class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val thumbnails: ImageView = itemView.findViewById(R.id.thumbnail)
        val trackCount: TextView = itemView.findViewById(R.id.track_count)
    }
}