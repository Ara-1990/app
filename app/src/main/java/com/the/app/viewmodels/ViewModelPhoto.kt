package com.the.app.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.the.app.TransactionsState
import com.the.app.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ViewModelPhoto (app: Application) : AndroidViewModel(app) {

    private val context by lazy { app }
    private val managerDownload by lazy { app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
    private val directory by lazy { File(Environment.DIRECTORY_PICTURES) }
    val status by lazy { MutableLiveData<TransactionsState>() }

    private fun request(url: String): DownloadManager.Request {
        if (!directory.exists()) directory.mkdirs()
        val downloadUri = Uri.parse(url)
        return DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }
    }

    @SuppressLint("Range")
    fun imageDownload(url: String) {
        if (!isNetworkAvailable(context)) {
            status.value = TransactionsState.NO_NETWORK
        } else {
            viewModelScope.launch(Dispatchers.IO) {

                val idOfdownload = managerDownload.enqueue(request(url))
                val query = DownloadManager.Query().setFilterById(idOfdownload)
                var download = true
                while (download) {
                    val cursor: Cursor = managerDownload.query(query)
                    cursor.moveToFirst()
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED)

                    {
                        download = false
                        withContext(Dispatchers.Main) {
                            status.value = TransactionsState.DOWNLOADING_FAILED
                        }
                    }
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        download = false
                        withContext(Dispatchers.Main) {
                            status.value = TransactionsState.DOWNLOADING_SUCCEED
                        }
                    }
                    cursor.close()
                }
            }
        }
    }

}