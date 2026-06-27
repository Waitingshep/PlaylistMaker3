package com.practicum.playlistmaker3.search.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker3.R

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkUrl100)

    fun bind(track: TrackUi) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text = track.formattedTime

        val cornerRadiusInPx = dpToPx(2f, itemView.context)
        val imageUrl = track.getCoverArtwork()

        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_cover_312)
            .error(R.drawable.placeholder_cover_312)
            .fallback(R.drawable.placeholder_cover_312)
            .transform(CenterCrop(), RoundedCorners(cornerRadiusInPx))
            .into(artworkImageView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}