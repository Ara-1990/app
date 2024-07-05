package com.the.app.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.lifecycle.ViewModelProvider
import com.the.app.*
import com.the.app.adapters.AdapterLocation
import com.the.app.models.Location
import com.the.app.receiver.ReceiverLocation
import com.the.app.receiver.ReceiverStateLocation
import com.the.app.service.ServiceApp
import com.the.app.viewmodels.ViewModelPermittance
import kotlinx.android.synthetic.main.fragment_servise.*


class FragmentServise : Fragment() {

    private lateinit var permissionsViewModel: ViewModelPermittance
    private val adapter: AdapterLocation by lazy { AdapterLocation() }
    private var playing: Boolean = false
    private var locationUpdateReceiver: ReceiverLocation? = null
    private var locationStateReceiver: ReceiverStateLocation? = null
    private lateinit var activity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        permissionsViewModel = ViewModelProvider(activity).get(ViewModelPermittance::class.java)
        return inflater.inflate(R.layout.fragment_servise, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        registerReceivers()
        registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeReceivers()
    }


    private fun initView() {
        recLocationInfo.setHasFixedSize(true)
        recLocationInfo.adapter = adapter
        playing = serviceRunning(ServiceApp::class.java, requireContext())
        if (playing) showServiceRunning()
        else showServiceStopped()
        btnStartStop.setOnClickListener {
            if (playing) stop()
            else start()
        }
    }


    private fun stop() {
        val serviceIntent = Intent(requireActivity(), ServiceApp::class.java)
        requireActivity().stopService(serviceIntent)
        playing = false
        showServiceStopped()
    }

    private fun start() {
        when {
            !isPermissionGranted(requireActivity()) -> {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_CODE_PERMISSION_LOCATION
                )
            }
            !isLocationEnabled(requireContext()) -> enableLocation(requireContext())

            else -> {
                val serviceIntent = Intent(requireActivity(), ServiceApp::class.java)
                ContextCompat.startForegroundService(requireActivity(), serviceIntent)
                playing = true
                showServiceRunning()
            }
        }
    }


    private fun showLocation(info: Location?) {
        progressService.hide()
        adapter.updateLocation(getFieldsToShow(info))
    }

    private fun getFieldsToShow(info: Location?): List<Pair<String, String>>? {
        return info?.run {
            val result = mutableListOf<Pair<String, String>>()
            address?.let { result.add(Pair(getString(R.string.address), it)) }
            city?.let { result.add(Pair(getString(R.string.city), it)) }
            region?.let { result.add(Pair(getString(R.string.region), it)) }
            country?.let { result.add(Pair(getString(R.string.country), it)) }
            val latAndLong = "$latitude , $longitude"
            result.add(Pair(getString(R.string.lat_long), latAndLong))
            result
        }
    }

    private fun registerReceivers() {
        locationUpdateReceiver = ReceiverLocation { showLocation(it) }
        locationUpdateReceiver?.register(requireContext())
        locationStateReceiver = ReceiverStateLocation { start() }
        locationStateReceiver?.register(requireContext())
    }

    private fun removeReceivers() {
        locationUpdateReceiver?.unregister(requireContext())
        locationStateReceiver?.unregister(requireContext())
    }

    private fun registerObservers() {
        permissionsViewModel.isLocationPermission().observe(viewLifecycleOwner, {
            if (isLocationEnabled(requireContext())) start()
            else enableLocation(requireContext())
        })
    }

    private fun showServiceRunning() {
        lottie_play_music.visibility = View.VISIBLE
        recLocationInfo.visibility = View.VISIBLE
        btnStartStop.customize(R.string.stop, R.drawable.stop)
        progressService.show()
        tvStatusService.hide()
    }

    private fun showServiceStopped() {
        lottie_play_music.visibility = View.GONE
        recLocationInfo.visibility = View.GONE
        btnStartStop.customize(R.string.start, R.drawable.start)
        progressService.hide()
        tvStatusService.showStatus(R.string.startToGetLocation)
    }

}

