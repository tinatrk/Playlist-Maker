package com.example.playlistmaker.playlists.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlists.domain.models.Playlist

class PlaylistAdapterGrid(
    private val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<PlaylistViewHolderGrid>() {
    private val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolderGrid {
        return PlaylistViewHolderGrid(parent)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolderGrid, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            onClickListener.onClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        this.notifyDataSetChanged()
    }

    fun interface OnClickListener {
        fun onClick(playlist: Playlist)
    }
}