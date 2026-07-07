package com.practicum.playlistmaker3.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker3.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createButton = view.findViewById<Button>(R.id.createPlaylistButton)
        val emptyImage = view.findViewById<ImageView>(R.id.emptyPlaylistImage)
        val emptyText = view.findViewById<TextView>(R.id.emptyPlaylistText)

        emptyImage.visibility = View.VISIBLE
        emptyText.visibility = View.VISIBLE

        createButton.setOnClickListener {
            // TODO: обработка нажатия
        }
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}