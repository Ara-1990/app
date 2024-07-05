package com.the.app.viewmodels

import android.app.Application
import androidx.lifecycle.*

import com.the.app.TransactionsState
import com.the.app.isNetworkAvailable
import com.the.app.models.Album
import com.the.app.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelLoad(app: Application) : AndroidViewModel(app) {
    private val context by lazy { app }
    private val repository: Repository by lazy { Repository(app) }

    private val listOfAlbum = MutableLiveData<List<Album>?>()

    private fun albumsLoad() {
        if (!isNetworkAvailable(context)) {
            status.value = TransactionsState.NO_NETWORK
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getServerAlbums()
                if (response.isSuccessful) {
                    val album = response.body()
                    withContext(Dispatchers.Main) {
                        listOfAlbum.value = album
                        status.value = TransactionsState.LOADING_SUCCEED
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        status.value = TransactionsState.LOADING_ERROR
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    status.value = TransactionsState.LOADING_ERROR
                }
            }
        }
    }
    private val triggerLouding = MutableLiveData<Unit>()

    fun refreshOrload() {
        triggerLouding.value = Unit
    }


    val status = MutableLiveData<TransactionsState>()

    val albums: LiveData<List<Album>?> = triggerLouding.switchMap {
        albumsLoad()
        listOfAlbum
    }
}