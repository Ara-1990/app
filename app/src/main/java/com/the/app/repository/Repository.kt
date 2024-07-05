package com.the.app.repository

import android.content.Context
import com.the.app.api.AppiService
import com.the.app.api.Remote
import com.the.app.data.AppDao
import com.the.app.data.AppDataBase
import com.the.app.models.Album
import com.the.app.models.Information
import retrofit2.Response

class Repository(private val context: Context):RepositoryInter {

    private val apiService: AppiService by lazy { Remote.create() }
    private val appDao: AppDao by lazy { AppDataBase.getDb(context).getAppDao() }

        override suspend fun getAlbumsSaved(): List<Album>? = appDao.getAlbumsSaved()

    override suspend fun getServerAlbums(): Response<List<Album>?> =
        apiService.getAlbums().await()



    override suspend fun getInfoAlbumDb(albumId: Int): List<Information>? =
        appDao.getFromAlbumInfo(albumId)






    override suspend fun getInfoAlbumServer(albumId: Int): Response<List<Information>?> =
        apiService.getAlbumInfo(albumId).await()



    override suspend fun save(album: Album, info: List<Information>) {
        appDao.albumSave(album)
        appDao.infoSave(info)

    }

    override suspend fun remove(albumId: Int) {
        appDao.albumRemove(albumId)
        appDao.infoRemove(albumId)


    }
}