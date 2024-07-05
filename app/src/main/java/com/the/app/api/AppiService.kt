package com.the.app.api

import com.the.app.models.Album
import com.the.app.models.Information
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppiService {
    @GET("albums")
    fun getAlbums(): Deferred<Response<List<Album>?>>

    @GET("photos")
    fun getAlbumInfo(@Query("albumId") id: Int): Deferred<Response<List<Information>?>>



}