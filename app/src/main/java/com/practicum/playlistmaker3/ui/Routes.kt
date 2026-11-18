package com.practicum.playlistmaker3.ui

object Routes {
    const val SEARCH = "search"
    const val MEDIA = "media"
    const val SETTINGS = "settings"
    const val PLAYER = "player"
    const val CREATE_PLAYLIST = "create_playlist"
    const val PLAYLIST = "playlist"
    const val EDIT_PLAYLIST = "edit_playlist"

    fun playlist(playlistId: Long) = "$PLAYLIST/$playlistId"
    fun editPlaylist(playlistId: Long) = "$EDIT_PLAYLIST/$playlistId"
}