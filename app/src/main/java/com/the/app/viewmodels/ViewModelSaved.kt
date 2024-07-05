package com.the.app.viewmodels

import android.app.Application
import androidx.lifecycle.*

import com.the.app.TransactionsState
import com.the.app.models.Album
import com.the.app.repository.Repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelSaved (application: Application) : AndroidViewModel(application) {

    private val repository: Repository by lazy { Repository(application) }

    private val listOfAlbum = MutableLiveData<List<Album>?>()

    private fun albumsLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val saved = repository.getAlbumsSaved()
                withContext(Dispatchers.Main) {
                    listOfAlbum.value = saved
                    status.value = TransactionsState.LOADING_SUCCEED
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    status.value = TransactionsState.LOADING_ERROR
                }
            }
        }
    }

    fun loading() {
        triggerOfRefresh.value = Unit
    }

    val status = MutableLiveData<TransactionsState>()
    fun removeing(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.remove(id)
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

    private val triggerOfRefresh = MutableLiveData<Unit>()

    val albums: LiveData<List<Album>?> = triggerOfRefresh.switchMap {
        albumsLoad()
        listOfAlbum
    }


}