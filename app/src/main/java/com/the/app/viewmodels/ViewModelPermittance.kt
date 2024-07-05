package com.the.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelPermittance : ViewModel() {

    private val permissionStorage by lazy { MutableLiveData<Unit>() }

    fun permissionStorageGrant() {
        permissionStorage.value = Unit
    }

    fun permissionGrantedStorage(): LiveData<Unit> = permissionStorage

    private val locationPermission by lazy { MutableLiveData<Unit>() }

    fun permissionLocation() {
        locationPermission.value = Unit
    }

    fun isLocationPermission(): LiveData<Unit> = locationPermission
}