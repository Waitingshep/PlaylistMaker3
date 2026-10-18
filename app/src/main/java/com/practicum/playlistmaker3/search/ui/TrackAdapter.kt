package com.practicum.playlistmaker3.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.R

class TrackAdapter(
    private var tracks: List<TrackUi>,
    private val onItemClick: (TrackUi) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var onLongClickListener: ((TrackUi) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClick(track)
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.invoke(track)
            true
        }
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<TrackUi>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    fun setOnLongClickListener(listener: (TrackUi) -> Unit) {
        onLongClickListener = listener
    }
}