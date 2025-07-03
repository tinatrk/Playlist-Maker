package com.example.playlistmaker.playlists.ui.fragment.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlists.domain.models.Playlist

class PlaylistAdapterGrid(private val onLongClickListener: OnLongClickListener) :
    RecyclerView.Adapter<PlaylistViewHolderGrid>() {
    private val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolderGrid {
        return PlaylistViewHolderGrid(parent)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolderGrid, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnLongClickListener {
            onLongClickListener.onLongClick(holder.itemView, playlists[position])
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

    fun interface OnLongClickListener {
        fun onLongClick(view: View, playlist: Playlist): Boolean
    }
}