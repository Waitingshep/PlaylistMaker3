package com.practicum.playlistmaker3

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop
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
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null

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
            if (isPlaying) {
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
            // Нет preview – блокируем кнопку
            playButton.isEnabled = false
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playButton.isEnabled = true
                // Можно также установить продолжительность, но она не нужна для отображения
            }
            setOnCompletionListener {
                // По окончании трека
                stopPlayback()
                currentTimeTextView.text = getString(R.string.default_track_time)
            }
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
        isPlaying = true
        playButton.setImageResource(R.drawable.ic_pause_button_100)
        startUpdatingTime()
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        isPlaying = false
        playButton.setImageResource(R.drawable.ic_play_button_100)
        stopUpdatingTime()
    }

    private fun stopPlayback() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            playButton.setImageResource(R.drawable.ic_play_button_100)
            stopUpdatingTime()
            mediaPlayer?.seekTo(0)
            currentTimeTextView.text = getString(R.string.default_track_time)
        }
    }

    private fun startUpdatingTime() {
        stopUpdatingTime()
        updateTimeRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val currentPosition = mp.currentPosition
                        currentTimeTextView.text = formatTime(currentPosition)
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

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    private fun getTrack(): Track? {
        return getParcelableExtraCompat<Track>(TRACK_EXTRA)
    }

    private inline fun <reified T> getParcelableExtraCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(key)
        }
    }

    private fun displayTrackInfo() {
        val track = getTrack()
        if (track == null) {
            finish()
            return
        }

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

        if (!track.collectionName.isNullOrEmpty()) {
            albumLayout.visibility = View.VISIBLE
            albumValueTextView.text = track.collectionName
        } else {
            albumLayout.visibility = View.GONE
        }

        if (!track.releaseYear.isNullOrEmpty()) {
            yearLayout.visibility = View.VISIBLE
            yearValueTextView.text = track.releaseYear
        } else {
            yearLayout.visibility = View.GONE
        }

        if (!track.primaryGenreName.isNullOrEmpty()) {
            genreLayout.visibility = View.VISIBLE
            genreValueTextView.text = track.primaryGenreName
        } else {
            genreLayout.visibility = View.GONE
        }

        if (!track.country.isNullOrEmpty()) {
            countryLayout.visibility = View.VISIBLE
            countryValueTextView.text = track.country
        } else {
            countryLayout.visibility = View.GONE
        }
    }

    private fun releaseMediaPlayer() {
        stopUpdatingTime()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        // При уходе в фон паузируем воспроизведение
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    companion object {
        const val TRACK_EXTRA = "track_extra"
    }
}