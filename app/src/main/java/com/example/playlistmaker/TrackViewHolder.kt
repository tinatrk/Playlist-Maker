package com.example.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context).inflate(R.layout.track, parent, false)
) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtistName: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackArt: ImageView = itemView.findViewById(R.id.track_art)

    fun bind(model: Track) {
        trackName.text = model.trackName
        trackArtistName.text = model.artistName
        trackArtistName.requestLayout()
        val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        trackTime.text = time
        val cornerRadiusDp = (itemView.resources.getDimension(R.dimen.corner_radius_2)).toInt()
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusDp))
            .placeholder(R.drawable.ic_placeholder)
            .into(trackArt)
    }
}