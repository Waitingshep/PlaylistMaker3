package com.practicum.playlistmaker3

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupBackButton()
        displayTrackInfo()
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
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            finish()
        }
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
        val track = getParcelableExtraCompat<Track>(TRACK_EXTRA)

        if (track == null) {
            finish()
            return
        }

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        durationValueTextView.text = track.formattedTime
        currentTimeTextView.text = getString(R.string.default_track_time)  // вместо "0:00"

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

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    companion object {
        const val TRACK_EXTRA = "track_extra"
    }
}