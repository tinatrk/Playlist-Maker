package com.example.playlistmaker.playlists.ui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistGridBinding
import com.example.playlistmaker.playlists.domain.models.Playlist
import java.io.File
import java.io.IOException

class PlaylistViewHolderGrid(
    parent: ViewGroup, private val binding: ItemPlaylistGridBinding =
        ItemPlaylistGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        val bitmap: Bitmap? = try {
            val inputStream = File(playlist.coverPath).inputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
        val cornerRadiusDp = (itemView.resources.getDimension(R.dimen.corner_radius_8)).toInt()
        Glide.with(itemView)
            .load(bitmap)
            .placeholder(R.drawable.ic_placeholder_45)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadiusDp)))
            .into(binding.ivPlaylistCover)

        binding.tvPlaylistTitle.text = playlist.title
        binding.tvPlaylistTracksCount.text = getTrackPlurals(playlist.tracksCount)
    }

    private fun getTrackPlurals(trackCount: Int): String {
        return when {
            (trackCount % 10 == 1) ->
                "$trackCount ${itemView.resources.getString(R.string.one_track)}"

            (trackCount % 10 in 2..4) ->
                "$trackCount ${itemView.resources.getString(R.string.few_tracks)}"

            else ->
                "$trackCount ${itemView.resources.getString(R.string.other_tracks)}"
        }
    }
}