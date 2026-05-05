package com.practicum.playlistmaker3.data.models

import com.google.gson.annotations.SerializedName

data class ItunesTrack(
    @SerializedName("trackId") val trackId: Int?,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    @SerializedName("collectionName") val collectionName: String?, // Название альбома
    @SerializedName("releaseDate") val releaseDate: String?, // Дата релиза
    @SerializedName("primaryGenreName") val primaryGenreName: String?, // Жанр
    @SerializedName("country") val country: String? // Страна
)