package com.rohitjakhar.composicx.domain.model

import android.net.Uri

data class Music(
    val artist: String?,
    val year: Int,
    val track: Int,
    val title: String?,
    val displayName: String?,
    val duration: Long,
    val album: String?,
    val albumId: Long?,
    val relativePath: String?,
    val id: Long?,
    val launchedBy: String,
    val startFrom: Int,
    val dateAdded: Int,
    val audioUri: Uri
)
