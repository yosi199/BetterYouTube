package infinity.to.loop.betteryoutube.home.playlists.playlist.item

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.api.services.youtube.model.PlaylistItem
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.home.playlists.PlaylistActionListener
import infinity.to.loop.betteryoutube.home.playlists.RequestInfo

class SpecificPlaylistAdapter(private val listener: PlaylistActionListener<TrackItemInfo>, private val requestInfoListener: RequestInfo<TrackItemInfo>) : RecyclerView.Adapter<SpecificPlaylistAdapter.ViewHolder>(), Filterable {

    private var items: MutableList<TrackItemInfo> = mutableListOf()
    private var filteredItems: MutableList<TrackItemInfo> = mutableListOf()

    companion object {
        const val ORIGINAL_HEIGHT = 300
        const val NEW_HEIGHT = 400
    }

    fun addData(data: MutableList<TrackItemInfo>) {
        this.items = data
        this.filteredItems = items
        notifyDataSetChanged()
    }

    fun updateStatsForItem(trackInfo: TrackItemInfo, index: Int) {
        if (filteredItems.size > index) {
            filteredItems[index] = trackInfo
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return ViewHolder(root)
    }

    override fun getItemCount() = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value = filteredItems[holder.adapterPosition]
        holder.title.text = value.item.snippet.title
        holder.description.text = value.item.snippet.description
        holder.duration.text = value.item.contentDetails.endAt

        Glide.with(holder.thumbnails).load(value.item.snippet.thumbnails.standard.url).into(holder.thumbnails)

        holder.itemView.setOnClickListener { listener.clickedItem(value, holder.adapterPosition) }

        holder.showStats.setOnClickListener {
            value.showStats = !value.showStats
            requestInfoListener.retrieveStatsFor(value, holder.adapterPosition)
        }

        adjustHeight(value.showStats, holder)
        adjustStats(value.showStats, holder, value)
    }

    private fun adjustStats(showStats: Boolean, holder: ViewHolder, value: TrackItemInfo) {
        if (showStats) {
            holder.showStats.visibility = View.GONE
            holder.views.visibility = View.VISIBLE
            holder.likes.visibility = View.VISIBLE
            holder.comments.visibility = View.VISIBLE
            holder.views.text = holder.views.resources.getString(R.string.views, value.views)
            holder.likes.text = holder.likes.resources.getString(R.string.likes, value.likes)
            holder.comments.text = holder.comments.resources.getString(R.string.comments, value.comments)

        } else {
            holder.showStats.text = holder.description.context.getString(R.string.show_stats)
            holder.views.visibility = View.GONE
            holder.likes.visibility = View.GONE
            holder.comments.visibility = View.GONE
        }
    }

    private fun adjustHeight(showStats: Boolean, holder: ViewHolder) {
        val params = holder.itemView.layoutParams
        if (showStats) {
            holder.statsLayout.visibility = View.VISIBLE
            params.height = NEW_HEIGHT
        } else {
            params.height = ORIGINAL_HEIGHT
        }
        holder.itemView.layoutParams = params
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val query = charSequence.toString()

                var filtered: MutableList<TrackItemInfo> = ArrayList()

                if (query.isEmpty()) {
                    filtered = items
                } else {
                    for (video in items) {
                        if (video.item.snippet.title.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(video)
                        }
                    }
                }

                val results = Filter.FilterResults()
                results.count = filtered.size
                results.values = filtered
                return results
            }

            override fun publishResults(charSequence: CharSequence, results: Filter.FilterResults) {
                filteredItems = results.values as MutableList<TrackItemInfo>
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val thumbnails: ImageView = itemView.findViewById(R.id.thumbnail)
        val showStats: TextView = itemView.findViewById(R.id.show_stats)
        val likes: TextView = itemView.findViewById(R.id.likes_count)
        val comments: TextView = itemView.findViewById(R.id.comments_count)
        val views: TextView = itemView.findViewById(R.id.view_count)
        val statsLayout: LinearLayout = itemView.findViewById(R.id.stats_layout)
    }
}