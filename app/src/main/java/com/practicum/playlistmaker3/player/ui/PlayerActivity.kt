package com.practicum.playlistmaker3.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.ui.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupBackButton()
        setupPlayButton()
        observeViewModel()
        loadTrack()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        coverImageView = findViewById(R.id.coverImageView)
        trackNameTextView = findViewById(R.id.trackNameTextView)
        artistNameTextView = findViewById(R.id.artistNameTextView)
        durationValueTextView = findViewById(R.id.durationValueTextView)
        albumLayout = findViewById(R.id.albumLayout)
        albumValueTextView = findViewById(R.id.albumValueTextView)
        yearLayout = findViewById(R.id.yearLayout)
        yearValueTextView = findViewById(R.id.yearValueTextView)
        genreLayout = findViewById(R.id.genreLayout)
        genreValueTextView = findViewById(R.id.genreValueTextView)
        countryLayout = findViewById(R.id.countryLayout)
        countryValueTextView = findViewById(R.id.countryValueTextView)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)
        playButton = findViewById(R.id.playButton)
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            viewModel.stop()
            finish()
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

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is PlayerState.Content -> {
                    displayTrackInfo(state.track)
                    playButton.setImageResource(R.drawable.ic_play_button_100)
                    currentTimeTextView.text = getString(R.string.default_track_time)
                }
                is PlayerState.Playing -> {
                    playButton.setImageResource(R.drawable.ic_pause_button_100)
                    currentTimeTextView.text = formatTime(state.currentPosition)
                }
                is PlayerState.Paused -> {
                    playButton.setImageResource(R.drawable.ic_play_button_100)
                    currentTimeTextView.text = formatTime(state.currentPosition)
                }
                else -> {}
            }
        }
    }

    private fun loadTrack() {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }
        if (track != null) {
            viewModel.loadTrack(track)
        } else {
            finish()
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

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }

    companion object {
        const val TRACK_EXTRA = "track_extra"
    }
}