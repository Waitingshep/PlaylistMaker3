package com.practicum.playlistmaker3.player.ui

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.practicum.playlistmaker3.search.ui.TrackUi
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

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

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null
    private val dateFormat: SimpleDateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupBackButton()
        setupPlayButton()
        displayTrackInfo()
        prepareMediaPlayer()
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
            releaseMediaPlayer()
            finish()
        }
    }

    private fun setupPlayButton() {
        playButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }
    }

    private fun prepareMediaPlayer() {
        val track = getTrack()
        val previewUrl = track?.previewUrl
        if (previewUrl.isNullOrEmpty()) {
            playButton.isEnabled = false
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener { playButton.isEnabled = true }
            setOnCompletionListener {
                stopPlayback()
                currentTimeTextView.text = getString(R.string.default_track_time)
            }
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.ic_pause_button_100)
        startUpdatingTime()
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        playButton.setImageResource(R.drawable.ic_play_button_100)
        stopUpdatingTime()
    }

    private fun stopPlayback() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        playButton.setImageResource(R.drawable.ic_play_button_100)
        stopUpdatingTime()
        mediaPlayer?.seekTo(0)
        currentTimeTextView.text = getString(R.string.default_track_time)
    }

    private fun startUpdatingTime() {
        stopUpdatingTime()
        updateTimeRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        currentTimeTextView.text = formatTime(mp.currentPosition)
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }
        handler.post(updateTimeRunnable!!)
    }

    private fun stopUpdatingTime() {
        updateTimeRunnable?.let { handler.removeCallbacks(it) }
        updateTimeRunnable = null
    }

    private fun formatTime(millis: Int): String = dateFormat.format(millis)

    private fun getTrack(): TrackUi? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }
    }

    private fun displayTrackInfo() {
        val track = getTrack() ?: run { finish(); return }

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        durationValueTextView.text = track.formattedTime
        currentTimeTextView.text = getString(R.string.default_track_time)

        val cornerRadiusPx = dpToPx(8f)
        val coverUrl = track.getCoverArtwork()
        if (coverUrl.isNotEmpty()) {
            Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.placeholder_cover_312)
                .error(R.drawable.placeholder_cover_312)
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

    private fun releaseMediaPlayer() {
        stopUpdatingTime()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    private fun dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).toInt()

    companion object {
        const val TRACK_EXTRA = "track_extra"
    }
}