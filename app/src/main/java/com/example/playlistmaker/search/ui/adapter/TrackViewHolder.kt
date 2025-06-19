package com.example.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.search.domain.models.Track
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
        trackTime.text = getTrackTimeString(model.trackTimeMillis)
        val cornerRadiusDp = (itemView.resources.getDimension(R.dimen.corner_radius_2)).toInt()
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusDp))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(trackArt)
    }

    private fun getTrackTimeString(trackTimeMillis: Int): String =
        if (trackTimeMillis != DEFAULT_INT) {
            SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
        } else {
            DEFAULT_STRING
        }

    companion object {
        private const val TRACK_TIME_FORMAT = "mm:ss"
    }
}