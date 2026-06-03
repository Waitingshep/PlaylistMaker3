package com.practicum.playlistmaker3.player.domain.usecase

import com.practicum.playlistmaker3.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker3.search.domain.models.Track

class PlayTrackUseCaseImpl(
    private val repository: PlayerRepository
) : PlayTrackUseCase {

    override fun prepare(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        track.previewUrl?.let { url ->
            repository.prepare(url, onPrepared, onCompletion)
        }
    }

    override fun play() = repository.start()
    override fun pause() = repository.pause()
    override fun stop() = repository.stop()
    override fun release() = repository.release()
    override fun getCurrentPosition(): Int = repository.getCurrentPosition()
    override fun isPlaying(): Boolean = repository.isPlaying()
}