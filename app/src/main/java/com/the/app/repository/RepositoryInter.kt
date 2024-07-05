package com.the.app.repository

import com.the.app.models.Album
import com.the.app.models.Information
import retrofit2.Response

interface RepositoryInter {
    suspend fun getAlbumsSaved(): List<Album>?
    suspend fun getServerAlbums(): Response<List<Album>?>
    suspend fun getInfoAlbumDb(albumId: Int): List<Information>?
    suspend fun getInfoAlbumServer(albumId: Int): Response<List<Information>?>
    suspend fun save(album: Album, info: List<Information>)
    suspend fun remove(albumId: Int)
}