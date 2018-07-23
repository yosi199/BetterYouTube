package infinity.to.loop.betteryoutube.home.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.home.playlists.PlaylistActionListener

class SearchAdapter(private val listener: PlaylistActionListener<SearchResult>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var items: MutableList<SearchResult> = mutableListOf()

    fun addData(playlist: SearchListResponse) {
        this.items = playlist.items
        notifyItemRangeInserted(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return SearchAdapter.ViewHolder(root)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.snippet.title
        holder.description.text = item.snippet.description

        Glide.with(holder.thumbnails).load(item.snippet.thumbnails.default.url).into(holder.thumbnails)

        holder.itemView.setOnClickListener { listener.clickedItem(item, position) }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val thumbnails: ImageView = itemView.findViewById(R.id.thumbnail)
    }
}