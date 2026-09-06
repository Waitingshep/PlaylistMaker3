package com.practicum.playlistmaker3.playlist.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.playlist.domain.models.Playlist

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val coverImageView: ImageView = itemView.findViewById(R.id.playlistCover)
    private val nameTextView: TextView = itemView.findViewById(R.id.playlistName)
    private val trackCountTextView: TextView = itemView.findViewById(R.id.playlistTrackCount)

    fun bind(playlist: Playlist) {
        nameTextView.text = playlist.name

        val trackCount = playlist.trackCount
        trackCountTextView.text = when (trackCount) {
            0 -> itemView.context.getString(R.string.no_tracks)
            1 -> itemView.context.getString(R.string.one_track)
            else -> itemView.context.getString(R.string.tracks_count, trackCount)
        }

        val coverPath = playlist.coverPath
        val placeholderDrawable = R.drawable.placeholder_cover_playlist

        Glide.with(itemView)
            .load(coverPath ?: placeholderDrawable)
            .placeholder(placeholderDrawable)
            .error(placeholderDrawable)
            .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
            .into(coverImageView)
    }

    private fun dpToPx(dp: Int): Int = (dp * itemView.context.resources.displayMetrics.density).toInt()
}