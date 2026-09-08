package com.practicum.playlistmaker3.playlist.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker3.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: CreatePlaylistViewModel by viewModel<EditPlaylistViewModel>()

    private val editViewModel: EditPlaylistViewModel
        get() = viewModel as EditPlaylistViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playlistId = arguments?.getLong("playlistId") ?: 0
        val playlistName = arguments?.getString("playlistName") ?: ""
        val playlistDescription = arguments?.getString("playlistDescription")
        val playlistCoverPath = arguments?.getString("playlistCoverPath")
        val trackIds = arguments?.getLongArray("trackIds")?.toList() ?: emptyList()
        val trackCount = arguments?.getInt("trackCount") ?: 0

        editViewModel.setExistingTracks(trackIds, trackCount)
        editViewModel.setExistingCoverPath(playlistCoverPath)

        editViewModel.initPlaylistData(playlistId, playlistName, playlistDescription, playlistCoverPath)

        super.onViewCreated(view, savedInstanceState)

        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = getString(R.string.edit_playlist_title)

        val createButton = view.findViewById<Button>(R.id.createButton)
        createButton.text = getString(R.string.save)

        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val descriptionEditText = view.findViewById<EditText>(R.id.descriptionEditText)
        val coverImageView = view.findViewById<ImageView>(R.id.coverImageView)
        val coverIcon = view.findViewById<ImageView>(R.id.coverIcon)

        if (playlistName.isNotEmpty()) {
            nameEditText.setText(playlistName)
        }

        if (!playlistDescription.isNullOrEmpty()) {
            descriptionEditText.setText(playlistDescription)
        }

        if (!playlistCoverPath.isNullOrEmpty()) {
            Glide.with(this)
                .load(playlistCoverPath)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8)))
                .into(coverImageView)
            coverIcon.isVisible = false
            coverImageView.isVisible = true
        }

        createButton.setOnClickListener {
            editViewModel.createPlaylist()
        }
    }

    override fun handleBackPress() {
        findNavController().popBackStack()
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}