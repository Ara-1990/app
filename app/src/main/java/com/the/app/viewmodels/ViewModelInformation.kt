package com.the.app.viewmodels

import android.app.Application
import androidx.lifecycle.*

import com.the.app.TransactionsState
import com.the.app.isNetworkAvailable
import com.the.app.models.Album
import com.the.app.models.Information
import com.the.app.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelInformation (app: Application) : AndroidViewModel(app) {

    private val context by lazy { app }
    private val repository: Repository by lazy { Repository(app) }

    private val SavedLoadedInfo by lazy { MutableLiveData<Pair<Boolean, List<Information>?>>() }
    val status by lazy { MutableLiveData<TransactionsState>() }

    private val isOnLoad by lazy { MutableLiveData<Pair<Boolean, Int>>() }

    fun loading(albumId: Int) {
        isOnLoad.value = Pair(false, albumId)
    }

    fun refreshing(albumId: Int) {
        isOnLoad.value = Pair(true, albumId)
    }

    private fun infoLoad(onlyNew: Boolean, albumId: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            if (!onlyNew) {
                val infoFromDb = repository.getInfoAlbumDb(albumId)
                infoFromDb?.let {
                    if (it.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            status.value = TransactionsState.LOADING_SUCCEED
                            SavedLoadedInfo.value = Pair(true, it)
                        }
                        return@launch
                    }
                }
            }



            withContext(Dispatchers.Main) {
                if (!isNetworkAvailable(context)) {
                    status.value = TransactionsState.NO_NETWORK
                    return@withContext
                } else {
                    withContext(Dispatchers.IO) {
                        try {
                            val response = repository.getInfoAlbumServer(albumId)
                            if (response.isSuccessful) {
                                val info = response.body()
                                withContext(Dispatchers.Main) {
                                    status.value = TransactionsState.LOADING_SUCCEED
                                    SavedLoadedInfo.value = Pair(false, info)
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
            }
        }
    }

    fun save(album: Album, info: List<Information>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.save(album, info)
        }
    }

    val infoAlbum: LiveData<Pair<Boolean, List<Information>?>> = isOnLoad.switchMap {
        infoLoad(it.first, it.second)
        SavedLoadedInfo
    }

    fun remove(albumId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.remove(albumId)
                withContext(Dispatchers.Main) {
                    status.value = TransactionsState.REMOVING_SUCCEED
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    status.value = TransactionsState.REMOVING_FAILED
                }
            }
        }
    }

}