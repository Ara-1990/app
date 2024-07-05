package com.the.app.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.the.app.INTERVAL_LOCATION_REQUEST
import java.io.IOException
import java.util.*

class ProviderLocation (private val context: Context) {

    private val locationRequest: LocationRequest = LocationRequest.create()
        .setInterval(INTERVAL_LOCATION_REQUEST)
        .setFastestInterval(500)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    @SuppressLint("MissingPermission")
    fun getDeviceLocationForGrantedPermissions(onLocationReceiver: (com.the.app.models.Location) -> Unit) {

        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)

                    locationResult?.run {
                        for (loc in locations) {
                            loc?.let {
                                val address = getAddressLocation(it)
                                address?.run {
                                    val streetAdd: String? = try {
                                        getAddressLine(0).split(",")[0]
                                    } catch (ex: Exception) {
                                        null
                                    }
                                    onLocationReceiver(
                                        com.the.app.models.Location(
                                            latitude,
                                            longitude,
                                            streetAdd,
                                            locality,
                                            adminArea,
                                            countryName
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }, null)
    }

    private fun getAddressLocation(location: Location): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.get(0)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}