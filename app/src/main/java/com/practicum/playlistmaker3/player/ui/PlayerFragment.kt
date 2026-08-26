package com.practicum.playlistmaker3.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.ui.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var backButton: ImageButton
    private lateinit var coverImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var durationValueTextView: TextView
    private lateinit var albumLayout: ConstraintLayout
    private lateinit var albumValueTextView: TextView
    private lateinit var yearLayout: ConstraintLayout
    private lateinit var yearValueTextView: TextView
    private lateinit var genreLayout: ConstraintLayout
    private lateinit var genreValueTextView: TextView
    private lateinit var countryLayout: ConstraintLayout
    private lateinit var countryValueTextView: TextView
    private lateinit var currentTimeTextView: TextView
    private lateinit var playButton: ImageButton
    private lateinit var favoriteButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.backButton)
        coverImageView = view.findViewById(R.id.coverImageView)
        trackNameTextView = view.findViewById(R.id.trackNameTextView)
        artistNameTextView = view.findViewById(R.id.artistNameTextView)
        durationValueTextView = view.findViewById(R.id.durationValueTextView)
        albumLayout = view.findViewById(R.id.albumLayout)
        albumValueTextView = view.findViewById(R.id.albumValueTextView)
        yearLayout = view.findViewById(R.id.yearLayout)
        yearValueTextView = view.findViewById(R.id.yearValueTextView)
        genreLayout = view.findViewById(R.id.genreLayout)
        genreValueTextView = view.findViewById(R.id.genreValueTextView)
        countryLayout = view.findViewById(R.id.countryLayout)
        countryValueTextView = view.findViewById(R.id.countryValueTextView)
        currentTimeTextView = view.findViewById(R.id.currentTimeTextView)
        playButton = view.findViewById(R.id.playButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)

        setupBackButton()
        setupPlayButton()
        setupFavoriteButton()
        observeViewModel()
        loadTrack()
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            viewModel.stop()
            findNavController().popBackStack()
        }
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            val state = viewModel.state.value
            when (state) {
                is PlayerState.Playing -> viewModel.pause()
                is PlayerState.Paused -> viewModel.play()
                is PlayerState.Content -> viewModel.play()
                else -> {}
            }
        }
    }

    private fun setupFavoriteButton() {
        favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled_51)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_51)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayerState.Content -> {
                    displayTrackInfo(state.track)
                    playButton.setImageResource(R.drawable.ic_play_button_100)
                    currentTimeTextView.text = getString(R.string.default_track_time)
                    updateFavoriteButton(state.isFavorite)
                }
                is PlayerState.Playing -> {
                    displayTrackInfo(state.track)
                    playButton.setImageResource(R.drawable.ic_pause_button_100)
                    currentTimeTextView.text = formatTime(state.currentPosition)
                    updateFavoriteButton(state.isFavorite)
                }
                is PlayerState.Paused -> {
                    displayTrackInfo(state.track)
                    playButton.setImageResource(R.drawable.ic_play_button_100)
                    currentTimeTextView.text = formatTime(state.currentPosition)
                    updateFavoriteButton(state.isFavorite)
                }
                else -> {}
            }
        }
    }

    private fun loadTrack() {
        val args = arguments?.getParcelable<TrackUi>("track")
        android.util.Log.d("PlayerFragment", "Track from arguments: isFavorite = ${args?.isFavorite}")
        if (args != null) {
            viewModel.loadTrack(args)
        }
    }

    private fun displayTrackInfo(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        durationValueTextView.text = track.formattedTime

        val cornerRadiusPx = dpToPx(8f)
        val coverUrl = track.getCoverArtwork()

        if (coverUrl.isNotEmpty()) {
            Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.placeholder_cover_312)
                .error(R.drawable.placeholder_cover_312)
                .fallback(R.drawable.placeholder_cover_312)
                .transform(CenterCrop(), RoundedCorners(cornerRadiusPx))
                .into(coverImageView)
        } else {
            Glide.with(this)
                .load(R.drawable.placeholder_cover_312)
                .into(coverImageView)
        }

        albumLayout.visibility = if (!track.collectionName.isNullOrEmpty()) {
            albumValueTextView.text = track.collectionName
            View.VISIBLE
        } else View.GONE

        yearLayout.visibility = if (!track.releaseYear.isNullOrEmpty()) {
            yearValueTextView.text = track.releaseYear
            View.VISIBLE
        } else View.GONE

        genreLayout.visibility = if (!track.primaryGenreName.isNullOrEmpty()) {
            genreValueTextView.text = track.primaryGenreName
            View.VISIBLE
        } else View.GONE

        countryLayout.visibility = if (!track.country.isNullOrEmpty()) {
            countryValueTextView.text = track.country
            View.VISIBLE
        } else View.GONE
    }

    private fun formatTime(millis: Int): String {
        return String.format("%02d:%02d", millis / 1000 / 60, millis / 1000 % 60)
    }

    private fun dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).toInt()
}