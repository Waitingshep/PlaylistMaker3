package com.practicum.playlistmaker3.search.data.dto

import com.google.gson.annotations.SerializedName

data class TrackResponseDto(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<ItunesTrackDto>
)