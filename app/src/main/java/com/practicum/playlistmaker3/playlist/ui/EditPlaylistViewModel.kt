package com.practicum.playlistmaker3.playlist.ui

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.usecase.CreatePlaylistUseCase
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase
) : CreatePlaylistViewModel(createPlaylistUseCase) {

    private var existingTrackIds: List<Long> = emptyList()
    private var existingTrackCount: Int = 0
    private var existingCoverPath: String? = null

    fun setExistingTracks(trackIds: List<Long>, trackCount: Int) {
        existingTrackIds = trackIds
        existingTrackCount = trackCount
    }

    fun setExistingCoverPath(coverPath: String?) {
        existingCoverPath = coverPath
    }

    override fun createPlaylist() {
        val currentState = _state.value ?: return
        val name = currentState.name
        if (name.isBlank()) return

        val coverPathToSave = if (currentState.coverPath.isNullOrEmpty()) {
            existingCoverPath
        } else {
            currentState.coverPath
        }

        coroutineScope.launch {
            val playlist = Playlist(
                id = currentState.playlistId ?: 0,
                name = name,
                description = currentState.description,
                coverPath = coverPathToSave,
                trackIds = existingTrackIds,
                trackCount = existingTrackCount
            )
            val id = createPlaylistUseCase(playlist)
            _state.value = _state.value?.copy(
                creationResult = id,
                isDataChanged = false
            )
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}