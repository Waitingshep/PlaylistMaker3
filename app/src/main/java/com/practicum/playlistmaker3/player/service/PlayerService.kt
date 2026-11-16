package com.practicum.playlistmaker3.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCase
import com.practicum.playlistmaker3.player.ui.PlayerState
import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerService : Service(), PlayerController, KoinComponent {

    companion object {
        private const val CHANNEL_ID = "player_channel"
        private const val NOTIFICATION_ID = 1
        private const val UPDATE_INTERVAL_MS = 300L // Вынесенная константа для задержки

        // Константы для ключей Intent
        const val EXTRA_TRACK_ID = "trackId"
        const val EXTRA_TRACK_NAME = "trackName"
        const val EXTRA_ARTIST_NAME = "artistName"
        const val EXTRA_TRACK_TIME_MILLIS = "trackTimeMillis"
        const val EXTRA_ARTWORK_URL_100 = "artworkUrl100"
        const val EXTRA_COLLECTION_NAME = "collectionName"
        const val EXTRA_RELEASE_DATE = "releaseDate"
        const val EXTRA_PRIMARY_GENRE_NAME = "primaryGenreName"
        const val EXTRA_COUNTRY = "country"
        const val EXTRA_PREVIEW_URL = "previewUrl"
        const val EXTRA_IS_FAVORITE = "isFavorite"
    }

    private val playTrackUseCase: PlayTrackUseCase by inject()

    private val binder = PlayerBinder()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _state = MutableStateFlow<PlayerState?>(null)
    override fun getState(): StateFlow<PlayerState?> = _state.asStateFlow()

    private var currentTrack: Track? = null
    private var isPrepared = false
    private var pendingPlay = false
    private var isCompleted = false
    private var isFavorite = false
    private var updateJob: Job? = null
    private var isForeground = false

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            val trackId = it.getLongExtra(EXTRA_TRACK_ID, 0L)
            val trackName = it.getStringExtra(EXTRA_TRACK_NAME) ?: ""
            val artistName = it.getStringExtra(EXTRA_ARTIST_NAME) ?: ""
            val previewUrl = it.getStringExtra(EXTRA_PREVIEW_URL)
            val isFavoriteExtra = it.getBooleanExtra(EXTRA_IS_FAVORITE, false)

            currentTrack = Track(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                trackTimeMillis = it.getLongExtra(EXTRA_TRACK_TIME_MILLIS, 0L),
                artworkUrl100 = it.getStringExtra(EXTRA_ARTWORK_URL_100) ?: "",
                collectionName = it.getStringExtra(EXTRA_COLLECTION_NAME),
                releaseDate = it.getStringExtra(EXTRA_RELEASE_DATE),
                primaryGenreName = it.getStringExtra(EXTRA_PRIMARY_GENRE_NAME),
                country = it.getStringExtra(EXTRA_COUNTRY),
                previewUrl = previewUrl,
                isFavorite = isFavoriteExtra
            )
            isFavorite = isFavoriteExtra
            prepareTrack()
        }
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun prepareTrack() {
        val track = currentTrack ?: return
        isPrepared = false
        isCompleted = false

        playTrackUseCase.release()
        playTrackUseCase.prepare(track,
            onPrepared = {
                isPrepared = true
                isCompleted = false
                if (pendingPlay) {
                    pendingPlay = false
                    play()
                } else {
                    _state.value = PlayerState.Content(track, isFavorite)
                }
            },
            onCompletion = {
                stopUpdating()
                isPrepared = false
                isCompleted = true
                pendingPlay = false
                _state.value = PlayerState.Content(track, isFavorite)
                if (isForeground) {
                    hideNotification()
                }
            }
        )
    }

    override fun play() {
        val track = currentTrack ?: return
        if (!isPrepared) {
            if (isCompleted) {
                pendingPlay = true
                prepareTrack()
            } else {
                pendingPlay = true
            }
            return
        }
        playTrackUseCase.play()
        startUpdating()
        _state.value = PlayerState.Playing(track, playTrackUseCase.getCurrentPosition(), isFavorite)
    }

    override fun pause() {
        val track = currentTrack ?: return
        pendingPlay = false
        playTrackUseCase.pause()
        stopUpdating()
        val position = playTrackUseCase.getCurrentPosition()
        _state.value = PlayerState.Paused(track, position, isFavorite)
    }

    override fun stop() {
        pendingPlay = false
        isCompleted = false
        playTrackUseCase.stop()
        stopUpdating()
        isPrepared = false
        currentTrack?.let { track ->
            _state.value = PlayerState.Content(track, isFavorite)
        }
        if (isForeground) {
            hideNotification()
        }
    }

    private fun startUpdating() {
        stopUpdating()
        updateJob = scope.launch {
            // Явное условие выхода из цикла
            while (updateJob?.isActive == true) {
                delay(UPDATE_INTERVAL_MS)
                currentTrack?.let { track ->
                    if (playTrackUseCase.isPlaying()) {
                        val position = playTrackUseCase.getCurrentPosition()
                        _state.value = PlayerState.Playing(track, position, isFavorite)
                    } else {
                        stopUpdating()
                        _state.value = PlayerState.Content(track, isFavorite)
                        playTrackUseCase.stop()
                        if (isForeground) {
                            hideNotification()
                        }
                    }
                }
            }
        }
    }

    private fun stopUpdating() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun showNotification() {
        val track = currentTrack ?: return
        val notification = createNotification(track)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            }
        )
        isForeground = true
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isForeground = false
    }

    private fun createNotification(track: Track): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("${track.artistName} - ${track.trackName}")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdating()
        playTrackUseCase.release()
        scope.cancel()
        if (isForeground) {
            hideNotification()
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stop()
        return super.onUnbind(intent)
    }
}