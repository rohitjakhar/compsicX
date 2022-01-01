package com.rohitjakhar.composicx

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rohitjakhar.composicx.domain.model.Music
import com.rohitjakhar.composicx.presentation.music_list.MusicListItem
import com.rohitjakhar.composicx.presentation.ui.theme.ComposicXTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mDeviceMusicList = mutableListOf<Music>()

        val pathColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME
        } else {
            MediaStore.Audio.AudioColumns.DATA
        }

        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.ARTIST, // 0
            MediaStore.Audio.AudioColumns.YEAR, // 1
            MediaStore.Audio.AudioColumns.TRACK, // 2
            MediaStore.Audio.AudioColumns.TITLE, // 3
            MediaStore.Audio.AudioColumns.DISPLAY_NAME, // 4,
            MediaStore.Audio.AudioColumns.DURATION, // 5,
            MediaStore.Audio.AudioColumns.ALBUM, // 6
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
            pathColumn, // 8
            MediaStore.Audio.AudioColumns._ID, // 9
            MediaStore.MediaColumns.DATE_MODIFIED // 10
        )
        val selection = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = 1"
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        setContent {
            ComposicXTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val songList = application.contentResolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        null,
                        sortOrder
                    )
                    songList?.use { cursor ->
                        val artistIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                        val yearIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
                        val trackIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
                        val titleIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                        val displayNameIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                        val durationIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                        val albumIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                        val albumIdIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                        val relativePathIndex =
                            cursor.getColumnIndexOrThrow(pathColumn)
                        val idIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                        val dateAddedIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)

                        val medpa = MediaPlayer()
                        while (cursor.moveToNext()) {
                            val audioId = cursor.getLong(idIndex)
                            val audioArtist = cursor.getString(artistIndex)
                            val audioYear = cursor.getInt(yearIndex)
                            val audioTrack = cursor.getInt(trackIndex)
                            val audioTitle = cursor.getString(titleIndex)
                            val audioDisplayName = cursor.getString(displayNameIndex)
                            val audioDuration = cursor.getLong(durationIndex)
                            val audioAlbum = cursor.getString(albumIndex)
                            val albumId = cursor.getLong(albumIdIndex)
                            val audioRelativePath = cursor.getString(relativePathIndex)
                            val audioDateAdded = cursor.getInt(dateAddedIndex)

                            val audioFolderName =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    audioRelativePath ?: application.getString(R.string.slash)
                                } else {
                                    val returnedPath = File(audioRelativePath).parentFile?.name
                                        ?: application.getString(R.string.slash)
                                    if (returnedPath != "0") {
                                        returnedPath
                                    } else {
                                        application.getString(
                                            R.string.slash
                                        )
                                    }
                                }
                            mDeviceMusicList.add(
                                Music(
                                    artist = audioArtist,
                                    year = audioYear,
                                    track = audioTrack,
                                    title = audioTitle,
                                    displayName = audioDisplayName,
                                    duration = audioDuration,
                                    album = audioAlbum,
                                    albumId = albumId,
                                    relativePath = audioRelativePath,
                                    id = audioId,
                                    launchedBy = "",
                                    startFrom = 0,
                                    dateAdded = audioDateAdded
                                )
                            )
                        }
                    }

                    Greeting(mDeviceMusicList)
                }
            }
        }
    }
}

@Composable
fun Greeting(musicList: List<Music>) {
    Log.d("test", "list: $musicList")
    LazyColumn(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        items(items = musicList) {music ->
            MusicListItem(music)
        }
    }
}
