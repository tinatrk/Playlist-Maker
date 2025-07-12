package com.example.playlistmaker.playlists.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapter.TrackViewHolder

class OnePlaylistTrackAdapter(
    private val onTackClickListener: OnClickListener,
    private val onTrackLongClickListener: OnLongClickListener
) :
    RecyclerView.Adapter<TrackViewHolder>() {
    private val tracks: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onTackClickListener.onClick(tracks[position])
        }

        holder.itemView.setOnLongClickListener {
            onTrackLongClickListener.onLongClick(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        this.notifyDataSetChanged()
    }

    fun interface OnClickListener {
        fun onClick(track: Track)
    }

    fun interface OnLongClickListener {
        fun onLongClick(track: Track): Boolean
    }

}