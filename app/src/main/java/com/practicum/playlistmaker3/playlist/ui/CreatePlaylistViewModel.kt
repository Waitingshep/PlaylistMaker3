package com.practicum.playlistmaker3.playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.usecase.CreatePlaylistUseCase
import kotlinx.coroutines.launch

data class CreatePlaylistState(
    val name: String = "",
    val description: String = "",
    val coverUri: Uri? = null,
    val coverPath: String? = null,
    val isCreateEnabled: Boolean = false,
    val creationResult: Long? = null,
    val showDiscardDialog: Boolean = false,
    val isDataChanged: Boolean = false,
    val playlistId: Long? = null,
    val isEditMode: Boolean = false
)

open class CreatePlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase
) : ViewModel() {

    protected val _state = MutableLiveData(CreatePlaylistState())
    val state: LiveData<CreatePlaylistState> = _state

    protected val coroutineScope = viewModelScope

    fun updateName(name: String) {
        _state.value = _state.value?.copy(
            name = name,
            isDataChanged = true,
            isCreateEnabled = name.isNotBlank()
        )
    }

    fun updateDescription(description: String) {
        _state.value = _state.value?.copy(
            description = description,
            isDataChanged = true
        )
    }

    fun updateCover(uri: Uri?) {
        _state.value = _state.value?.copy(
            coverUri = uri,
            isDataChanged = true
        )
    }

    fun setCoverPath(path: String?) {
        _state.value = _state.value?.copy(coverPath = path)
    }

    fun setPlaylistId(id: Long) {
        _state.value = _state.value?.copy(playlistId = id)
    }

    open fun initPlaylistData(playlistId: Long, name: String, description: String?, coverPath: String?) {
        _state.value = _state.value?.copy(
            playlistId = playlistId,
            name = name,
            description = description ?: "",
            coverPath = coverPath,
            isCreateEnabled = name.isNotBlank(),
            isDataChanged = false,
            isEditMode = playlistId > 0
        )
    }

    open fun createPlaylist() {
        val currentState = _state.value ?: return
        val name = currentState.name
        if (name.isBlank()) return

        coroutineScope.launch {
            val playlist = Playlist(
                id = currentState.playlistId ?: 0,
                name = name,
                description = currentState.description,
                coverPath = currentState.coverPath
            )
            val id = createPlaylistUseCase(playlist)
            _state.value = _state.value?.copy(
                creationResult = id,
                isDataChanged = false
            )
        }
    }

    open fun onBackPressed(): Boolean {
        val currentState = _state.value ?: return false
        return if (currentState.isDataChanged) {
            _state.value = currentState.copy(showDiscardDialog = true)
            true
        } else {
            false
        }
    }

    fun onDiscardDialogConfirmed() {
        _state.value = _state.value?.copy(
            showDiscardDialog = false,
            isDataChanged = false
        )
    }

    fun onDiscardDialogCancelled() {
        _state.value = _state.value?.copy(showDiscardDialog = false)
    }

    fun resetCreationResult() {
        _state.value = _state.value?.copy(creationResult = null)
    }
}