package com.practicum.playlistmaker3.playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.usecase.CreatePlaylistUseCase
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase
) : ViewModel() {

    private val _playlistName = MutableLiveData<String>("")
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData<String>("")
    val playlistDescription: LiveData<String> = _playlistDescription

    private val _coverUri = MutableLiveData<Uri?>(null)
    val coverUri: LiveData<Uri?> = _coverUri

    private val _isCreateEnabled = MutableLiveData<Boolean>(false)
    val isCreateEnabled: LiveData<Boolean> = _isCreateEnabled

    private val _creationResult = MutableLiveData<Long?>()
    val creationResult: LiveData<Long?> = _creationResult

    private val _showDiscardDialog = MutableLiveData<Boolean>(false)
    val showDiscardDialog: LiveData<Boolean> = _showDiscardDialog

    private var isDataChanged = false
    private var savedCoverPath: String? = null

    fun updateName(name: String) {
        _playlistName.value = name
        isDataChanged = true
        updateCreateButtonState()
    }

    fun updateDescription(description: String) {
        _playlistDescription.value = description
        isDataChanged = true
    }

    fun updateCover(uri: Uri?) {
        _coverUri.value = uri
        isDataChanged = true
    }

    fun setCoverPath(path: String?) {
        savedCoverPath = path
    }

    private fun updateCreateButtonState() {
        val name = _playlistName.value ?: ""
        _isCreateEnabled.value = name.isNotBlank()
    }

    fun createPlaylist() {
        val name = _playlistName.value ?: ""
        if (name.isBlank()) return

        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = _playlistDescription.value,
                coverPath = savedCoverPath
            )
            val id = createPlaylistUseCase(playlist)
            _creationResult.value = id
            isDataChanged = false
        }
    }

    fun onBackPressed(): Boolean {
        return if (isDataChanged) {
            _showDiscardDialog.value = true
            true
        } else {
            false
        }
    }

    fun onDiscardDialogConfirmed() {
        _showDiscardDialog.value = false
        isDataChanged = false
    }

    fun onDiscardDialogCancelled() {
        _showDiscardDialog.value = false
    }

    fun resetCreationResult() {
        _creationResult.value = null
    }
}