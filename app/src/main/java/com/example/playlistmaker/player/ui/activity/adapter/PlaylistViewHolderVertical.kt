package com.example.playlistmaker.player.ui.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistVerticalBinding
import com.example.playlistmaker.playlists.domain.models.Playlist

class PlaylistViewHolderVertical(
    parent: ViewGroup, private val binding: ItemPlaylistVerticalBinding =
        ItemPlaylistVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        val cornerRadiusDp = (itemView.resources.getDimension(R.dimen.corner_radius_2)).toInt()
        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(R.drawable.ic_placeholder_45)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadiusDp)))
            .into(binding.ivPlaylistCover)

        binding.tvPlaylistTitle.text = playlist.title
        binding.tvPlaylistTracksCount.text = getTrackPlurals(playlist.tracksCount)
    }

    private fun getTrackPlurals(trackCount: Int): String {
        return when {
            (trackCount % 10 == 1) ->
                "$trackCount ${itemView.resources.getString(R.string.playlist_one_track)}"

            (trackCount % 10 in 2..4) ->
                "$trackCount ${itemView.resources.getString(R.string.playlist_few_tracks)}"

            else ->
                "$trackCount ${itemView.resources.getString(R.string.playlist_other_tracks)}"
        }
    }
}