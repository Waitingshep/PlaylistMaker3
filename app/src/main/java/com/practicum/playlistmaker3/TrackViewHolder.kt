package com.practicum.playlistmaker3

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkUrl100)

    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text = track.formattedTime // Используем отформатированное время

        // Конвертируем 2dp в пиксели
        val cornerRadiusInPx = dpToPx(2f, itemView.context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_cover)
            .error(R.drawable.placeholder_cover)
            .transform(CenterCrop(), RoundedCorners(cornerRadiusInPx))
            .into(artworkImageView)
    }

    // Функция для конвертации dp в px
    private fun dpToPx(dp: Float, context: android.content.Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}