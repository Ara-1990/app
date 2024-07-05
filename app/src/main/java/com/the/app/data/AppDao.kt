package com.the.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.the.app.models.Album
import com.the.app.models.Information

@Dao
interface AppDao {

    @Query("SELECT * FROM albums")
    suspend fun getAlbumsSaved(): List<Album>?

    @Query("SELECT * FROM information WHERE albumId =:albumId")
    suspend fun getFromAlbumInfo(albumId: Int): List<Information>?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun albumSave(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun infoSave(info: List<Information>)

    @Query("DELETE FROM ALBUMS WHERE ID =:id")
    suspend fun albumRemove(id: Int)

    @Query("DELETE FROM information WHERE albumId =:albumId")
    suspend fun infoRemove(albumId: Int)

}