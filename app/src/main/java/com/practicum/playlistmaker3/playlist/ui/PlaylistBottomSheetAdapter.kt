package com.practicum.playlistmaker3.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.playlist.domain.models.Playlist

class PlaylistBottomSheetAdapter(
    private var playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
        return PlaylistBottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}