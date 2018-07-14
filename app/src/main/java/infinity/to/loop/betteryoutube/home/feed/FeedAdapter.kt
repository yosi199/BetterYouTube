package infinity.to.loop.betteryoutube.home.feed

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.persistance.FriendsCurrentlyPlaying

class FeedAdapter(private val items: FriendsCurrentlyPlaying) : RecyclerView.Adapter<FeedAdapter.FeedHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return FeedHolder(root)
    }

    override fun getItemCount() = items.subscriptionSnippet.size

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        holder.name.text = items.subscriptionSnippet[position].title
        holder.currentlyPlaying.text = items.currentlyPlaying[position].currentlyPlaying
    }


    class FeedHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name: TextView = item.findViewById(R.id.feed_user_name)
        val currentlyPlaying: TextView = item.findViewById(R.id.feed_currently_playing)
    }
}