package com.the.app.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location (

    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val city: String?,
    val region: String?,
    val country: String?,

    ):Parcelable
